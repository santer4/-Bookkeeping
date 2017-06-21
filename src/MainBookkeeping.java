import javax.swing.*;
import java.awt.*;


public class MainBookkeeping {
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                FrameBookkeeping frame = new FrameBookkeeping();
                frame.setTitle("Книга учета доходов-расходов");
                frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }
}
