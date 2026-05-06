import javax.swing.JFrame;

public class main {
    public static void main(String[] args) {
        // Создаем окно
        JFrame frame = new JFrame("Sibvaine - Underwater Industrial");
        
        // Создаем твой игровой экран
        play game = new play();
        
        // Настройки окна
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600); // Можешь поставить любой размер
        frame.add(game); // Добавляем игру в окно
        frame.setLocationRelativeTo(null); // Окно по центру экрана
        frame.setVisible(true); // Показываем окно
    }
}
