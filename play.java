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
    private BufferedImage floor1, floor2, floor3, ggf1, ggf2, playerImage, factoryImage, burImage;
    
    private final int TILE_SIZE = 32;  
    private final int PLAYER_SIZE = 40; 
    
    private double playerX = 500, playerY = 500; 
    private double angle = 0;
    private final double SPEED = 4.0;
    
    private final int WORLD_WIDTH = 100;
    private final int WORLD_HEIGHT = 100;

    private BuildMenu buildMenu = new BuildMenu();
    private int selectedBuild = -1; 
    private ArrayList<Building> buildings = new ArrayList<>(); 

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
            burImage = ImageIO.read(new File("bur.png"));
            
            File f = new File("factory.png");
            if(f.exists()) factoryImage = ImageIO.read(f);
            
            generateWorld();
        } catch (IOException e) {
            System.out.println("ОШИБКА ЗАГРУЗКИ: " + e.getMessage());
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
                int camX = calculateCamX();
                int camY = calculateCamY();
                int mouseWorldX = e.getX() + camX;
                int mouseWorldY = e.getY() + camY;


                // 1. УДАЛЕНИЕ (Правая кнопка)
                if (SwingUtilities.isRightMouseButton(e)) {
                    for (int i = 0; i < buildings.size(); i++) {
                        
                        // ТУТ ИСПРАВЛЕНО: проверяем через .rect.contains
                        if (buildings.get(i).rect.contains(mouseWorldX, mouseWorldY)) {
                            buildings.remove(i);
                            repaint();
                            return;
                        }
                    }
                    selectedBuild = -1;
                } 


                // 2. СТРОИТЕЛЬСТВО (Левая кнопка)
                else if (SwingUtilities.isLeftMouseButton(e)) {
                    int cell = buildMenu.getClickedCell(e.getX(), e.getY(), getWidth());

                    if (cell != -1) {
                        selectedBuild = cell;
                    } else if (selectedBuild != -1) {

                        int gx = (mouseWorldX / TILE_SIZE) * TILE_SIZE;
                        int gy = (mouseWorldY / TILE_SIZE) * TILE_SIZE;
                        
                        // Завод (0) — размер 2x2 (64), Бур (1) — размер 1x1 (32)
                        int size = (selectedBuild == 0) ? TILE_SIZE * 2 : TILE_SIZE;

                        // Временный квадрат для проверок
                        Rectangle tempRect = new Rectangle(gx, gy, size, size);


                        // ПРОВЕРКА 1: Границы
                        boolean inBounds = gx >= 0 && gy >= 0 && 
                                          (gx + size) <= (WORLD_WIDTH * TILE_SIZE) && 
                                          (gy + size) <= (WORLD_HEIGHT * TILE_SIZE);


                        // ПРОВЕРКА 2: Ресурсы для бура
                        boolean canPlace = inBounds;

                        if (canPlace && selectedBuild == 1) { 
                            boolean onResource = false;
                            
                            for (Point p : iron) {
                                if (p.x == gx && p.y == gy) onResource = true;
                            }
                            
                            for (Point p : silicon) {
                                if (p.x == gx && p.y == gy) onResource = true;
                            }

                            if (!onResource) {
                                System.out.println("Тут нет руды для бура!");
                                canPlace = false;
                            }
                        }


                        // ПРОВЕРКА 3: Наложение на другие здания
                        if (canPlace) {
                            for (Building b : buildings) {
                                
                                // ТУТ ИСПРАВЛЕНО: проверяем пересечение с b.rect
                                if (tempRect.intersects(b.rect)) {
                                    canPlace = false;
                                    break;
                                }
                            }
                        }


                        // ИТОГ: Ставим новое здание типа Building
                        if (canPlace) {
                            // ТУТ ИСПРАВЛЕНО: создаем объект нашего нового класса
                            buildings.add(new Building(gx, gy, size, selectedBuild));
                        }

                    }
                }
            }
        });


    }

    // Вспомогательные методы для камеры
    private int calculateCamX() {
        int cx = (int)playerX - getWidth() / 2;
        return Math.max(0, Math.min(cx, WORLD_WIDTH * TILE_SIZE - getWidth()));
    }

    private int calculateCamY() {
        int cy = (int)playerY - getHeight() / 2;
        return Math.max(0, Math.min(cy, WORLD_HEIGHT * TILE_SIZE - getHeight()));
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
            angle = Math.atan2(dy, dx);
            double length = Math.sqrt(dx * dx + dy * dy);
            double nextX = playerX + (dx / length) * SPEED;
            double nextY = playerY + (dy / length) * SPEED;

            if (nextX > 0 && nextX < WORLD_WIDTH * TILE_SIZE) playerX = nextX;
            if (nextY > 0 && nextY < WORLD_HEIGHT * TILE_SIZE) playerY = nextY;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        int camX = calculateCamX();
        int camY = calculateCamY();

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

        // 3. Постройки на карте
        for (Building b : buildings) {
            if (b.type == 0) { // Это завод
                if (factoryImage != null) {
                    g2d.drawImage(factoryImage, b.rect.x - camX, b.rect.y - camY, b.rect.width, b.rect.height, null);
                }
            } 
            else if (b.type == 1) { // Это бур
                if (burImage != null) {
                    g2d.drawImage(burImage, b.rect.x - camX, b.rect.y - camY, b.rect.width, b.rect.height, null);
                }
            }
        }


        // 4. Игрок
        AffineTransform old = g2d.getTransform();
        g2d.translate(playerX - camX, playerY - camY); 
        g2d.rotate(angle + Math.PI / 2); 
        g2d.drawImage(playerImage, -PLAYER_SIZE/2, -PLAYER_SIZE/2, PLAYER_SIZE, PLAYER_SIZE, null);
        g2d.setTransform(old);

        // 5. ПРИЗРАК ПОСТРОЙКИ
        if (selectedBuild != -1) {
            Point m = getMousePosition();
            if (m != null) {
                // Если мышка не над меню, рисуем призрак
                if (!buildMenu.isClicked(m.x, m.y, getWidth())) {
                    g2d.setColor(new Color(0, 255, 255, 100));
                    int size = (selectedBuild == 0) ? TILE_SIZE * 2 : TILE_SIZE;
                    // Рассчитываем сетку с учетом камеры
                    int gx = ((m.x + camX) / TILE_SIZE) * TILE_SIZE - camX;
                    int gy = ((m.y + camY) / TILE_SIZE) * TILE_SIZE - camY;
                    g2d.fillRect(gx, gy, size, size);
                }
            }
        }
        
        // --- ИСПРАВЛЕНО: Теперь вызываем только с Graphics2D и шириной ---
        buildMenu.draw(g2d, getWidth(), factoryImage, burImage);
    }
}
