import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.GregorianCalendar;


//диалоговое окно с общими полями ввода и кнопками для всех диалоговых окон приложения
public class DialogParent extends JPanel {

    private JComboBox<Integer> dayComboBox;
    private DefaultComboBoxModel<Integer> dayModel;
    private JComboBox<String> monthComboBox;
    private JComboBox<Integer> yearComboBox;
    private boolean leapYear = false;
    private JPanel mainPanel;
    private JButton anotherButton;
    private JButton okButton;
    private JDialog dialog;
    private GregorianCalendar todayCalendar;
    private int maxYear;
    private boolean isEdit; //переменная для режима редактироваия. в режиме редактироваия ActionListener для кнопки okButton
    //обрабатывает вводимые пользователем данные как редактируемые, а не как вводимые впервые


    public DialogParent(){
        //конструктор создает панель для диалогового окна, но не отображает диалоговое окно
        setLayout(new BorderLayout());

        //создание панели с полями названия счета и начального баланса
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(1,2));

        //добавление даты (для создания даты: день, месяц и год - выпадающие списки)
        todayCalendar = new GregorianCalendar();
        JPanel panelDate = new JPanel();
        dayModel = new DefaultComboBoxModel<>();

        String[] monthNames = {"Январь","Февраль","Март","Апрель","Май","Июнь","Июль","Август","Сентябрь","Октябрь","Ноябрь","Декабрь"};
        monthComboBox = new JComboBox<>(monthNames);
        monthComboBox.addActionListener(new MonthListener());

        yearComboBox = new JComboBox<>();
        yearComboBox.addActionListener(new YearListener());
        //добавить в календарь годы с 2000 по текущий год
        maxYear = todayCalendar.get(Calendar.YEAR);
        for(int i = 2000; i < maxYear + 1; i++){
            yearComboBox.addItem(i);
        }

        dayComboBox = new JComboBox<>(dayModel);

        setTodayDate();

        panelDate.add(new JLabel("Год"));
        panelDate.add(yearComboBox);
        panelDate.add(new JLabel("Месяц"));
        panelDate.add(monthComboBox);
        panelDate.add(new JLabel("День"));
        panelDate.add(dayComboBox);

        Font font = new Font("SansSerif", Font.PLAIN, 14);
        Component[] componentsPanelDate = panelDate.getComponents();
        for(Component x : componentsPanelDate) {
            x.setFont(font);
        }

        JLabel labelDate = new JLabel("Дата");
        labelDate.setFont(font);
        mainPanel.add(labelDate);
        mainPanel.add(panelDate);

        add(mainPanel, BorderLayout.CENTER);

        anotherButton = new JButton("Добавить ещё");
        //создание кнопок Ok и Cancel, которые завершают работу с диалоговым окном
        okButton = new JButton("Ok");


        JButton cancelButton = new JButton("Отмена");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
            }
        });



        //добавление кнопок в нижнюю часть диалогового окна
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(anotherButton);
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        Component[] componentsButtonPanel = buttonPanel.getComponents();
        for (Component x : componentsButtonPanel) x.setFont(font);
        add(buttonPanel, BorderLayout.SOUTH);

    }

    //создание панели DialogAccount в виде диалогового окна
    public void showDialog(Component parent, String title){
        //обнаружить фрейм-владелец
        Frame owner = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);
        //создать новое диалоговое окно при первом обращении
        if (dialog == null){
            dialog = new JDialog(owner, true);
            dialog.add(this);
            dialog.getRootPane().setDefaultButton(okButton);
            dialog.pack();
            dialog.setResizable(false);
            dialog.setLocationRelativeTo(null);
        }

        dialog.setTitle(title);
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public JButton getAnotherButton() {
        return anotherButton;
    }

    public JButton getOkButton() {
        return okButton;
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

    public JDialog getDialog() {
        return dialog;
    }

    public boolean isEdit() {
        return isEdit;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
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
}

