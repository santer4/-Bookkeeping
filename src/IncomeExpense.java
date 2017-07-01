import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Компонент для вкладок Счета, Расходы, Доходы, График
 *
 * 1. Следует избегать расширения не-свойх классов. Как правило вместо этого можно обойтись
 * композицией
 *
 * 2. Как правило, при разработке интерфейсов разделяют логику отображения и бизнесс-логику
 * для этого используют паттерны вроде
 * https://ru.wikipedia.org/wiki/Model-View-Presenter
 * или
 * https://ru.wikipedia.org/wiki/Model-View-Controller
 *
 */
public class IncomeExpense extends JTabbedPane {
    private PanelEarning earnings; //вкладка Доходы
    private PanelExpenses expenses;  //вкладка Расходы
    private PanelAccount accounts; //вкладка Счета
    private PanelChart panelChart;

    private DialogAccount dialogAccount = null; //диалоговое окно для добавления счета
    private DialogExpense dialogExpense = null; //диалоговое окно для добавления и редактирования расхода
    private DialogEarning dialogEarning = null; //диалоговое окно для добавления и редактирования дохода
    private TreeMap<String, MyAccount> accountsMap; //map для хранения map Имя Счета - Счет
    private TreeSet<MyExpense> expensesSet;
    private TreeSet<MyEarning> earningsSet;

    /**
     * Название переменной должно быть понятнее
     */
    private boolean edit = false;

    public IncomeExpense() {
        //считать из файла записанные данные по каждому счету
        accountsMap = new TreeMap<>();
        expensesSet = new TreeSet<>(new MyExpenseComparator());
        earningsSet = new TreeSet<>(new MyEarningComparator());

        fillAccountsMap();
        fillExpensesSet();
        fillEarningsSet();

        //создание вкладки Счета
        accounts = new PanelAccount();
        JPanel panelButtonAccounts = (JPanel) accounts.getComponent(0);
        ((JButton) panelButtonAccounts.getComponent(0)).addActionListener(new AddAccountActionListener());
        ((JButton) panelButtonAccounts.getComponent(1)).addActionListener(new EditAccountActionListener());
        ((JButton) panelButtonAccounts.getComponent(2)).addActionListener(new RemoveAccActionListener());
        //accounts.getComponent()

        //создание вкладки Расходы
        expenses = new PanelExpenses();
        JPanel panelButtonExpenses = (JPanel) expenses.getComponent(0);
        ((JButton) panelButtonExpenses.getComponent(0)).addActionListener(new AddExpenseActionListener());
        ((JButton) panelButtonExpenses.getComponent(1)).addActionListener(new EditExpenseActionListener());
        ((JButton) panelButtonExpenses.getComponent(2)).addActionListener(new RemoveExpenseActionListener());

        //создание вкладки Доходы
        earnings = new PanelEarning();
        JPanel panelButtonEarnings = (JPanel) earnings.getComponent(0);
        ((JButton) panelButtonEarnings.getComponent(0)).addActionListener(new AddEarnActionListener());
        ((JButton) panelButtonEarnings.getComponent(1)).addActionListener(new EditEarnActionListener());
        ((JButton) panelButtonEarnings.getComponent(2)).addActionListener(new RemoveEarnActionListener());

        fillTables();

        addTab("Счета", accounts);
        addTab("Расходы", expenses);
        addTab("Доходы", earnings);

        panelChart = new PanelChart(expensesSet, earningsSet, accountsMap);
        addTab("График", panelChart);

        addChangeListener(new InlayListener());

    }


    //метод для заполнения списка счетов, записанных в файле
    public void fillAccountsMap() {
        try {
            //заполнение map Счетов
            File fileInput = new File(System.getProperty("user.dir") + "\\Data\\AccountMap.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileInput), "UTF-8"));
            ArrayList<String> listStrings = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                listStrings.add(line);
            }
            String name;
            double expenses;
            double earnings;
            double startBalance;
            double balance;
            GregorianCalendar calendar;
            if (listStrings.size() > 0) {
                for (String x : listStrings) {
                    String[] strings = x.split("/");
                    name = strings[0];
                    expenses = Double.parseDouble(strings[1]);
                    earnings = Double.parseDouble(strings[2]);
                    startBalance = Double.parseDouble(strings[3]);
                    balance = Double.parseDouble(strings[4]);
                    String[] dateStrings = strings[5].split("\\.");
                    int day = Integer.parseInt(dateStrings[0]);
                    int month = Integer.parseInt(dateStrings[1]);
                    int year = Integer.parseInt(dateStrings[2]);
                    calendar = new GregorianCalendar(year, month, day);
                    accountsMap.put(name, new MyAccount(name, expenses, earnings, startBalance, balance, calendar));
                }
            }
            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * метод для заполнения списка расходов, записанных в файле
     *
     * Это однозначно должно быть в отдельном сервисе
     */
    public void fillExpensesSet() {
        // Общее замечание. Переменные должны объявляться как можно ближе к их использованию
        // нет ничего страшного если переменная объявляется внутри цикла. Иначе код начинает ноправданно разрастаться
        // Я ни разу не видел чтобы так хранили дату. Смотри либо в сторону Date либо более новых LocalDate LocalDateTime
        GregorianCalendar calendar;
        try{
            //заполнение списка Расходов
            // Может сначала проверить что файл существует? Что будет если забыли задать свойство?
            File fileInputExpenses = new File(System.getProperty("user.dir") + "\\Data\\ExpenseSet.txt");
            BufferedReader readerExpenses = new BufferedReader(new InputStreamReader(new FileInputStream(fileInputExpenses), "UTF-8"));

            // Общее замечание. Один из принципов SOLID - Dependency Inversion предполагает что код
            // изпользующий обект не должен знать ничего о его реализации. Поэтому вместо ArrayList
            // нужно объявлять либо List либо Collection
            ArrayList<String> listExpenses = new ArrayList<>();
            String lineExpenses;
            while ((lineExpenses = readerExpenses.readLine()) != null) {
                listExpenses.add(lineExpenses);
            }

            String nameMyAccount;
            String category;
            String subcategory;
            Double quantity;
            String unitMeasure;
            double sum;
            String note;
            if (listExpenses.size() > 0) {
                for (String x : listExpenses) {
                    String[] strings = x.split("/");
                    String[] dateStrings = strings[0].split("\\.");
                    int day = Integer.parseInt(dateStrings[0]);
                    int month = Integer.parseInt(dateStrings[1]);
                    int year = Integer.parseInt(dateStrings[2]);
                    calendar = new GregorianCalendar(year, month, day);
                    nameMyAccount = strings[1];
                    category = strings[2];
                    subcategory = strings[3];
                    quantity = Double.parseDouble(strings[4]);
                    unitMeasure = strings[5];
                    sum = Double.parseDouble(strings[6]);
                    note = (strings[7].equals("1")) ? "" : strings[7];
                    expensesSet.add(new MyExpense(category, subcategory, nameMyAccount, calendar, quantity, unitMeasure, sum, note));
                }
            }
            // Ресурсы которые требуют освобождения освобождают в блоке finally
            // До этого места код может и не дойти
            // А вообще лучше испольовать try-with-resources
            readerExpenses.close();
        } catch (IOException e){
            e.printStackTrace();
        }

        // Вообще такой подход к чтению файлов устарел я бы следал как-то так
        // См. streams
        // expensesSet = Files.lines(Paths.get("path.txt"))
        //        .map(line -> new MyExpense(...))
        //        .collect(Collectors.toList())
    }

    /**
     * метод для заполнения списка доходов, записанных в файле
     * то же что и в предыдущем методе
     */
    public void fillEarningsSet() {
        GregorianCalendar calendar;
        try{
            //заполнение списка Доходов
            File fileInputEarning = new File(System.getProperty("user.dir") + "\\Data\\EarningSet.txt");
            BufferedReader readerEarning = new BufferedReader(new InputStreamReader(new FileInputStream(fileInputEarning), "UTF-8"));
            ArrayList<String> listEarning = new ArrayList<>();
            String lineEarning;
            while ((lineEarning = readerEarning.readLine()) != null) {
                listEarning.add(lineEarning);
            }

            String nameMyAccountEarn;
            String categoryEarn;
            String subcategoryEarn;
            double sumEarn;
            String noteEarn;
            if (listEarning.size() > 0) {
                for (String x : listEarning) {
                    String[] strings = x.split("/");
                    String[] dateStrings = strings[0].split("\\.");
                    int day = Integer.parseInt(dateStrings[0]);
                    int month = Integer.parseInt(dateStrings[1]);
                    int year = Integer.parseInt(dateStrings[2]);
                    calendar = new GregorianCalendar(year, month, day);
                    nameMyAccountEarn = strings[1];
                    categoryEarn = strings[2];
                    subcategoryEarn = strings[3];
                    sumEarn = Double.parseDouble(strings[4]);
                    noteEarn = (strings[5].equals("1")) ? "" : strings[5];
                    earningsSet.add(new MyEarning(calendar, nameMyAccountEarn, categoryEarn, subcategoryEarn, sumEarn, noteEarn));
                }
            }
            readerEarning.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    //метод для заполнения таблицы со счетами, таблицы с расходами и таблицы с доходами
    public void fillTables() {
        // Тут видимо нужен values() а еще можно испольовать forEach((key, value) -> {})
        Set<Map.Entry<String, MyAccount>> entrySet = accountsMap.entrySet();

        for (Map.Entry<String, MyAccount> x : entrySet) {
            MyAccount myAccount = x.getValue();
            accounts.getTableModelAccount().addRow(new Object[]{myAccount.getName(), myAccount.getExpenses(),
                    myAccount.getEarnings(), myAccount.getStartBalance(), myAccount.getBalance()});
        }

        // Может сделать все за один проход?
        for (MyExpense x : expensesSet) {
            expenses.getTableModelExpense().addRow(new Object[]{x.getGregorianCalendar(), x.getNameMyAccount(),
                    x.getCategory(), x.getSubcategory(),
                    x.getQuantity(), x.getUnitMeasure(), x.getSum(), x.getNote()});
        }

        for (MyEarning x : earningsSet) {
            earnings.getTableModelEarning().addRow(new Object[]{x.getGregorianCalendar(), x.getNameMyAccount(),
                    x.getCategory(), x.getSubcategory(),
                    x.getSum(), x.getNote()});
        }
    }

    public class AddAccountActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            edit = false;
            ArrayList<MyAccount> tempListAccounts = new ArrayList<>();
            Set<String> namesAccountsSet = accountsMap.keySet();

            // Нам настолько важно повторное использование диалогового окна что мы выделили под него
            // целое поле и возимся с ленивой инициализацией?

            //при первом обращении конструируется диалоговое окно
            if (dialogAccount == null) dialogAccount = new DialogAccount();
            //показать диалоговое окно
            if (dialogAccount.showAddDialogAccount(IncomeExpense.this, "Добавление счета", namesAccountsSet, tempListAccounts, edit)) {
                //если пользователь подтвердил введеные данные (нажал кнопку  Ok в диалоговом окне),
                //заполнить таблицу полученными данными
                for(MyAccount x : tempListAccounts){
                    accountsMap.put(x.getName(), x);
                    int indexMyAccount = accountsMap.headMap(x.getName()).size();
                    accounts.getTableModelAccount().insertRow(indexMyAccount, new Object[]{x.getName(), x.getExpenses(),
                            x.getEarnings(), x.getStartBalance(), x.getBalance()});
                }
            }
            tempListAccounts.removeAll(new ArrayList<>(tempListAccounts));
            dialogAccount.setTodayDate();
        }
    }

    public class EditAccountActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            edit = true;
            int row = accounts.getAccountTable().getSelectedRow();
            if (row == -1) return;//если строка не выбрана, не показывать диалоговое окно для редактирования
            Set<String> namesAccountsSet = accountsMap.keySet();
            String nameOldAccount = (String) accounts.getTableModelAccount().getValueAt(row, 0);
            MyAccount oldAccount = accountsMap.get(nameOldAccount);

            //при первом обращении конструируется диалоговое окно
            if (dialogAccount == null) dialogAccount = new DialogAccount();
            //показать диалоговое окно
            if (dialogAccount.showEditDialogAccount(IncomeExpense.this, "Редактирование счета",
                    namesAccountsSet, oldAccount, edit)) {
                //если пользователь подтвердил введеные данные,
                //извлечь их для последующей обработки

                MyAccount myAccount = dialogAccount.getMyAccount();

                Double newBalance = myAccount.getEarnings() + myAccount.getStartBalance() - myAccount.getExpenses();
                newBalance = new BigDecimal(newBalance).setScale(2, RoundingMode.HALF_UP).doubleValue();
                myAccount.setBalance(newBalance);
                accountsMap.remove(nameOldAccount);
                accountsMap.put(myAccount.getName(), myAccount);

                MyExpense myExpense;
                ArrayList<MyExpense> expensesList = new ArrayList<>();
                Iterator<MyExpense> expenseIterator = expensesSet.iterator();
                for (int i = 0; i < expensesSet.size();){
                    myExpense = expenseIterator.next();
                    if(myExpense.getNameMyAccount().equals(nameOldAccount)){
                        myExpense.setNameMyAccount(myAccount.getName());
                        expensesList.add(myExpense);
                        expenseIterator.remove();
                        expenses.getTableModelExpense().removeRow(i);
                    } else i++;
                }
                for(MyExpense x : expensesList) {
                    expensesSet.add(x);
                    int indexExpense = expensesSet.headSet(x).size();
                    expenses.getTableModelExpense().insertRow(indexExpense, new Object[]{x.getGregorianCalendar(),
                            x.getNameMyAccount(),
                            x.getCategory(), x.getSubcategory(), x.getQuantity(),
                            x.getUnitMeasure(), x.getSum(), x.getNote()});
                }

                MyEarning myEarning;
                ArrayList<MyEarning> earningsList = new ArrayList<>();
                Iterator<MyEarning> earningIterator = earningsSet.iterator();
                for(int i = 0; i < earningsSet.size(); ){
                    myEarning = earningIterator.next();
                    if (myEarning.getNameMyAccount().equals(nameOldAccount)){
                        myEarning.setNameMyAccount(myAccount.getName());
                        earningsList.add(myEarning);
                        earningIterator.remove();
                        earnings.getTableModelEarning().removeRow(i);
                    } else i++;
                }
                for(MyEarning x : earningsList){
                    earningsSet.add(x);
                    int indexEarning = earningsSet.headSet(x).size();
                    earnings.getTableModelEarning().insertRow(indexEarning, new Object[]{x.getGregorianCalendar(),
                            x.getNameMyAccount(), x.getCategory(), x.getSubcategory(), x.getSum(), x.getNote()});
                }

                //задание новых значений в редактируемой строке
                accounts.getTableModelAccount().removeRow(row);
                int indexNewAccount = accountsMap.headMap(myAccount.getName()).size();
                accounts.getTableModelAccount().insertRow(indexNewAccount, new Object[]{myAccount.getName(),
                        myAccount.getExpenses(), myAccount.getEarnings(), myAccount.getStartBalance(), myAccount.getBalance()});

            }
            dialogAccount.setTodayDate();
        }
    }

    public class RemoveAccActionListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            int row = accounts.getAccountTable().getSelectedRow();
            if (row == -1) return;

            String nameOldAccount = (String) accounts.getTableModelAccount().getValueAt(row, 0);
            String s = "Удалить счет \"" + nameOldAccount + "\" и все связанные с ним данные?";
            int selection =  JOptionPane.showConfirmDialog(IncomeExpense.this, s, "Подтверждение",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (selection == JOptionPane.OK_OPTION){
                MyExpense myExpense;
                Iterator<MyExpense> expenseIterator = expensesSet.iterator();
                for (int i = 0; i < expensesSet.size(); ){
                    myExpense = expenseIterator.next();
                    if(myExpense.getNameMyAccount().equals(nameOldAccount)){
                        expenses.getTableModelExpense().removeRow(i);
                        expenseIterator.remove();
                    } else i++;
                }

                MyEarning myEarning;
                Iterator<MyEarning> earningIterator = earningsSet.iterator();
                for(int i = 0; i < earningsSet.size();){
                    myEarning = earningIterator.next();
                    if (myEarning.getNameMyAccount().equals(nameOldAccount)){
                        earnings.getTableModelEarning().removeRow(i);
                        earningIterator.remove();
                    } else i++;
                }
                accounts.getTableModelAccount().removeRow(row);
                accountsMap.remove(nameOldAccount);
            }
        }
    }

    public class AddExpenseActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (accountsMap.size() == 0) return;
            ArrayList<MyExpense> tempExpensesList = new ArrayList<>();
            Set<String> namesAccountsSet = accountsMap.keySet();
            edit = false;
            //при первом обращении конструируется диалоговое окно
            if (dialogExpense == null) {
                File file = new File(System.getProperty("user.dir") + "\\Data\\CategoryExpenses.txt");
                dialogExpense = new DialogExpense(file);
            }
            //показать диалоговое окно
            if (dialogExpense.showDialogExpense(IncomeExpense.this, "Добавление расхода", namesAccountsSet, tempExpensesList, edit)) {
                //если пользователь подтвердил введеные данные,
                //извлечь их для последующей обработки
                for (MyExpense x : tempExpensesList){
                    expensesSet.add(x);
                    int indexExpense = expensesSet.headSet(x).size();
                    expenses.getTableModelExpense().insertRow(indexExpense, new Object[]{x.getGregorianCalendar(),
                            x.getNameMyAccount(),
                            x.getCategory(), x.getSubcategory(), x.getQuantity(),
                            x.getUnitMeasure(), x.getSum(), x.getNote()});

                    //списание с выбранного счета суммы расхода
                    String nameAccount = x.getNameMyAccount();
                    MyAccount myAccount = accountsMap.get(nameAccount);
                    int indexAccount = accountsMap.headMap(nameAccount).size();
                    Double expense = myAccount.getExpenses() + x.getSum();
                    expense = new BigDecimal(expense).setScale(2, RoundingMode.HALF_UP).doubleValue();
                    Double balance = myAccount.getBalance() - x.getSum();
                    balance = new BigDecimal(balance).setScale(2, RoundingMode.HALF_UP).doubleValue();
                    myAccount.setExpenses(expense);
                    myAccount.setBalance(balance);
                    accountsMap.remove(nameAccount);
                    accountsMap.put(nameAccount, myAccount);
                    accounts.getTableModelAccount().setValueAt(myAccount.getExpenses(), indexAccount, 1);
                    accounts.getTableModelAccount().setValueAt(myAccount.getBalance(), indexAccount, 4);
                }
            }
            dialogExpense.setTodayDate();
        }
    }

    public class EditExpenseActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Set<String> namesAccountsSet = accountsMap.keySet();
            edit = true;
            int row = expenses.getExpenseTable().getSelectedRow();
            if (row == -1) return;

            //получение суммы расхода до редактирования
            MyExpense expenseBefore = null;
            Iterator<MyExpense> expenseIterator = expensesSet.iterator();
            for (int i = 0; i < row + 1; i++) expenseBefore = expenseIterator.next();
            String myAccountBefore = expenseBefore.getNameMyAccount();
            double sumBefore = expenseBefore.getSum();


            //при первом обращении конструируется диалоговое окно
            if (dialogExpense == null) {
                File file = new File(System.getProperty("user.dir") + "\\Data\\CategoryExpenses.txt");
                dialogExpense = new DialogExpense(file);
            }
            //показать диалоговое окно
            if (dialogExpense.showEditDialogExpense(IncomeExpense.this, "Редактирование расхода", namesAccountsSet,
                    expenseBefore, edit)) {
                //если пользователь подтвердил введеные данные,
                //извлечь их для последующей обработки

                MyExpense newExpense = dialogExpense.getMyExpense();
                expensesSet.remove(expenseBefore);
                expensesSet.add(newExpense);
                int indexExpense = expensesSet.headSet(newExpense).size();

                //назначение новых данных для таблицы модели в отредактированной строке

                expenses.getTableModelExpense().removeRow(row);
                expenses.getTableModelExpense().insertRow(indexExpense, new Object[]{
                        newExpense.getGregorianCalendar(), newExpense.getNameMyAccount(), newExpense.getCategory(),
                        newExpense.getSubcategory(), newExpense.getQuantity(), newExpense.getUnitMeasure(),
                        newExpense.getSum(), newExpense.getNote()
                });

                String nameAccountNewExpense = newExpense.getNameMyAccount();
                if (myAccountBefore.equals(nameAccountNewExpense)){
                    //если счет списания не менялся
                    //списание с выбранного счета суммы расхода с учетом суммы расхода до редактирования
                    MyAccount myAccount = accountsMap.get(myAccountBefore);
                    int indexAccount = accountsMap.headMap(myAccountBefore).size();
                    Double expense = myAccount.getExpenses() + newExpense.getSum() - sumBefore;
                    expense = new BigDecimal(expense).setScale(2, RoundingMode.HALF_UP).doubleValue();
                    Double balance = myAccount.getBalance() + sumBefore - newExpense.getSum();
                    balance = new BigDecimal(balance).setScale(2, RoundingMode.HALF_UP).doubleValue();
                    myAccount.setExpenses(expense);
                    myAccount.setBalance(balance);
                    accountsMap.remove(myAccountBefore);
                    accountsMap.put(myAccountBefore, myAccount);
                    accounts.getTableModelAccount().setValueAt(myAccount.getExpenses(), indexAccount, 1);
                    accounts.getTableModelAccount().setValueAt(myAccount.getBalance(), indexAccount, 4);
                } else {
                    //при смене Счета для списания денег
                    //сначала вернуть на старый счет деньги
                    MyAccount oldMyAccount = accountsMap.get(myAccountBefore); //старый счет списания
                    Double expense = oldMyAccount.getExpenses() - sumBefore;
                    expense = new BigDecimal(expense).setScale(2, RoundingMode.HALF_UP).doubleValue();
                    Double balance = oldMyAccount.getBalance() + sumBefore;
                    balance = new BigDecimal(balance).setScale(2, RoundingMode.HALF_UP).doubleValue();
                    oldMyAccount.setExpenses(expense);
                    oldMyAccount.setBalance(balance);
                    int indexOld = accountsMap.headMap(myAccountBefore).size();
                    accountsMap.remove(oldMyAccount.getName());
                    accountsMap.put(oldMyAccount.getName(), oldMyAccount);
                    accounts.getTableModelAccount().setValueAt(oldMyAccount.getExpenses(), indexOld, 1);
                    accounts.getTableModelAccount().setValueAt(oldMyAccount.getBalance(), indexOld, 4);
                    accounts.getAccountTable().revalidate();

                    //списать с нового счета сумму
                    MyAccount newMyAccount = accountsMap.get(nameAccountNewExpense);
                    Double expenseNew = newMyAccount.getExpenses() + newExpense.getSum();
                    expenseNew = new BigDecimal(expenseNew).setScale(2, RoundingMode.HALF_UP).doubleValue();
                    Double balanceNew = newMyAccount.getBalance() - newExpense.getSum();
                    balanceNew = new BigDecimal(balanceNew).setScale(2, RoundingMode.HALF_UP).doubleValue();
                    newMyAccount.setExpenses(expenseNew);
                    newMyAccount.setBalance(balanceNew);
                    int indexNew = accountsMap.headMap(nameAccountNewExpense).size();
                    accountsMap.remove(newMyAccount.getName());
                    accountsMap.put(newMyAccount.getName(), newMyAccount);
                    accounts.getTableModelAccount().setValueAt(newMyAccount.getExpenses(), indexNew, 1);
                    accounts.getTableModelAccount().setValueAt(newMyAccount.getBalance(), indexNew, 4);

                }

            }
            dialogExpense.setTodayDate();
        }

    }

    public class RemoveExpenseActionListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            int row = expenses.getExpenseTable().getSelectedRow();
            if (row == -1){
                return;
            }
            MyExpense removeExpense = null;
            Iterator<MyExpense> expenseIterator = expensesSet.iterator();
            for (int i = 0; i < row + 1; i++) removeExpense = expenseIterator.next();
            if(removeExpense != null) {
                String strDate = String.format("%1$td.%1$tm.%1$tY", removeExpense.getGregorianCalendar());
                String myExpenseString = strDate + " " + removeExpense.getNameMyAccount() + " " + removeExpense.getCategory() + " " + removeExpense.getSum();
                String s = "Удалить расход \"" + myExpenseString + "\" и связанные с ним данные?";
                int selection = JOptionPane.showConfirmDialog(IncomeExpense.this, s, "Подтверждение",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (selection == JOptionPane.OK_OPTION){
                    expenses.getTableModelExpense().removeRow(row);

                    String nameAccount = removeExpense.getNameMyAccount();
                    MyAccount myAccount = accountsMap.get(nameAccount);
                    int indexAccount = accountsMap.headMap(nameAccount).size();
                    Double expense;
                    Double balance;
                    try{
                        expense = myAccount.getExpenses() - removeExpense.getSum();
                        expense = new BigDecimal(expense).setScale(2, RoundingMode.HALF_UP).doubleValue();
                        balance = myAccount.getBalance() + removeExpense.getSum();
                        balance = new BigDecimal(balance).setScale(2, RoundingMode.HALF_UP).doubleValue();
                        myAccount.setExpenses(expense);
                        myAccount.setBalance(balance);
                        accounts.getTableModelAccount().setValueAt(myAccount.getExpenses(), indexAccount, 1);
                        accounts.getTableModelAccount().setValueAt(myAccount.getBalance(), indexAccount, 4);
                        accountsMap.remove(nameAccount);
                        accountsMap.put(nameAccount, myAccount);
                    } catch (NullPointerException ex) {
                        ex.printStackTrace();
                    }
                    expensesSet.remove(removeExpense);
                }
            }

        }
    }



    public class AddEarnActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (accountsMap.size() == 0) return;
            ArrayList<MyEarning> tempEarningsList = new ArrayList<>();
            Set<String> namesAccountsSet = accountsMap.keySet();
            edit = false;
            //при первом обращении конструируется диалоговое окно
            //int oldSize = earningsList.size();// длина спика доходов до добавления нового дохода или нескольких доходов
            if (dialogEarning == null) {
                File file = new File(System.getProperty("user.dir") + "\\Data\\CategoryEarnings.txt");
                dialogEarning = new DialogEarning(file);
            }
            //показать диалоговое окно
            if (dialogEarning.showDialogEarning(IncomeExpense.this, "Добавление дохода", namesAccountsSet, tempEarningsList, edit)) {
                //если пользователь подтвердил введеные данные,
                //извлечь их для последующей обработки

                for(MyEarning x : tempEarningsList){
                    //добавлить в таблицу доходов стоку с новым доходом
                    earningsSet.add(x);
                    int indexEarning = earningsSet.headSet(x).size();
                    earnings.getTableModelEarning().insertRow(indexEarning, new Object[]{x.getGregorianCalendar(),
                            x.getNameMyAccount(), x.getCategory(), x.getSubcategory(),
                            x.getSum(), x.getNote()});

                    //добавление на счет суммы дохода
                    MyAccount myAccount = accountsMap.get(x.getNameMyAccount());
                    int indexAccount = accountsMap.headMap(x.getNameMyAccount()).size();
                    Double earn = myAccount.getEarnings() + x.getSum();
                    Double balance = myAccount.getBalance() + x.getSum();
                    earn = new BigDecimal(earn).setScale(2, RoundingMode.HALF_UP).doubleValue();
                    balance = new BigDecimal(balance).setScale(2, RoundingMode.HALF_UP).doubleValue();
                    myAccount.setEarnings(earn);
                    myAccount.setBalance(balance);
                    accountsMap.remove(x.getNameMyAccount());
                    accountsMap.put(x.getNameMyAccount(), myAccount);
                    accounts.getTableModelAccount().setValueAt(myAccount.getEarnings(), indexAccount, 2);
                    accounts.getTableModelAccount().setValueAt(myAccount.getBalance(), indexAccount, 4);
                }
            }
            dialogEarning.setTodayDate();

        }
    }

    public class EditEarnActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Set<String> namesAccountsSet = accountsMap.keySet();
            edit = true;
            int row = earnings.getEarningTable().getSelectedRow();
            if (row == -1) {
                return;
            }

            MyEarning earningBefore = null; //доход до редактирования
            Iterator<MyEarning> earningIterator = earningsSet.iterator();
            for (int i = 0; i < row + 1; i++){
                earningBefore = earningIterator.next();
            }

            String accountBefore = earningBefore.getNameMyAccount(); //название счета до редактирования
            double sumBefore = earningBefore.getSum(); //значение дохода до редактирования

            //при первом обращении конструируется диалоговое окно
            if (dialogEarning == null) {
                File file = new File(System.getProperty("user.dir") + "\\Data\\CategoryEarnings.txt");
                dialogEarning = new DialogEarning(file);
            }
            //показать диалоговое окно
            if (dialogEarning.showEditDialogEarning(IncomeExpense.this, "Редактирование дохода", namesAccountsSet,
                                                    earningBefore, edit)) {
                //если пользователь подтвердил введеные данные,
                //извлечь их для последующей обработки


                MyEarning myEarning = dialogEarning.getMyEarning();
                earningsSet.remove(earningBefore);
                earningsSet.add(myEarning);
                int indexEarning = earningsSet.headSet(myEarning).size();

                //назначение новых данных в каждой ячейке выбранной строки
                earnings.getTableModelEarning().removeRow(row);
                earnings.getTableModelEarning().insertRow(indexEarning, new Object[]{
                        myEarning.getGregorianCalendar(),
                        myEarning.getNameMyAccount(), myEarning.getCategory(), myEarning.getSubcategory(),
                        myEarning.getSum(), myEarning.getNote()
                });

                if (myEarning.getNameMyAccount().equals(accountBefore)) {
                    //если счет зачисления не менялся
                    //изменение зачисляемой суммы от данного дохода
                    MyAccount myAccount = accountsMap.get(accountBefore);
                    Double earn = myAccount.getEarnings() + myEarning.getSum() - sumBefore;
                    Double balance = myAccount.getBalance() - sumBefore + myEarning.getSum();
                    earn = new BigDecimal(earn).setScale(2, RoundingMode.HALF_UP).doubleValue();
                    balance = new BigDecimal(balance).setScale(2, RoundingMode.HALF_UP).doubleValue();
                    myAccount.setEarnings(earn);
                    myAccount.setBalance(balance);//изменить сумму зачисления на счет от данного дохода
                    accountsMap.remove(accountBefore);
                    accountsMap.put(accountBefore, myAccount);
                    int indexAccount = accountsMap.headMap(accountBefore).size();
                    accounts.getTableModelAccount().setValueAt(myAccount.getEarnings(), indexAccount, 2);
                    accounts.getTableModelAccount().setValueAt(myAccount.getBalance(), indexAccount, 4);
                } else {
                    // при смене счета для зачисления денег
                    //сначала списать со старого счета ранее зачисленную сумму
                    MyAccount myAccountOld = accountsMap.get(accountBefore);
                    Double earn = myAccountOld.getEarnings() - sumBefore;
                    earn = new BigDecimal(earn).setScale(2, RoundingMode.HALF_UP).doubleValue();
                    Double balance = myAccountOld.getBalance() - sumBefore;
                    balance = new BigDecimal(balance).setScale(2, RoundingMode.HALF_UP).doubleValue();
                    myAccountOld.setEarnings(earn);
                    myAccountOld.setBalance(balance); //списание со старого счета ранее добавленного зачисления
                    int indexOldAccount = accountsMap.headMap(accountBefore).size();
                    accountsMap.remove(accountBefore);
                    accountsMap.put(accountBefore, myAccountOld);
                    accounts.getTableModelAccount().setValueAt(myAccountOld.getEarnings(), indexOldAccount, 2);
                    accounts.getTableModelAccount().setValueAt(myAccountOld.getBalance(), indexOldAccount, 4);

                    //добавить на новый счет сумму
                    String nameNewAccount = myEarning.getNameMyAccount();
                    MyAccount newMyAccount = accountsMap.get(nameNewAccount);
                    Double earnNew = newMyAccount.getEarnings() + myEarning.getSum();
                    Double balanceNew = newMyAccount.getBalance() + myEarning.getSum();
                    earnNew = new BigDecimal(earnNew).setScale(2, RoundingMode.HALF_UP).doubleValue();
                    balanceNew = new BigDecimal(balanceNew).setScale(2, RoundingMode.HALF_UP).doubleValue();
                    newMyAccount.setEarnings(earnNew);
                    newMyAccount.setBalance(balanceNew); //добавить на новый счет сумму
                    int indexNewAccount = accountsMap.headMap(nameNewAccount).size();
                    accountsMap.remove(nameNewAccount);
                    accountsMap.put(nameNewAccount, newMyAccount);
                    accounts.getTableModelAccount().setValueAt(newMyAccount.getEarnings(), indexNewAccount, 2);
                    accounts.getTableModelAccount().setValueAt(newMyAccount.getBalance(), indexNewAccount, 4);
                }
            }
            dialogEarning.setTodayDate();
        }
    }

    public class RemoveEarnActionListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            int row = earnings.getEarningTable().getSelectedRow();
            if (row == -1){
                return;
            }
            MyEarning earningRemove = null; //доход до редактирования
            Iterator<MyEarning> earningIterator = earningsSet.iterator();
            for (int i = 0; i < row + 1; i++){
                earningRemove = earningIterator.next();
            }
            if (earningRemove != null){
                String strDate = String.format("%1$td.%1$tm.%1$tY", earningRemove.getGregorianCalendar());
                String myEarningString = strDate + " " + earningRemove.getNameMyAccount() + " " + earningRemove.getCategory() + " " + earningRemove.getSum();
                String s = "Удалить доход \"" + myEarningString + "\" и связанные с ним данные?";
                int selection = JOptionPane.showConfirmDialog(IncomeExpense.this, s, "Подтверждение", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (selection == JOptionPane.OK_OPTION){
                    earnings.getTableModelEarning().removeRow(row);
                    String nameMyAccount = earningRemove.getNameMyAccount();
                    MyAccount myAccount = accountsMap.get(nameMyAccount);
                    int indexAccount = accountsMap.headMap(nameMyAccount).size();
                    Double earn;
                    Double balance;
                    try{
                        earn = myAccount.getEarnings() - earningRemove.getSum();
                        balance = myAccount.getBalance() - earningRemove.getSum();
                        earn = new BigDecimal(earn).setScale(2, RoundingMode.HALF_UP).doubleValue();
                        balance = new BigDecimal(balance).setScale(2, RoundingMode.HALF_UP).doubleValue();
                        myAccount.setBalance(balance);
                        myAccount.setEarnings(earn);
                        accounts.getTableModelAccount().setValueAt(myAccount.getEarnings(), indexAccount, 2);
                        accounts.getTableModelAccount().setValueAt(myAccount.getBalance(), indexAccount, 4);
                        accountsMap.remove(nameMyAccount);
                        accountsMap.put(nameMyAccount, myAccount);
                    } catch (NullPointerException ex){
                        ex.printStackTrace();
                    }
                    earningsSet.remove(earningRemove);
                }
            }
        }
    }

    public TreeMap<String, MyAccount> getAccountsMap() {
        return accountsMap;
    }

    public TreeSet<MyExpense> getExpensesSet() {
        return expensesSet;
    }

    public TreeSet<MyEarning> getEarningsSet() {
        return earningsSet;
    }


   public class InlayListener implements ChangeListener{
       @Override
       public void stateChanged(ChangeEvent e) {
           JTabbedPane jTabbedPane = (JTabbedPane) e.getSource();
           if(jTabbedPane.getSelectedIndex() == 3) {
               panelChart.updateAccounts();
           }

       }
   }


}
