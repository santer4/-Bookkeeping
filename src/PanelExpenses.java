import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.GregorianCalendar;

/**
 * Панель для вкладки "Расходы"
 */

public class PanelExpenses extends JPanel {
    private BuhTableModel tableModelExpense;
    private JTable expenseTable;

    public PanelExpenses(){
        JButton buttonAddExpense = new JButton("Добавить");
        JButton buttonEditExpense = new JButton("Редактировать");
        JButton buttonRemoveExpense = new JButton("Удалить");

        setLayout(new BorderLayout());
        JPanel panelButton = new JPanel();
        panelButton.add(buttonAddExpense);
        panelButton.add(buttonEditExpense);
        panelButton.add(buttonRemoveExpense);
        add(panelButton, BorderLayout.SOUTH);

        Component[] components = panelButton.getComponents();
        for (Component x : components) {
            x.setFont(new Font("SansSerif", Font.PLAIN, 14));
        }

        //создание таблицы для отображения счетов, добавление таблицы на панель
        String[] columnNames = {"Дата",
                "Счет",
                "Категория расхода",
                "Подкатегория расхода",
                "Кол.",
                "Ед. изм.",
                "Сумма",
                "Примечание"};
        Object[][] dataTable = {};
        tableModelExpense = new BuhTableModel(dataTable, columnNames);
        expenseTable = new JTable(tableModelExpense);
        expenseTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        expenseTable.getTableHeader().setFont(new Font("SansSerif", Font.PLAIN, 14));

        DefaultTableCellRenderer doubleRenderer = new DoubleTableCellRenderer();
        doubleRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
        expenseTable.setDefaultRenderer(Double.class, doubleRenderer);

        DefaultTableCellRenderer stringRenderer = new StringTableCellRenderer();
        stringRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        expenseTable.setDefaultRenderer(String.class, stringRenderer);

        DefaultTableCellRenderer calendarRenderer = new CalendarTableCellRenderer();
        calendarRenderer.setHorizontalAlignment(DefaultTableCellRenderer.LEFT);
        expenseTable.setDefaultRenderer(GregorianCalendar.class, calendarRenderer);

        expenseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(expenseTable);
        add(scrollPane, BorderLayout.CENTER);

        expenseTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        expenseTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        expenseTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        expenseTable.getColumnModel().getColumn(3).setPreferredWidth(200);
        expenseTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        expenseTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        expenseTable.getColumnModel().getColumn(6).setPreferredWidth(70);
        expenseTable.getColumnModel().getColumn(7).setPreferredWidth(70);

        //expenseTable.setAutoCreateRowSorter(true);


    }





    public BuhTableModel getTableModelExpense() {
        return tableModelExpense;
    }

    public JTable getExpenseTable() {
        return expenseTable;
    }




}
