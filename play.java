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
    // Изображения
    private BufferedImage floor1, floor2, floor3, ggf1, ggf2, playerImage;
    
    private final int TILE_SIZE = 32;  
    private final int PLAYER_SIZE = 40; 
    
    private double playerX = 500, playerY = 500; 
    private double angle = 0;
    private final double SPEED = 4.0;
    
    private final int WORLD_WIDTH = 100; // Увеличил мир для интереса
    private final int WORLD_HEIGHT = 100;

    private BuildMenu buildMenu = new BuildMenu();

    private final Set<Integer> pressedKeys = new HashSet<>();
    
    // Списки объектов (данные из генератора)
    private ArrayList<Point> waterPlants = new ArrayList<>(); // floor2
    private ArrayList<Point> silicon = new ArrayList<>();    // floor3
    private ArrayList<Point> basalt = new ArrayList<>();     // ggf1
    private ArrayList<Point> iron = new ArrayList<>();       // ggf2

    public play() {
        setBackground(Color.BLACK); 

        try {
            // Загрузка всех твоих 6 файлов
            floor1 = ImageIO.read(new File("floor1.png"));
            floor2 = ImageIO.read(new File("floor2.png"));
            floor3 = ImageIO.read(new File("floor3.png"));
            ggf1 = ImageIO.read(new File("ggf1.png"));
            ggf2 = ImageIO.read(new File("ggf2.png"));
            playerImage = ImageIO.read(new File("player.png"));
            
            generateWorld();
        } catch (IOException e) {
            System.out.println("Ошибка: Проверьте наличие всех 6 картинок в папке!");
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
           // Добавляем getWidth() вот сюда:
            if (buildMenu.isClicked(e.getX(), e.getY(), getWidth())) {
               System.out.println("Клик по меню!");
            }
        }
    });
    }

    private void generateWorld() {
        // Вызов твоего внешнего генератора
        generate gen = new generate(WORLD_WIDTH, WORLD_HEIGHT, TILE_SIZE);
        generate.WorldData data = gen.generate();
        
        // Переносим данные в наш класс
        this.waterPlants = data.waterPlants;
        this.silicon = data.silicon;
        this.basalt = data.basalt;
        this.iron = data.iron;
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

            int mapLimitX = WORLD_WIDTH * TILE_SIZE;
            int mapLimitY = WORLD_HEIGHT * TILE_SIZE;
            
            if (nextX > 0 && nextX < mapLimitX) playerX = nextX;
            if (nextY > 0 && nextY < mapLimitY) playerY = nextY;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        int camX = (int)playerX - getWidth() / 2;
        int camY = (int)playerY - getHeight() / 2;

        // Чтобы пиксель-арт не мылился
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        // 1. Слой 1: Песок (везде)
        for (int x = 0; x < WORLD_WIDTH; x++) {
            for (int y = 0; y < WORLD_HEIGHT; y++) {
                g2d.drawImage(floor1, x * TILE_SIZE - camX, y * TILE_SIZE - camY, TILE_SIZE, TILE_SIZE, null);
            }
        }

        // 2. Слой 2: Базальт (поверх песка)
        for (Point p : basalt) {
            g2d.drawImage(ggf1, p.x - camX, p.y - camY, TILE_SIZE, TILE_SIZE, null);
        }

        // 3. Слой 3: Водоросли и Кремний
        for (Point p : waterPlants) g2d.drawImage(floor2, p.x - camX, p.y - camY, TILE_SIZE, TILE_SIZE, null);
        for (Point p : silicon) g2d.drawImage(floor3, p.x - camX, p.y - camY, TILE_SIZE, TILE_SIZE, null);

        // 4. Слой 4: Железо
        for (Point p : iron) {
            g2d.drawImage(ggf2, p.x - camX, p.y - camY, TILE_SIZE, TILE_SIZE, null);
        }
        
        // 5. Игрок
        AffineTransform old = g2d.getTransform();
        g2d.translate(getWidth() / 2, getHeight() / 2); 
        g2d.rotate(angle + Math.PI / 2); 
        g2d.drawImage(playerImage, -PLAYER_SIZE/2, -PLAYER_SIZE/2, PLAYER_SIZE, PLAYER_SIZE, null);
        g2d.setTransform(old);
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
