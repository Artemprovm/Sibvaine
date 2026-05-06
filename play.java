import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.swing.*;

public class play extends JPanel implements ActionListener {
    private BufferedImage floor1, floor2, floor3, ggf1, ggf2, playerImage, factoryImage;
    
    private final int TILE_SIZE = 32;  
    private final int PLAYER_SIZE = 40; 
    
    private double playerX = 500, playerY = 500; 
    private double angle = 0;
    private final double SPEED = 4.0;
    
    private final int WORLD_WIDTH = 100;
    private final int WORLD_HEIGHT = 100;

    private BuildMenu buildMenu = new BuildMenu();
    private int selectedBuild = -1; // -1 = ничего, 0 = завод 2x2
    private ArrayList<Rectangle> buildings = new ArrayList<>(); // Храним тут заводы

    private final Set<Integer> pressedKeys = new HashSet<>();
    private ArrayList<Point> waterPlants = new ArrayList<>(); 
    private ArrayList<Point> silicon = new ArrayList<>();    
    private ArrayList<Point> basalt = new ArrayList<>();     
    private ArrayList<Point> iron = new ArrayList<>();       

    public play() {
        setBackground(Color.BLACK); 

        try {
            floor1 = ImageIO.read(new File("floor1.png"));
            floor2 = ImageIO.read(new File("floor2.png"));
            floor3 = ImageIO.read(new File("floor3.png"));
            ggf1 = ImageIO.read(new File("ggf1.png"));
            ggf2 = ImageIO.read(new File("ggf2.png"));
            playerImage = ImageIO.read(new File("player.png"));
            // Если файла нет, пока будет просто рисоваться серый квадрат
            File f = new File("factory.png");
            if(f.exists()) factoryImage = ImageIO.read(f);
            
            generateWorld();
        } catch (IOException e) {
            System.out.println("ОШИБКА: " + e.getMessage());
        }

        Timer timer = new Timer(16, this);
        timer.start();

        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) { pressedKeys.add(e.getKeyCode()); }
            public void keyReleased(KeyEvent e) { pressedKeys.remove(e.getKeyCode()); }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int cell = buildMenu.getClickedCell(e.getX(), e.getY(), getWidth());
                if (cell != -1) {
                    selectedBuild = cell;
                } else if (selectedBuild != -1) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        int camX = (int)playerX - getWidth() / 2;
                        int camY = (int)playerY - getHeight() / 2;
                        int gx = ((e.getX() + camX) / TILE_SIZE) * TILE_SIZE;
                        int gy = ((e.getY() + camY) / TILE_SIZE) * TILE_SIZE;
                        int size = (selectedBuild == 0) ? TILE_SIZE * 2 : TILE_SIZE;
                        
                        Rectangle newBuilding = new Rectangle(gx, gy, size, size);

                        // ПРОВЕРКА 1: Границы карты
                        boolean inBounds = gx >= 0 && gy >= 0 && 
                                          (gx + size) <= (WORLD_WIDTH * TILE_SIZE) && 
                                          (gy + size) <= (WORLD_HEIGHT * TILE_SIZE);

                        // ПРОВЕРКА 2: Наложение
                        boolean canPlace = inBounds; 
                        if (canPlace) {
                            for (Rectangle b : buildings) {
                                if (newBuilding.intersects(b)) {
                                    canPlace = false;
                                    break;
                                }
                            }
                        }

                        if (canPlace) {
                            buildings.add(newBuilding);
                        } else {
                            System.out.println("Тут строить нельзя!");
                        }
                    } else {
                        selectedBuild = -1; // Отмена на правую кнопку
                    }
                }
            }
        });

    }

    private void generateWorld() {
        generate gen = new generate(WORLD_WIDTH, WORLD_HEIGHT, TILE_SIZE);
        generate.WorldData data = gen.generate();
        this.waterPlants = new ArrayList<>(data.waterPlants);
        this.silicon = new ArrayList<>(data.silicon);
        this.basalt = new ArrayList<>(data.basalt);
        this.iron = new ArrayList<>(data.iron);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        updateMovement();
        repaint();
    }

    private void updateMovement() {
        double dx = 0, dy = 0;
        if (pressedKeys.contains(KeyEvent.VK_W)) dy -= 1;
        if (pressedKeys.contains(KeyEvent.VK_S)) dy += 1;
        if (pressedKeys.contains(KeyEvent.VK_A)) dx -= 1;
        if (pressedKeys.contains(KeyEvent.VK_D)) dx += 1;

        if (dx != 0 || dy != 0) {
            // 1. Сначала ВСЕГДА обновляем угол, чтобы игрок мог вращаться на месте у стены
            angle = Math.atan2(dy, dx);
            
            double length = Math.sqrt(dx * dx + dy * dy);
            double nextX = playerX + (dx / length) * SPEED;
            double nextY = playerY + (dy / length) * SPEED;

            // 2. Теперь проверяем границы отдельно для каждой оси
            int mapLimitX = WORLD_WIDTH * TILE_SIZE;
            int mapLimitY = WORLD_HEIGHT * TILE_SIZE;

            // Если по X мы в границах — двигаемся по X
            if (nextX > 0 && nextX < mapLimitX) {
                playerX = nextX;
            }
            // Если по Y мы в границах — двигаемся по Y
            if (nextY > 0 && nextY < mapLimitY) {
                playerY = nextY;
            }
        }
    }



    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        int camX = (int)playerX - getWidth() / 2;
        int camY = (int)playerY - getHeight() / 2;

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        // 1. Пол
        for (int x = 0; x < WORLD_WIDTH; x++) {
            for (int y = 0; y < WORLD_HEIGHT; y++) {
                g2d.drawImage(floor1, x * TILE_SIZE - camX, y * TILE_SIZE - camY, TILE_SIZE, TILE_SIZE, null);
            }
        }

        // 2. Ресурсы
        for (Point p : basalt) g2d.drawImage(ggf1, p.x - camX, p.y - camY, TILE_SIZE, TILE_SIZE, null);
        for (Point p : waterPlants) g2d.drawImage(floor2, p.x - camX, p.y - camY, TILE_SIZE, TILE_SIZE, null);
        for (Point p : silicon) g2d.drawImage(floor3, p.x - camX, p.y - camY, TILE_SIZE, TILE_SIZE, null);
        for (Point p : iron) g2d.drawImage(ggf2, p.x - camX, p.y - camY, TILE_SIZE, TILE_SIZE, null);

        // 3. ПОСТРОЙКИ
        for (Rectangle r : buildings) {
            if (factoryImage != null) {
                g2d.drawImage(factoryImage, r.x - camX, r.y - camY, r.width, r.height, null);
            } else {
                g2d.setColor(Color.GRAY);
                g2d.fillRect(r.x - camX, r.y - camY, r.width, r.height);
            }
        }

        // 4. Игрок
        AffineTransform old = g2d.getTransform();
        g2d.translate(getWidth() / 2, getHeight() / 2); 
        g2d.rotate(angle + Math.PI / 2); 
        g2d.drawImage(playerImage, -PLAYER_SIZE/2, -PLAYER_SIZE/2, PLAYER_SIZE, PLAYER_SIZE, null);
        g2d.setTransform(old);

        // 5. ПРИЗРАК ПОСТРОЙКИ
        if (selectedBuild != -1) {
            Point m = getMousePosition();
            if (m != null) {
                g2d.setColor(new Color(0, 255, 255, 100));
                int size = (selectedBuild == 0) ? TILE_SIZE * 2 : TILE_SIZE;
                int gx = ((m.x + camX) / TILE_SIZE) * TILE_SIZE - camX;
                int gy = ((m.y + camY) / TILE_SIZE) * TILE_SIZE - camY;
                g2d.fillRect(gx, gy, size, size);
            }
        }

        buildMenu.draw(g2d, getWidth());
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Sibvaine");
        frame.add(new play());
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
