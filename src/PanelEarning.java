import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.GregorianCalendar;

/**
 * Панель для вкладки "Доходы"
 */
public class PanelEarning extends JPanel {
    private BuhTableModel tableModelEarning;
    private JTable earningTable;

    public PanelEarning(){
        JButton buttonAddEarning = new JButton("Добавить");
        JButton buttonEditEarning = new JButton("Редактировать");
        JButton buttonRemoveEarning = new JButton("Удалить");

        setLayout(new BorderLayout());
        JPanel panelButton = new JPanel();
        panelButton.add(buttonAddEarning);
        panelButton.add(buttonEditEarning);
        panelButton.add(buttonRemoveEarning);
        add(panelButton, BorderLayout.SOUTH);

        Component[] components = panelButton.getComponents();
        for (Component x : components) {
            x.setFont(new Font("SansSerif", Font.PLAIN, 14));
        }

        // Лучше вынесты в static поле
        //создание таблицы для отображения счетов, добавление таблицы на панель
        String[] columnNames = {"Дата",
                "Счет",
                "Категория дохода",
                "Подкатегория дохода",
                "Сумма",
                "Примечание"};
        Object[][] dataTable = {};
        tableModelEarning = new BuhTableModel(dataTable, columnNames);
        earningTable = new JTable(tableModelEarning);
        earningTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        earningTable.getTableHeader().setFont(new Font("SansSerif", Font.PLAIN, 14));

        DefaultTableCellRenderer doubleRenderer = new DoubleTableCellRenderer();
        doubleRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
        earningTable.setDefaultRenderer(Double.class, doubleRenderer);

        DefaultTableCellRenderer stringRenderer = new StringTableCellRenderer();
        stringRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        earningTable.setDefaultRenderer(String.class, stringRenderer);

        DefaultTableCellRenderer calendarRenderer = new CalendarTableCellRenderer();
        calendarRenderer.setHorizontalAlignment(DefaultTableCellRenderer.LEFT);
        earningTable.setDefaultRenderer(GregorianCalendar.class, calendarRenderer);

        earningTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(earningTable);
        add(scrollPane, BorderLayout.CENTER);

        /*
        expenseTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        expenseTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        expenseTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        expenseTable.getColumnModel().getColumn(3).setPreferredWidth(200);
        expenseTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        expenseTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        expenseTable.getColumnModel().getColumn(6).setPreferredWidth(70);
        expenseTable.getColumnModel().getColumn(7).setPreferredWidth(70);
        */

        earningTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        earningTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        earningTable.getColumnModel().getColumn(2).setPreferredWidth(250);
        earningTable.getColumnModel().getColumn(3).setPreferredWidth(200);
        earningTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        earningTable.getColumnModel().getColumn(5).setPreferredWidth(150);
    }

    public BuhTableModel getTableModelEarning() {
        return tableModelEarning;
    }

    public JTable getEarningTable() {
        return earningTable;
    }
}
