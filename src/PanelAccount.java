import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Панель для вкладки "Счета"
 */
public class PanelAccount extends JPanel {
    private BuhTableModel tableModelAccount;
    private JTable accountTable;

    public PanelAccount(){

        JButton buttonAddAccount = new JButton("Добавить");
        JButton buttonEditAccount = new JButton("Редактировать");
        JButton buttonRemoveAccount = new JButton("Удалить");

        setLayout(new BorderLayout());

        JPanel panelButton = new JPanel();
        panelButton.add(buttonAddAccount);
        panelButton.add(buttonEditAccount);
        panelButton.add(buttonRemoveAccount);
        add(panelButton, BorderLayout.SOUTH);

        Component[] components = panelButton.getComponents();
        for (Component x : components) {
            x.setFont(new Font("SansSerif", Font.PLAIN, 14));
        }

        //создание таблицы для отображения счетов, добавление таблицы на панель
        String[] columnNames = {"Счет", "Расход", "Доход", "Начальный баланс", "Остаток"};
        Object[][] dataTable = {};
        tableModelAccount = new BuhTableModel(dataTable, columnNames);
        accountTable = new JTable(tableModelAccount);
        accountTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        accountTable.getTableHeader().setFont(new Font("SansSerif", Font.PLAIN, 14));

        DefaultTableCellRenderer doubleRenderer = new DoubleTableCellRenderer();
        doubleRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
        accountTable.setDefaultRenderer(Double.class, doubleRenderer);

        DefaultTableCellRenderer stringRenderer = new StringTableCellRenderer();
        stringRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        accountTable.setDefaultRenderer(String.class, stringRenderer);

        accountTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(accountTable);
        add(scrollPane, BorderLayout.CENTER);


    }

    public BuhTableModel getTableModelAccount() {
        return tableModelAccount;
    }

    public JTable getAccountTable() {
        return accountTable;
    }











}
