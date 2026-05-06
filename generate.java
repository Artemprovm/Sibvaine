import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class generate {
    private final int WIDTH, HEIGHT, TILE_SIZE;
    private final Random rand = new Random();

    public generate(int width, int height, int tileSize) {
        this.WIDTH = width;
        this.HEIGHT = height;
        this.TILE_SIZE = tileSize;
    }

    public class WorldData {
        public ArrayList<Point> waterPlants = new ArrayList<>(); // floor2
        public ArrayList<Point> silicon = new ArrayList<>();    // floor3
        public ArrayList<Point> basalt = new ArrayList<>();     // ggf1
        public ArrayList<Point> iron = new ArrayList<>();       // ggf2
    }

    public WorldData generate() {
        WorldData data = new WorldData();
        // Используем Set для быстрой проверки биома
        HashSet<Point> basaltMap = new HashSet<>();

        // 1. ГЕНЕРАЦИЯ БАЗАЛЬТА (Редкие неровные круги)
        int zones = 6 + rand.nextInt(5); 
        for (int i = 0; i < zones; i++) {
            int cx = rand.nextInt(WIDTH);
            int cy = rand.nextInt(HEIGHT);
            int radius = 6 + rand.nextInt(10);

            for (int x = cx - radius; x <= cx + radius; x++) {
                for (int y = cy - radius; y <= cy + radius; y++) {
                    if (x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT) {
                        double dist = Math.sqrt(Math.pow(x - cx, 2) + Math.pow(y - cy, 2));
                        // Неровный край
                        if (dist < radius - rand.nextDouble() * 4) {
                            Point p = new Point(x * TILE_SIZE, y * TILE_SIZE);
                            basaltMap.add(p);
                            data.basalt.add(p);

                            // ЖЕЛЕЗО на базальте (редко кучками)
                            if (rand.nextDouble() < 0.03) {
                                spawnCluster(x, y, data.iron, 5, basaltMap, true);
                            }
                        }
                    }
                }
            }
        }

        // 2. ГЕНЕРАЦИЯ НА ПЕСКЕ (Водоросли и Кремний)
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                Point p = new Point(x * TILE_SIZE, y * TILE_SIZE);
                
                if (!basaltMap.contains(p)) {
                    // Водоросли очень часто
                    if (rand.nextDouble() < 0.18) {
                        data.waterPlants.add(p);
                    } 
                    // Кремний реже кучками
                    else if (rand.nextDouble() < 0.02) {
                        spawnCluster(x, y, data.silicon, 4, basaltMap, false);
                    }
                }
            }
        }
        return data;
    }

    private void spawnCluster(int x, int y, ArrayList<Point> list, int size, HashSet<Point> basaltMap, boolean mustBeBasalt) {
        for (int i = 0; i < size; i++) {
            int nx = x + rand.nextInt(3) - 1;
            int ny = y + rand.nextInt(3) - 1;
            Point np = new Point(nx * TILE_SIZE, ny * TILE_SIZE);

            if (nx >= 0 && nx < WIDTH && ny >= 0 && ny < HEIGHT) {
                boolean isBasalt = basaltMap.contains(np);
                if (mustBeBasalt && isBasalt) list.add(np);
                else if (!mustBeBasalt && !isBasalt) list.add(np);
            }
        }
    }
}
