import java.awt.*;
import java.awt.image.BufferedImage;

public class BuildMenu {
    // Настройки панели
    private final int width = 150;
    private final int height = 300;
    private final int y = 20;

    // --- ОСТАВЛЯЕМ ТОЛЬКО ОДИН МЕТОД DRAW С 4 ПАРАМЕТРАМИ ---
    public void draw(Graphics2D g2d, int panelWidth, BufferedImage factoryImg, BufferedImage burImg) {
        
        // ВЫЧИСЛЯЕМ X: ширина окна минус ширина меню минус отступ
        int x = panelWidth - width - 20; 

        // 1. Рисуем фон меню
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRoundRect(x, y, width, height, 15, 15);

        // 2. Рисуем белую рамку
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(x, y, width, height, 15, 15);

        // 3. Заголовок
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("ПОСТРОЙКИ", x + 30, y + 25);

        // 4. Цикл отрисовки ячеек и иконок
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                
                int cellX = x + 15 + j * 65;
                int cellY = y + 45 + i * 65;
                int index = i * 2 + j; // Номер ячейки (0, 1, 2...)

                // Рисуем серый квадрат ячейки
                g2d.setColor(new Color(255, 255, 255, 50));
                g2d.fillRect(cellX, cellY, 55, 55);

                // Рисуем ЗАВОД в ячейке 0
                if (index == 0 && factoryImg != null) {
                    g2d.drawImage(factoryImg, cellX + 5, cellY + 5, 45, 45, null);
                }
                
                // Рисуем БУР в ячейке 1
                if (index == 1 && burImg != null) {
                    g2d.drawImage(burImg, cellX + 5, cellY + 5, 45, 45, null);
                }
            }
        }
    }

    // Метод для проверки клика по меню
    public boolean isClicked(int mouseX, int mouseY, int panelWidth) {
        int x = panelWidth - width - 20; 
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    // Метод для определения, на какую ячейку нажали
    public int getClickedCell(int mouseX, int mouseY, int panelWidth) {
        int xStart = panelWidth - width - 20; 
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                int cx = xStart + 15 + j * 65;
                int cy = y + 45 + i * 65;
                if (mouseX >= cx && mouseX <= cx + 55 && mouseY >= cy && mouseY <= cy + 55) {
                    return i * 2 + j;
                }
            }
        }
        return -1;
    }
}
