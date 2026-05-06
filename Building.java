import java.awt.Rectangle;

public class Building {
    public Rectangle rect;
    public int type; // 0 - завод, 1 - бур

    public Building(int x, int y, int size, int type) {
        this.rect = new Rectangle(x, y, size, size);
        this.type = type;
    }
}
