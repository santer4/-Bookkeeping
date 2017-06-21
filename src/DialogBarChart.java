import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;


public class DialogBarChart extends JPanel {
    ArrayList<String> columnBarNames;
    ArrayList<Double> sumList;
    private JDialog dialog;

    public DialogBarChart(){
        super();
        setLayout(new BorderLayout());
        columnBarNames = new ArrayList<>();
        sumList = new ArrayList<>();
    }

    public void showDialog(Component parent, String title, ArrayList<String> columnBarNames, ArrayList<Double> sumList){
        this.columnBarNames.removeAll(new ArrayList<>(this.columnBarNames));
        this.sumList.removeAll(new ArrayList<>(this.sumList));
        this.columnBarNames.addAll(columnBarNames);
        this.sumList.addAll(sumList);
        Frame owner = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);
        dialog = new JDialog(owner, true);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int width = new Double(dim.getWidth()).intValue();
        int widthDialog;
        if (width > 1500) widthDialog = new Double(width*0.9).intValue();
        else widthDialog = new Double(width*0.6).intValue();
        int height = new Double(dim.getHeight()*0.9).intValue();
        dialog.setPreferredSize(new Dimension(widthDialog, height));
        dialog.add(this);
        dialog.pack();
        dialog.setLocationRelativeTo(null);

        dialog.setTitle(title);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                initAndShowFX();
            }
        });

        dialog.setVisible(true);


    }
    public void initAndShowFX(){
        final JFXPanel jfxPanel = new JFXPanel();
        this.add(jfxPanel, BorderLayout.CENTER);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                initFX(jfxPanel);
            }
        });
    }

    public void initFX(JFXPanel jfxPanel){
        Scene scene = createScene();
        jfxPanel.setScene(scene);
    }

    public Scene createScene(){
        final CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        final BarChart<String,Number> bc =
                new BarChart<>(xAxis,yAxis);
        bc.setTitle("Данные суммарно");
        xAxis.setLabel("Категории");
        yAxis.setLabel("Сумма");

        XYChart.Series series1 = new XYChart.Series();
        series1.setName("Величина");

        for (int i = 0; i < columnBarNames.size(); i++) {
            series1.getData().add(new XYChart.Data(columnBarNames.get(i), sumList.get(i)));
        }

        Scene scene = new Scene(bc, 800,400);
        bc.getData().addAll(series1);
        return (scene);
    }
}
