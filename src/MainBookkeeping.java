import javax.swing.*;
import java.awt.*;


public class MainBookkeeping {
    public static void main(String[] args) {
        // Начиная в 8й java не обязательно везде испольовать анонимные классы
        // См. лямбды и функциональные интерфейсы
        EventQueue.invokeLater(() -> {
            FrameBookkeeping frame = new FrameBookkeeping();
            frame.setTitle("Книга учета доходов-расходов");
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}
