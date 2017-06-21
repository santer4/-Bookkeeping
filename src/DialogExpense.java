import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;

/**
 * Created by Alexandr on 18.03.2017.
 */

public class DialogExpense extends DialogEarning{
    private JLabel accountLabel;
    private MoneyTextField quantityTextField;
    private Vector<String> unitMeasure;
    private DefaultComboBoxModel<String> unitMeasureModel;
    private JComboBox<String> unitMeasureComboBox;
    private ArrayList<MyExpense> tempExpensesList;

    public DialogExpense(final File fileCategory){
        super(fileCategory);

        JPanel mainPanel = getMainPanel();
        mainPanel.setLayout(new GridLayout(8,2));

        accountLabel = (JLabel) mainPanel.getComponent(2);
        accountLabel.setText("Списать со счета");

        JLabel sumLabel = getSumLabel();
        MoneyTextField sumField = getSumTextField();
        JLabel noteLabel = getNoteLabel();
        NoteTextField noteField = getNoteTextField();

        mainPanel.remove(8);
        mainPanel.remove(8);
        mainPanel.remove(8);
        mainPanel.remove(8);

        mainPanel.add(new JLabel("Количество"));
        quantityTextField = new MoneyTextField(3);
        mainPanel.add(quantityTextField);
        mainPanel.add(new JLabel("Единицы измерения"));
        unitMeasure = new Vector<>();
        fillUnitMeasure();
        unitMeasureModel = new DefaultComboBoxModel<>(unitMeasure);
        unitMeasureComboBox = new JComboBox<>(unitMeasureModel);
        mainPanel.add(unitMeasureComboBox);

        mainPanel.add(sumLabel);
        mainPanel.add(sumField);
        mainPanel.add(noteLabel);
        mainPanel.add(noteField);

        Font font = new Font("SansSerif", Font.PLAIN, 14);
        Component[] componentsPanelDate = mainPanel.getComponents();
        for(Component x : componentsPanelDate) {
            x.setFont(font);
        }

        tempExpensesList = new ArrayList<>();

        ActionListener[] listeners = getAnotherButton().getActionListeners();
        getAnotherButton().removeActionListener(listeners[0]);
        getAnotherButton().addActionListener(new ExpenseAnotherListener());

        getOkButton().removeActionListener(getOkButton().getActionListeners()[0]);
        getOkButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String strSum = getSumTextField().getText();
                strSum = strSum.replace(new String(new char[]{160}), "");
                strSum = strSum.replace(",", ".");
                boolean startZero = true;
                if (strSum.matches("0+\\.0+")){
                    startZero = false;
                }
                while (startZero){
                    if (!strSum.startsWith("0")) startZero = false;
                    else strSum = strSum.replaceFirst("0", "");
                }
                if(strSum.isEmpty()){
                    String s = "Значение суммы расхода не может быть пустым";
                    JOptionPane.showMessageDialog(DialogExpense.this, s, "Предупреждение", JOptionPane.INFORMATION_MESSAGE);
                    return;
                } else if(Double.compare(Double.parseDouble(strSum), 0) == 0 ) {
                    String s = "Значение суммы расхода не может быть нулевым";
                    JOptionPane.showMessageDialog(DialogExpense.this, s, "Предупреждение", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                String strQuantity = quantityTextField.getText();
                strQuantity = strQuantity.replace(new String(new char[]{160}), "");
                strQuantity = strQuantity.replace(",", ".");
                boolean startZeroQuant = true;
                while (startZeroQuant){
                    if (!strQuantity.startsWith("0")) startZeroQuant = false;
                    else strQuantity = strQuantity.replaceFirst("0", "");
                }
                if(strQuantity.isEmpty()){
                    String s = "Значение количества не может быть пустым";
                    JOptionPane.showMessageDialog(DialogExpense.this, s, "Предупреждение", JOptionPane.INFORMATION_MESSAGE);
                } else if(Double.compare(Double.parseDouble(strQuantity), 0) == 0){
                    String s = "Значение количества не может быть нулевым";
                    JOptionPane.showMessageDialog(DialogExpense.this, s, "Предупреждение", JOptionPane.INFORMATION_MESSAGE);
                } else if (!isEdit()) {
                    tempExpensesList.add(getMyExpense());
                    setOk(true);
                    getDialog().setVisible(false);
                } else {
                    setOk(true);
                    getDialog().setVisible(false);
                }
            }
        });
    }

    public void fillUnitMeasure(){
        try{
            File fileUnitMeasure = new File(System.getProperty("user.dir") + "\\Data\\UnitMeasure.txt");
            BufferedReader readerUnit = new BufferedReader(new InputStreamReader(new FileInputStream(fileUnitMeasure), "UTF-8"));
            String line = readerUnit.readLine();
            String[] strings = line.split("/");
            for(String x : strings) unitMeasure.add(x);
            readerUnit.close();
        } catch (IOException exception){
            exception.printStackTrace();
        }
    }


    public MyExpense getMyExpense(){
        //получение дня, месяца и года для календаря
        int day = (Integer) getDayComboBox().getSelectedItem();
        int month = getMonthComboBox().getSelectedIndex();
        int year = (Integer) getYearComboBox().getSelectedItem();
        String category = (String) getCategoryComboBox().getSelectedItem();
        String subcategory = (String) getSubcategoryComboBox().getSelectedItem();
        String nameMyAccount = (String) getNamesMyAccount().getSelectedItem();
        String quantityStr = quantityTextField.getText();
        quantityStr = quantityStr.replace(new String(new char[]{160}), "");
        quantityStr = quantityStr.replace(",", ".");
        boolean startZeroQuant = true;
        while (startZeroQuant){
            if (!quantityStr.startsWith("0")) startZeroQuant = false;
            else quantityStr = quantityStr.replaceFirst("0", "");
        }
        double quantity;
        if (quantityStr.contains(".")){
            quantity = Double.parseDouble(quantityStr);
        } else {
            quantity = Double.parseDouble(quantityStr) + 0.0001;
        }
        String unitMeasure = (String) unitMeasureComboBox.getSelectedItem();
        String sumStr = getSumTextField().getText();
        sumStr = sumStr.replace(new String(new char[]{160}), "");
        sumStr = sumStr.replace(",", ".");
        boolean startZero = true;
        if (sumStr.matches("0+\\.0+")){
            startZero = false;
        }

        while (startZero){
            if (!sumStr.startsWith("0")) startZero = false;
            else sumStr = sumStr.replaceFirst("0", "");
        }
        double sum = Double.parseDouble(sumStr);
        String note = getNoteTextField().getText();

        return new MyExpense(category, subcategory, nameMyAccount, new GregorianCalendar(year,month,day), quantity, unitMeasure,
                sum, note);
    }

    public boolean showDialogExpense(Component parent, String title, Set<String> namesAccountsSet,
                                     ArrayList<MyExpense> tempExpensesList, boolean edit) {
        setEdit(edit);
        setOk(false);
        getAnotherButton().setVisible(true);
        this.tempExpensesList = tempExpensesList;
        //создать диалоговое окно в унаследованном методе от DialogParent
        showDialog(parent, title);

        fillNamesMyAccount(namesAccountsSet);
        getSumTextField().setText("0.00");
        getNoteTextField().setText("");
        quantityTextField.setText("1");
        getDialog().setVisible(true);
        return isOk();
    }

    public boolean showEditDialogExpense(Component parent, String title, Set<String> namesAccountsSet, MyExpense editMyExpense, boolean edit){
        setEdit(edit);
        setOk(false);
        getAnotherButton().setVisible(false);

        //создать диалоговое окно в унаследованном методе от DialogParent
        showDialog(parent, title);

        fillNamesMyAccount(namesAccountsSet);

        //назначить значения полям диалогового окна
        GregorianCalendar calendar = editMyExpense.getGregorianCalendar();
        getYearComboBox().setSelectedItem(calendar.get(Calendar.YEAR));
        getMonthComboBox().setSelectedIndex(calendar.get(Calendar.MONTH));
        getDayComboBox().setSelectedItem(calendar.get(Calendar.DAY_OF_MONTH));
        String category = editMyExpense.getCategory();
        getCategoryComboBox().setSelectedItem(category);
        fillSubcategoryModel(category);
        getSubcategoryComboBox().setSelectedItem(editMyExpense.getSubcategory());
        String nameAccount = editMyExpense.getNameMyAccount();
        getNamesMyAccount().setSelectedItem(nameAccount);
        Double quantity = editMyExpense.getQuantity();
        String quantityStr = Double.toString(quantity);

        if (quantityStr.matches("\\d+\\.0001")){
            Integer count = quantity.intValue();
            quantityTextField.setText(count.toString());
        } else {
            quantityTextField.setText(quantityStr);
        }

        String unitMeasure = editMyExpense.getUnitMeasure();
        unitMeasureComboBox.setSelectedItem(unitMeasure);
        double sum = editMyExpense.getSum();
        getSumTextField().setText(Double.toString(sum));
        getNoteTextField().setText(editMyExpense.getNote());

        // показать диалоговое окно
        getDialog().setVisible(true); //переход к return ok после того как произошел вызов метода dialog.setVisible(false)
        // это произойдет при нажатии кнопки Ok или кнопки Canсel или при закрытии диалогового окна
        return isOk();
    }

    public class ExpenseAnotherListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            String strSum = getSumTextField().getText();
            strSum = strSum.replace(new String(new char[]{160}), "");
            strSum = strSum.replace(",", ".");
            boolean startZero = true;
            if (strSum.matches("0+\\.0+")){
                startZero = false;
            }
            while (startZero){
                if (!strSum.startsWith("0")) startZero = false;
                else strSum = strSum.replaceFirst("0", "");
            }
            if(strSum.isEmpty()){
                String s = "Значение суммы расхода не может быть пустым";
                JOptionPane.showMessageDialog(DialogExpense.this, s, "Предупреждение", JOptionPane.INFORMATION_MESSAGE);
                return;
            } else if(Double.compare(Double.parseDouble(strSum), 0) == 0 ) {
                String s = "Значение суммы расхода не может быть нулевым";
                JOptionPane.showMessageDialog(DialogExpense.this, s, "Предупреждение", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String strQuantity = quantityTextField.getText();
            strQuantity = strQuantity.replace(new String(new char[]{160}), "");
            strQuantity = strQuantity.replace(",", ".");
            boolean startZeroQuant = true;
            while (startZeroQuant){
                if (!strQuantity.startsWith("0")) startZeroQuant = false;
                else strQuantity = strQuantity.replaceFirst("0", "");
            }
            if(strQuantity.isEmpty()){
                String s = "Значение количества не может быть пустым";
                JOptionPane.showMessageDialog(DialogExpense.this, s, "Предупреждение", JOptionPane.INFORMATION_MESSAGE);

            } else if(Double.compare(Double.parseDouble(strQuantity), 0) == 0){
                String s = "Значение количества не может быть нулевым";
                JOptionPane.showMessageDialog(DialogExpense.this, s, "Предупреждение", JOptionPane.INFORMATION_MESSAGE);
            } else {
                tempExpensesList.add(getMyExpense());
                quantityTextField.setText("1");
                getSumTextField().setText("0.00");
                getNoteTextField().setText("");
            }
        }
    }
}