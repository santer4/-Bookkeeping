import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.nio.file.Files;
import java.util.*;


/**
 * Главное окно приложения
 */
public class FrameBookkeeping extends JFrame {
    private IncomeExpense tabbedPane; //компонент с вкладками Расходы, Доходы, Счета
    private final static int DEFAULT_WIDTH = 900;
    private final static int DEFAULT_HEIGHT = 500;


    public FrameBookkeeping(){
        tabbedPane = new IncomeExpense();
        tabbedPane.setFont(new Font("SansSerif", Font.PLAIN, 14));

        setSize(DEFAULT_WIDTH,DEFAULT_HEIGHT);
        setMinimumSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        setLocationRelativeTo(null);
        add(tabbedPane, BorderLayout.CENTER);
        addWindowListener(new BookkeepingWindowListener());
    }

    public class BookkeepingWindowListener extends WindowAdapter{
        @Override
        public void windowClosed(WindowEvent e) {
            // Метод переопределять необязательно
        }

        @Override
        public void windowClosing(WindowEvent e) {
            TreeMap<String, MyAccount> accountsMap = tabbedPane.getAccountsMap();
            Set<String> namesAccountsSet = accountsMap.keySet();
            TreeSet<MyExpense> expensesSet = tabbedPane.getExpensesSet();
            TreeSet<MyEarning> earningsSet = tabbedPane.getEarningsSet();

            if(accountsMap.size() > 0){
                try{
                    File fileOutput = new File(System.getProperty("user.dir") + "\\Data\\AccountMap.txt");
                    // Files.newBufferedWriter()
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileOutput), "UTF-8"));
                    for(String nameAccount : namesAccountsSet){
                        MyAccount x = accountsMap.get(nameAccount);
                        String name = x.getName();
                        double expenses = x.getExpenses();
                        double earnings = x.getEarnings();
                        double startBalance = x.getStartBalance();
                        double balance = x.getBalance();
                        GregorianCalendar calendar = x.getGregorianCalendar();
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        int month = calendar.get(Calendar.MONTH);
                        int year = calendar.get(Calendar.YEAR);
                        writer.write(name + "/" + expenses + "/" + earnings + "/" + startBalance + "/" +
                                balance + "/" + day + "." + month + "." + year + "\r\n");
                    }
                    writer.close();
                } catch (IOException exception){
                    exception.printStackTrace();
                }
                // accountMap.isEmpty()
            } else if (accountsMap.size() == 0){
                try {
                    // Логику записи пустой и непустой коллекции лучше объединить
                    File fileOutput = new File(System.getProperty("user.dir") + "\\Data\\AccountMap.txt");
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileOutput), "UTF-8"));
                    writer.write("");
                    writer.close();
                } catch (IOException exception){
                    exception.printStackTrace();
                }

            }
            // Логика записи файлов с разными сущьностями отлиается только тем как сущьность трансформируется в
            // строку. Надо подумать над обстракцией
            if (expensesSet.size() > 0){
                try {
                    File fileOutputExpenses = new File(System.getProperty("user.dir") + "\\Data\\ExpenseSet.txt");
                    BufferedWriter writerExpenses = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileOutputExpenses), "UTF-8"));
                    for(MyExpense x : expensesSet){
                        GregorianCalendar calendar = x.getGregorianCalendar();
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        int month = calendar.get(Calendar.MONTH);
                        int year = calendar.get(Calendar.YEAR);
                        String nameMyAccount = x.getNameMyAccount();
                        String category = x.getCategory();
                        String subcategory = x.getSubcategory();
                        double quantity = x.getQuantity();
                        String unitMeasure = x.getUnitMeasure();
                        double sum = x.getSum();
                        String note =  (x.getNote().equals("") ) ? "1" : x.getNote();
                        writerExpenses.write(day + "." + month + "." + year+"/" + nameMyAccount +"/" +
                                category + "/" + subcategory + "/" + quantity + "/" + unitMeasure + "/" +
                                sum + "/" + note + "\r\n");
                    }
                    writerExpenses.close();
                } catch (IOException exc){
                    exc.printStackTrace();
                }
            } else if(expensesSet.size() == 0){
                try {
                    File fileOutputExpenses = new File(System.getProperty("user.dir") + "\\Data\\ExpenseSet.txt");
                    BufferedWriter writerExpenses = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileOutputExpenses), "UTF-8"));
                    writerExpenses.write("");
                    writerExpenses.close();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }

            }
            if(earningsSet.size() > 0){
                try {
                    File fileOutputEarn = new File(System.getProperty("user.dir") + "\\Data\\EarningSet.txt");
                    BufferedWriter writerEarn = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileOutputEarn), "UTF-8"));
                    for(MyEarning x : earningsSet){
                        GregorianCalendar calendar = x.getGregorianCalendar();
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        int month = calendar.get(Calendar.MONTH);
                        int year = calendar.get(Calendar.YEAR);
                        String nameMyAccount = x.getNameMyAccount();
                        String category = x.getCategory();
                        String subcategory = x.getSubcategory();
                        double sum = x.getSum();
                        String note =  (x.getNote().equals("") ) ? "1" : x.getNote();
                        writerEarn.write(day + "." + month + "." + year+"/" + nameMyAccount +"/" +
                                category + "/" + subcategory + "/" +
                                sum + "/" + note + "\r\n");
                    }
                    writerEarn.close();
                } catch (IOException exc){
                    exc.printStackTrace();
                }
            } else if (earningsSet.size() == 0){
                try {
                    File fileOutputEarn = new File(System.getProperty("user.dir") + "\\Data\\EarningSet.txt");
                    BufferedWriter writerEarn = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileOutputEarn), "UTF-8"));
                    writerEarn.write("");
                    writerEarn.close();
                }catch (IOException exception){
                    exception.printStackTrace();
                }
            }
            dispose();
        }
    }
}
