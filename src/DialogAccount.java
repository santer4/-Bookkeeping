import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.*;


/*
/*
Диалоговое окно для добавления счета
*/


public class DialogAccount extends DialogParent{

    private NameAccountTextField nameAccount;
    private MoneyTextField startBalance;
    private double expenses = 0;
    private double earnings = 0;
    private boolean ok; // если переменная ok == true, значит пользователь ввел все необх. данные, можно конструировать объект типа MyAccount
    private ArrayList<MyAccount> tempListAccounts;
    private Set<String> namesAccountsSet;

    public DialogAccount(){
        tempListAccounts = new ArrayList<>();
        namesAccountsSet = new TreeSet<>();
        JPanel mainPanel = getMainPanel();
        mainPanel.setLayout(new GridLayout(3,2));

        //создание панели с полями названия счета и начального баланса
        Font font = new Font("SansSerif", Font.PLAIN, 14);
        mainPanel.add(new JLabel("Название счета"), BorderLayout.LINE_END);
        mainPanel.add(nameAccount = new NameAccountTextField(20));
        mainPanel.add(new JLabel("Начальный баланс"));
        mainPanel.add(startBalance = new MoneyTextField(2));
        Component[] componentsMainPanel = mainPanel.getComponents();
        for (Component x : componentsMainPanel) x.setFont(font);
        startBalance.addFocusListener(new StartBalanceFocusAdapter());

        getAnotherButton().addActionListener(new AnotherActionListener());
        getOkButton().addActionListener(new AccountActionListener());

    }
    //создание нового аккаунта
    public MyAccount getMyAccount(){
        //получение дня, месяца и года для календаря
        int day = (Integer) getDayComboBox().getSelectedItem();
        int month = getMonthComboBox().getSelectedIndex();
        int year = (Integer) getYearComboBox().getSelectedItem();
        double expenses = 0;
        double earnings = 0;
        String formatBalance = startBalance.getText();
        formatBalance = formatBalance.replace(",", ".");
        formatBalance = formatBalance.replace(new String(new char[]{160}), "");
        boolean startZero = true;
        if (formatBalance.matches("0+\\.*0*")){
            startZero = false;
        }
        while (startZero){
            if (!formatBalance.startsWith("0")) startZero = false;
            else formatBalance = formatBalance.replaceFirst("0", "");
        }
        if (formatBalance.equals("")) formatBalance = "0";
        double balance = Double.parseDouble(formatBalance);
        if (isEdit()){
            expenses = this.expenses;
            earnings = this.earnings;
        }
        return new MyAccount(nameAccount.getText(), expenses, earnings, balance,
                balance, new GregorianCalendar(year, month, day));
    }

    /**
     * Создание панели DialogAccount в виде диалогового окна
     *
     * Тут наблиюдается классический случай побочного эффекта в методе. Такое бывает когда метод получает
     * агрумент и меняет его в своем теле вместо того чтобы вернуть нормальный реультат.
     *
     * Побочные эффекты - зло и от них надо избавляться. Я бы создал какую-нибудь структуру которая содержала
     * результат работы пользователя в диалоговом окне
     */
    public boolean showAddDialogAccount(Component parent, String title, Set<String> namesAccountsSet, ArrayList<MyAccount> tempListAccounts, boolean edit){
        setEdit(edit);
        ok = false;
        getAnotherButton().setVisible(true);
        this.tempListAccounts = tempListAccounts;
        showDialog(parent, title);

        nameAccount.setText("");
        startBalance.setText("0.00");

        this.namesAccountsSet.removeAll(new TreeSet<>(this.namesAccountsSet));
        this.namesAccountsSet.addAll(namesAccountsSet);

        getDialog().setVisible(true);
        return ok;
    }

    public boolean showEditDialogAccount(Component parent, String title, Set<String> namesAccountsSet,
                                         MyAccount editMyAccount, boolean edit){
        setEdit(edit);
        ok = false;
        getAnotherButton().setVisible(false);

        showDialog(parent, title);

        //отображение данных счета в полях в диалоговом окне
        nameAccount.setText(editMyAccount.getName());
        earnings = editMyAccount.getEarnings();
        expenses = editMyAccount.getExpenses();
        startBalance.setText(Double.toString(editMyAccount.getStartBalance()));
        GregorianCalendar calendar = editMyAccount.getGregorianCalendar();
        int dayMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        getYearComboBox().setSelectedIndex(year - 2000);
        getMonthComboBox().setSelectedIndex(month);
        getDayComboBox().setSelectedIndex(dayMonth - 1);

        this.namesAccountsSet.removeAll(new TreeSet<>(this.namesAccountsSet));
        this.namesAccountsSet.addAll(namesAccountsSet);
        getDialog().setVisible(true);
        return ok;
    }

    public class AccountActionListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            boolean hasItem = false;
            String name = nameAccount.getText();
            if (namesAccountsSet.contains(name)) hasItem = true;

            if (name.isEmpty()) {
                //если не введено никакого имени показать предупреждение
                String s = "Введите название счета";
                JOptionPane.showMessageDialog(DialogAccount.this, s, "Предупреждение", JOptionPane.INFORMATION_MESSAGE);
            } else if (hasItem && !isEdit()) {
                //если такой счет уже существует показать предупреждение
                String s = "Счет с названием \"" + name + "\" уже существует";
                JOptionPane.showMessageDialog(DialogAccount.this, s, "Предупреждение", JOptionPane.INFORMATION_MESSAGE);
            } else if (!isEdit()) {
                //если это не вызов диалогового окна для редактирования и имя введено и такого счета не существует, то добавить в счет в список и скрыть диалоговое окно
                tempListAccounts.add(getMyAccount());
                ok = true;
                getDialog().setVisible(false);
            } else {
                //если это диалоговое окно для редактирования
                ok = true;
                getDialog().setVisible(false);
            }
        }
    }

    public class AnotherActionListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            boolean hasItem = false;
            String name = nameAccount.getText();
            if (namesAccountsSet.contains(name)) hasItem = true;

            if (name.isEmpty()) {
                //если не введено никакого имени показать предупреждение
                String s = "Введите название счета";
                JOptionPane.showMessageDialog(DialogAccount.this, s, "Предупреждение", JOptionPane.INFORMATION_MESSAGE);
            } else if (hasItem) {
                //если такой счет уже существует показать предупреждение
                String s = "Счет с названием \"" + name + "\" уже существует";
                JOptionPane.showMessageDialog(DialogAccount.this, s, "Предупреждение", JOptionPane.INFORMATION_MESSAGE);
            } else {
                //если имя введено и такого счета не существует, новый счет добавить в список счетов и обнулить поля ввода
                tempListAccounts.add(getMyAccount());
                namesAccountsSet.add(name);
                System.out.println(getMyAccount());
                nameAccount.setText("");
                startBalance.setText("0.00");
            }
        }
    }

    public class StartBalanceFocusAdapter extends FocusAdapter{
        @Override
        public void focusGained(FocusEvent e) {
            if(startBalance.getText().equals("0,00")){
                startBalance.setText("");
            }
        }
    }
}

