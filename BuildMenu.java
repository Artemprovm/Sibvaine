import java.awt.*;

public class BuildMenu {
    // Настройки панели
    private final int x = 620; // Позиция справа
    private final int y = 20;  // Отступ сверху
    private final int width = 150;
    private final int height = 300;

    public void draw(Graphics2D g2d, int panelWidth) {
     int width = 150;
     int height = 300;
     int y = 20;
    
     // ВЫЧИСЛЯЕМ X: ширина окна минус ширина меню минус отступ (например, 20)
     int x = panelWidth - width - 20; 

     // Теперь рисуем всё, используя этот вычисленный 'x'
     g2d.setColor(new Color(0, 0, 0, 180));
     g2d.fillRoundRect(x, y, width, height, 15, 15);

     g2d.setColor(Color.WHITE);
     g2d.setStroke(new BasicStroke(2));
     g2d.drawRoundRect(x, y, width, height, 15, 15);

     g2d.setFont(new Font("Arial", Font.BOLD, 14));
     g2d.drawString("ПОСТРОЙКИ", x + 30, y + 25);

     g2d.setColor(new Color(255, 255, 255, 50));
     for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 2; j++) {
             g2d.fillRect(x + 15 + j * 65, y + 45 + i * 65, 55, 55);
         }
     }
    }


    // Метод для проверки: кликнул ли пользователь в область меню?
    public boolean isClicked(int mouseX, int mouseY, int panelWidth) {
        int width = 150;
        int x = panelWidth - width - 20; 
        return mouseX >= x && mouseX <= x + width && mouseY >= 20 && mouseY <= 20 + 300;
    }
    public int getClickedCell(int mouseX, int mouseY, int panelWidth) {
    int xStart = panelWidth - 150 - 20; 
    for (int i = 0; i < 3; i++) {
        for (int j = 0; j < 2; j++) {
            int cx = xStart + 15 + j * 65;
            int cy = 20 + 45 + i * 65;
            if (mouseX >= cx && mouseX <= cx + 55 && mouseY >= cy && mouseY <= cy + 55) return i * 2 + j;
        }
    }
    return -1;
 }
}
