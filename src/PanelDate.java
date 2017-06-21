import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Alexandr on 06.06.2017.
 */
public class PanelDate extends JPanel {
    private JComboBox<Integer> dayComboBox;
    private DefaultComboBoxModel<Integer> dayModel;
    private JComboBox<String> monthComboBox;
    private JComboBox<Integer> yearComboBox;
    private boolean leapYear = false;
    private GregorianCalendar todayCalendar;
    private int maxYear;

    public PanelDate(){
        super();

        todayCalendar = new GregorianCalendar();
        dayModel = new DefaultComboBoxModel<>();

        String[] monthNames = {"Январь","Февраль","Март","Апрель","Май","Июнь","Июль","Август","Сентябрь","Октябрь","Ноябрь","Декабрь"};
        monthComboBox = new JComboBox<>(monthNames);
        monthComboBox.addActionListener(new MonthListener());


        yearComboBox = new JComboBox<>();
        yearComboBox.addActionListener(new YearListener());
        maxYear = todayCalendar.get(Calendar.YEAR);
        for(int i = 2000; i < maxYear + 1; i++){
            yearComboBox.addItem(i);
        }
        dayComboBox = new JComboBox<>(dayModel);
        setTodayDate();

        add(new JLabel("Год"));
        add(yearComboBox);
        add(new JLabel("Месяц"));
        add(monthComboBox);
        add(new JLabel("День"));
        add(dayComboBox);

        Font font = new Font("SansSerif", Font.PLAIN, 14);
        Component[] componentsPanelDate = getComponents();
        for(Component x : componentsPanelDate) {
            x.setFont(font);
        }
    }

    public class YearListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Integer year = (Integer) yearComboBox.getSelectedItem();
            if ((year % 4 == 0) && (year % 100 != 0)) {
                leapYear = true;
                String month = (String) monthComboBox.getSelectedItem();
                fillDayModel(month);
                return;
            }
            if (year % 400 == 0) {
                leapYear = true;
                String month = (String) monthComboBox.getSelectedItem();
                fillDayModel(month);
                return;
            }
            leapYear = false;
            String month = (String) monthComboBox.getSelectedItem();
            fillDayModel(month);
        }
    }

    public class MonthListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String month = (String) monthComboBox.getSelectedItem();
            fillDayModel(month);

        }
    }

    public void fillDayModel(String month){
        dayModel.removeAllElements();
        if (month.equals("Январь") || month.equals("Март") || month.equals("Май") || month.equals("Июль")
                || month.equals("Август") || month.equals("Октябрь") || month.equals("Декабрь")) {
            for (int i = 1; i < 32; i++){
                dayModel.addElement(i);
            }
            return;
        }
        if (month.equals("Апрель") || month.equals("Июнь") || month.equals("Сентябрь") || month.equals("Ноябрь")) {
            for (int i = 1; i < 31; i++){
                dayModel.addElement(i);
            }
            return;
        }
        if (month.equals("Февраль")){
            for (int i = 1; i < 29; i++){
                dayModel.addElement(i);
            }
            if (leapYear) dayModel.addElement(29);
        }

    }

    public void setTodayDate(){
        yearComboBox.setSelectedItem(maxYear);
        monthComboBox.setSelectedIndex(todayCalendar.get(Calendar.MONTH));
        dayComboBox.setSelectedItem(todayCalendar.get(Calendar.DAY_OF_MONTH));
    }

    public String inverseSimpleDate(){
        GregorianCalendar calendar = new GregorianCalendar((Integer) yearComboBox.getSelectedItem(),
                monthComboBox.getSelectedIndex(), (Integer) dayComboBox.getSelectedItem());
        return String.format("%1$tY.%1$tm.%1$td", calendar);

    }

    public String simpleDate(){
        GregorianCalendar calendar = new GregorianCalendar((Integer) yearComboBox.getSelectedItem(),
                monthComboBox.getSelectedIndex(), (Integer) dayComboBox.getSelectedItem());
        return String.format("%1$td.%1$tm.%1$tY", calendar);
    }

    public String nextDayInverseSimpleDate(){
        GregorianCalendar calendar = new GregorianCalendar((Integer) yearComboBox.getSelectedItem(),
                monthComboBox.getSelectedIndex(), (Integer) dayComboBox.getSelectedItem());
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        return String.format("%1$tY.%1$tm.%1$td", calendar);
    }

    public JComboBox<Integer> getDayComboBox() {
        return dayComboBox;
    }

    public JComboBox<String> getMonthComboBox() {
        return monthComboBox;
    }

    public JComboBox<Integer> getYearComboBox() {
        return yearComboBox;
    }
}
