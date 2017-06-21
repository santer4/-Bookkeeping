import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

//класс для создания диалогового окна для добавления дохода

public class DialogEarning extends DialogParent{
    private JLabel accountLabel;
    private DefaultComboBoxModel<String> accountsComboBoxModel; //модель для комбинированного списка с названием счета
    private JComboBox<String> namesMyAccount; //название счета для списания
    private Vector<String> vectorCategory; //вектор для хранения списка категорий
    private DefaultComboBoxModel<String> categoryModel;// модель для ComboBox с категориями
    private JComboBox<String> categoryComboBox; //ComboBox с категориями
    private TreeMap<String, Vector<String>> mapCategory; //map для хранения данных Категрия - Список подкатегорий
    private DefaultComboBoxModel<String> subCategoryModel; //модель для комбинированного списка подкатегории расхода
    private JComboBox<String> subcategoryComboBox;//комбинированный список подкатегории расхода
    private JLabel sumLabel; //метка для поля с суммой
    private MoneyTextField sumTextField;
    private JLabel noteLabel; //метка для поля с примечанием
    private NoteTextField noteTextField;
    private ArrayList<MyEarning> tempEarningsList;
    private boolean ok; // если переменная ok == true, значит пользователь ввел название счета, можно конструировать объект типа MyEarning


    public DialogEarning(File fileCategory){

        JPanel mainPanel = getMainPanel();
        mainPanel.setLayout(new GridLayout(6,2));

        accountLabel = new JLabel("Добавить на счет");
        mainPanel.add(accountLabel);
        accountsComboBoxModel = new DefaultComboBoxModel<>();
        namesMyAccount = new JComboBox<>(accountsComboBoxModel);
        mainPanel.add(namesMyAccount);

        fillVectorCategory(fileCategory);
        mainPanel.add(new JLabel("Категория"));
        categoryComboBox = new JComboBox<>(categoryModel);
        categoryComboBox.addActionListener(new CategoryListener());
        mainPanel.add(categoryComboBox);

        Vector<String> startSubCategory = new Vector<>(mapCategory.get(vectorCategory.get(0)));
        subCategoryModel = new DefaultComboBoxModel<>(startSubCategory);
        subcategoryComboBox = new JComboBox<>(subCategoryModel);
        mainPanel.add(new JLabel("Подкатегория"));
        mainPanel.add(subcategoryComboBox);

        sumLabel = new JLabel("Cумма");
        mainPanel.add(sumLabel);
        sumTextField = new MoneyTextField(2);
        sumTextField.addFocusListener(new SumFocusAdapter());
        mainPanel.add(sumTextField);

        noteLabel = new JLabel("Примечание");
        mainPanel.add(noteLabel);
        noteTextField = new NoteTextField();
        mainPanel.add(noteTextField);

        Font font = new Font("SansSerif", Font.PLAIN, 14);
        Component[] componentsPanelDate = mainPanel.getComponents();
        for(Component x : componentsPanelDate) {
            x.setFont(font);
        }

        tempEarningsList = new ArrayList<>();
        getAnotherButton().addActionListener(new AnotherActionListener());
        getOkButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String strSum = sumTextField.getText();
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
                    String s = "Значение суммы дохода не может быть пустым";
                    JOptionPane.showMessageDialog(DialogEarning.this, s, "Предупреждение", JOptionPane.INFORMATION_MESSAGE);
                } else if(Double.compare(Double.parseDouble(strSum), 0) == 0) {
                    String s = "Значение суммы дохода не может быть нулевой";
                    JOptionPane.showMessageDialog(DialogEarning.this, s, "Предупреждение", JOptionPane.INFORMATION_MESSAGE);
                } else if(!isEdit()){
                    tempEarningsList.add(getMyEarning());
                    ok = true;
                    getDialog().setVisible(false);
                } else {
                    ok = true;
                    getDialog().setVisible(false);
                }

            }
        });
    }

    public void fillVectorCategory(File fileCategory){
        vectorCategory = new Vector<>();

        mapCategory = new TreeMap<>();
        try{

            BufferedReader readerCategory = new BufferedReader(new InputStreamReader(new FileInputStream(fileCategory), "UTF-8"));
            String line;
            while ((line = readerCategory.readLine()) != null){

                String category = line.substring(0, line.indexOf(":"));
                vectorCategory.add(category);
                String subcategoryStr = line.substring(line.indexOf(":")+1, line.length());
                String[] arraySubcategory = subcategoryStr.split("/");
                Vector<String> vectorSubcategory = new Vector<>();
                vectorSubcategory.addAll(Arrays.asList(arraySubcategory));
                mapCategory.put(category, vectorSubcategory);
            }
            readerCategory.close();
        } catch (IOException exception){
            exception.printStackTrace();
        }
        categoryModel = new DefaultComboBoxModel<>(vectorCategory);
    }

    public MyEarning getMyEarning(){
        int day = (Integer) getDayComboBox().getSelectedItem();
        int month = getMonthComboBox().getSelectedIndex();
        int year = (Integer) getYearComboBox().getSelectedItem();
        String category = (String) categoryComboBox.getSelectedItem();
        String subcategory = (String) subcategoryComboBox.getSelectedItem();
        String nameMyAccount = (String) namesMyAccount.getSelectedItem();
        String sumStr = sumTextField.getText();
        sumStr = sumStr.replace(",", ".");
        sumStr = sumStr.replace(new String(new char[]{160}), "");
        boolean startZero = true;
        if (sumStr.matches("0+\\.0+")){
            startZero = false;
        }
        while (startZero){
            if (!sumStr.startsWith("0")) startZero = false;
            else sumStr = sumStr.replaceFirst("0", "");
        }
        double sum = Double.parseDouble(sumStr);
        String note = noteTextField.getText();

        return new MyEarning(new GregorianCalendar(year,month,day), nameMyAccount, category, subcategory, sum, note);
    }

    //создание панели DialogEarning в виде диалогового окна
    public boolean showDialogEarning(Component parent, String title, Set<String> namesAccountsSet,
                                     ArrayList<MyEarning> tempEarningsList, boolean edit){
        setEdit(edit);
        ok = false;
        getAnotherButton().setVisible(true);
        this.tempEarningsList = tempEarningsList;
        //создать диалоговое окно в унаследованном методе от DialogParent
        showDialog(parent, title);
        fillNamesMyAccount(namesAccountsSet);

        //обнулить поля в диалоговом окне
        sumTextField.setText("0.00");
        noteTextField.setText("");
        //показать диалоговое окно
        getDialog().setVisible(true);
        return ok;
    }

    public boolean showEditDialogEarning(Component parent, String title, Set<String> namesAccountsSet,
                                         MyEarning editMyEarning, boolean edit){
        setEdit(edit);
        ok = false;
        getAnotherButton().setVisible(false);

        //создать диалоговое окно в унаследованном методе
        showDialog(parent, title);
        fillNamesMyAccount(namesAccountsSet);

        //назначить значения полям диалогового окна
        GregorianCalendar calendar = editMyEarning.getGregorianCalendar();
        getYearComboBox().setSelectedItem(calendar.get(Calendar.YEAR));
        getMonthComboBox().setSelectedIndex(calendar.get(Calendar.MONTH));
        getDayComboBox().setSelectedItem(calendar.get(Calendar.DAY_OF_MONTH));
        String category = editMyEarning.getCategory();
        categoryComboBox.setSelectedItem(category);
        fillSubcategoryModel(category);
        subcategoryComboBox.setSelectedItem(editMyEarning.getSubcategory());
        namesMyAccount.setSelectedItem(editMyEarning.getNameMyAccount());
        double sum = editMyEarning.getSum();
        sumTextField.setText(Double.toString(sum));
        noteTextField.setText(editMyEarning.getNote());

        // показать диалоговое окно
        getDialog().setVisible(true);
        return ok;
    }



    public void fillNamesMyAccount(Set<String> namesAccountsSet){
        accountsComboBoxModel.removeAllElements();
        for(String x : namesAccountsSet){
            accountsComboBoxModel.addElement(x);
        }
    }


    public void fillSubcategoryModel(String category){
            subCategoryModel.removeAllElements();
            Vector<String> subcategory = mapCategory.get(category);
            for(String x : subcategory) {
                subCategoryModel.addElement(x);
            }
    }



    public class SumFocusAdapter extends FocusAdapter{
        @Override
        public void focusGained(FocusEvent e) {
            if (sumTextField.getText().equals("0,00")){
                sumTextField.setText("");
            }

        }
    }

    public class CategoryListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String category = (String) categoryComboBox.getSelectedItem();
            fillSubcategoryModel(category);

        }
    }

    public class AnotherActionListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            String strSum = sumTextField.getText();
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
                String s = "Сумма дохода не может быть пустой";
                JOptionPane.showMessageDialog(DialogEarning.this, s, "Предупреждение", JOptionPane.INFORMATION_MESSAGE);
            } else if(Double.compare(Double.parseDouble(strSum), 0) == 0) {
                String s = "Сумма дохода не может быть нулевой";
                JOptionPane.showMessageDialog(DialogEarning.this, s, "Предупреждение", JOptionPane.INFORMATION_MESSAGE);
            } else {
                tempEarningsList.add(getMyEarning());
                sumTextField.setText("0.00");
                noteTextField.setText("");
            }
        }
    }

    public JLabel getSumLabel() {
        return sumLabel;
    }

    public MoneyTextField getSumTextField() {
        return sumTextField;
    }

    public JLabel getNoteLabel() {
        return noteLabel;
    }

    public NoteTextField getNoteTextField() {
        return noteTextField;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public JComboBox<String> getCategoryComboBox() {
        return categoryComboBox;
    }

    public JComboBox<String> getSubcategoryComboBox() {
        return subcategoryComboBox;
    }

    public JComboBox<String> getNamesMyAccount() {
        return namesMyAccount;
    }
}
