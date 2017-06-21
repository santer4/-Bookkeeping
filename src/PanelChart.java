import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;

public class PanelChart extends JPanel {
    private TreeSet<MyExpense> expensesSet;
    private TreeSet<MyEarning> earningsSet;
    private TreeMap<String, MyAccount> accountsMap;
    private ArrayList<MyExpense> tempExpenseList;
    private ArrayList<MyEarning> tempEarningList;
    private DefaultComboBoxModel<String> accountsComboBoxModel;
    private JComboBox<String> namesMyAccount;
    private ArrayList<String> columnBarNames;
    private ArrayList<Double> sumList;
    private PanelDate panelDateWith;
    private PanelDate panelDateTo;
    private Vector<String> categoryExpense;
    private Vector<String> categoryEarning;
    private DefaultComboBoxModel<String> categoryModel;
    private JComboBox<String> categoryComboBox;
    private TreeMap<String, Vector<String>> mapCategoryEarning;
    private TreeMap<String, Vector<String>> mapCategoryExpense;
    private JButton makeGraph;
    private boolean selectExpense = true;
    private boolean selectCategory = true;
    private boolean notData = false;

    public PanelChart(TreeSet<MyExpense> expensesSet,  TreeSet<MyEarning> earningsSet, TreeMap<String, MyAccount> accountsMap){
        super();
        this.expensesSet = expensesSet;
        this.earningsSet = earningsSet;
        this.accountsMap = accountsMap;
        tempExpenseList = new ArrayList<>();
        tempEarningList = new ArrayList<>();
        columnBarNames = new ArrayList<>();
        sumList = new ArrayList<>();
        mapCategoryExpense = new TreeMap<>();
        mapCategoryEarning = new TreeMap<>();
        initPanel();
    }

    public void initPanel(){
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        Font font = new Font("SansSerif", Font.PLAIN, 14);

        panelDateWith = new PanelDate();
        ChangeActionListener changesInitData = new ChangeActionListener();
        panelDateWith.getDayComboBox().addActionListener(changesInitData);
        panelDateWith.getMonthComboBox().addActionListener(changesInitData);
        panelDateWith.getYearComboBox().addActionListener(changesInitData);
        panelDateTo = new PanelDate();
        panelDateTo.getDayComboBox().addActionListener(changesInitData);
        panelDateTo.getMonthComboBox().addActionListener(changesInitData);
        panelDateTo.getYearComboBox().addActionListener(changesInitData);

        JPanel panelWith = new JPanel();
        panelWith.add(new JLabel("Начало периода"));
        panelWith.getComponent(0).setFont(font);
        panelWith.add(panelDateWith);
        JPanel panelTo = new JPanel();
        panelTo.add(new JLabel("Окончание периода"));
        panelTo.getComponent(0).setFont(font);
        panelTo.add(panelDateTo);

        Border grayLine = BorderFactory.createLineBorder(Color.DARK_GRAY);
        TitledBorder titled = BorderFactory.createTitledBorder(grayLine, "Период");
        titled.setTitleJustification(TitledBorder.CENTER);
        titled.setTitlePosition(TitledBorder.DEFAULT_POSITION);
        JPanel periodPanel = new JPanel();

        JPanel cont = new JPanel(new GridLayout(2, 1));
        cont.add(panelWith);
        cont.add(panelTo);
        cont.setBorder(titled);

        periodPanel.add(cont);

        add(periodPanel);

        JPanel accountPanel = new JPanel();
        accountsComboBoxModel = new DefaultComboBoxModel<>();
        for(String x : accountsMap.keySet()) accountsComboBoxModel.addElement(x);
        namesMyAccount = new JComboBox<>(accountsComboBoxModel);
        namesMyAccount.addActionListener(changesInitData);
        JLabel nameAccount = new JLabel("Cчет");
        accountPanel.add(nameAccount);
        accountPanel.add(namesMyAccount);

        JPanel expensePanelRd = new JPanel(new GridLayout(1, 2));
        Border grayLineTwo = BorderFactory.createLineBorder(Color.DARK_GRAY);
        TitledBorder titledExpense = BorderFactory.createTitledBorder(grayLineTwo);
        titledExpense.setTitleJustification(TitledBorder.CENTER);
        titledExpense.setTitlePosition(TitledBorder.DEFAULT_POSITION);
        JRadioButton expenseRadBtn = new JRadioButton("расходы", true);
        expenseRadBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectExpense = true;
                updateCategory(categoryExpense);
                makeGraph.setEnabled(false);
            }
        });
        JRadioButton earningRadBtn = new JRadioButton("доходы", false);
        earningRadBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectExpense = false;
                updateCategory(categoryEarning);
                makeGraph.setEnabled(false);
            }
        });
        ButtonGroup buttonGroupOne = new ButtonGroup();
        buttonGroupOne.add(expenseRadBtn);
        buttonGroupOne.add(earningRadBtn);
        expensePanelRd.add(expenseRadBtn);
        expensePanelRd.add(earningRadBtn);
        expensePanelRd.setBorder(titledExpense);


        JRadioButton categoryRadBtn = new JRadioButton("категории", true);
        categoryRadBtn.setToolTipText("формирует график для категорий");
        JRadioButton subCatRadBtn = new JRadioButton("подкатегории", false);
        subCatRadBtn.setToolTipText("формирует график для подкатегорий выбранной категории");
        ButtonGroup buttonGroupTwo = new ButtonGroup();
        buttonGroupTwo.add(categoryRadBtn);
        buttonGroupTwo.add(subCatRadBtn);
        JPanel categPanelRd = new JPanel(new GridLayout(1, 2));
        Border grayLineThree = BorderFactory.createLineBorder(Color.DARK_GRAY);
        TitledBorder titledCateg = BorderFactory.createTitledBorder(grayLineThree);
        titledCateg.setTitleJustification(TitledBorder.CENTER);
        titledCateg.setTitlePosition(TitledBorder.DEFAULT_POSITION);
        categPanelRd.add(categoryRadBtn);
        categPanelRd.add(subCatRadBtn);
        categPanelRd.setBorder(titledCateg);

        categoryExpense = new Vector<>();
        categoryEarning = new Vector<>();
        fillMapCategory(mapCategoryExpense, mapCategoryEarning);
        categoryModel = new DefaultComboBoxModel<>();
        updateCategory(categoryExpense);
        categoryComboBox = new JComboBox<>(categoryModel);
        categoryComboBox.addActionListener(changesInitData);
        JPanel catComboBoxPanel = new JPanel();
        catComboBoxPanel.add(categoryComboBox);
        categoryComboBox.setEnabled(false);

        categoryRadBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectCategory = true;
                categoryComboBox.setEnabled(false);
                makeGraph.setEnabled(false);
            }
        });
        subCatRadBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectCategory = false;
                categoryComboBox.setEnabled(true);
                makeGraph.setEnabled(false);
            }
        });

        JButton makeData = new JButton("cформировать данные для графика");
        makeData.addActionListener(new DataActionListener());
        JPanel panelMakeData = new JPanel();
        panelMakeData.add(makeData);
        makeGraph = new JButton("показать график");
        makeGraph.setEnabled(false);
        makeGraph.addActionListener(new GraphActionListener());
        JPanel panelMakeGraph = new JPanel();
        panelMakeGraph.add(makeGraph);

        JPanel panelControl = new JPanel(new GridLayout(6, 1, 0, 5));
        panelControl.add(accountPanel);
        panelControl.add(expensePanelRd);
        panelControl.add(categPanelRd);
        panelControl.add(catComboBoxPanel);
        panelControl.add(makeData);
        panelControl.add(makeGraph);
        JPanel panel = new JPanel();
        panel.add(panelControl);

        Component[] componentsPanelDate = panelControl.getComponents();
        for(Component x : componentsPanelDate) {
            if (x instanceof JPanel) {
                Component[] components = ((JPanel) x).getComponents();
                for(Component component : components) {
                    component.setFont(font);
                }
            } else x.setFont(font);
        }

        add(panel);

    }

    public void fillMapCategory(TreeMap<String, Vector<String>> mapCategoryExpense, TreeMap<String, Vector<String>> mapCategoryEarning){
        File fileCategoryExpense = new File(System.getProperty("user.dir") + "\\Data\\CategoryExpenses.txt");

        try{

            BufferedReader readerCategory = new BufferedReader(new InputStreamReader(new FileInputStream(fileCategoryExpense), "UTF-8"));
            String line;
            while ((line = readerCategory.readLine()) != null){

                String category = line.substring(0, line.indexOf(":"));
                categoryExpense.add(category);
                String subcategoryStr = line.substring(line.indexOf(":")+1, line.length());
                String[] arraySubcategory = subcategoryStr.split("/");
                Vector<String> vectorSubcategory = new Vector<>();
                vectorSubcategory.addAll(Arrays.asList(arraySubcategory));
                mapCategoryExpense.put(category, vectorSubcategory);
            }
            readerCategory.close();
        } catch (IOException exception){
            exception.printStackTrace();
        }

        File fileCategoryEarning = new File(System.getProperty("user.dir") + "\\Data\\CategoryEarnings.txt");
        try{

            BufferedReader readerCategoryEarning = new BufferedReader(new InputStreamReader(new FileInputStream(fileCategoryEarning), "UTF-8"));
            String line;
            while ((line = readerCategoryEarning.readLine()) != null){

                String category = line.substring(0, line.indexOf(":"));
                categoryEarning.add(category);
                String subcategoryStr = line.substring(line.indexOf(":")+1, line.length());
                String[] arraySubcategory = subcategoryStr.split("/");
                Vector<String> vectorSubcategory = new Vector<>();
                vectorSubcategory.addAll(Arrays.asList(arraySubcategory));
                mapCategoryEarning.put(category, vectorSubcategory);
            }
            readerCategoryEarning.close();
        } catch (IOException exception){
            exception.printStackTrace();
        }

    }

    public void updateAccounts(){
        accountsComboBoxModel.removeAllElements();
        Set<String> set = accountsMap.keySet();
        for(String x : set){
            accountsComboBoxModel.addElement(x);
        }
    }

    public void updateCategory(Vector<String> category){
        categoryModel.removeAllElements();
        for (String x : category){
            categoryModel.addElement(x);
        }

    }

    public class DataActionListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            String startDate = panelDateWith.inverseSimpleDate();
            String endDate = panelDateTo.inverseSimpleDate();
            if (startDate.compareTo(endDate) > 0){
                String s = "Дата начала периода больше даты окончания периода";
                JOptionPane.showMessageDialog(PanelChart.this, s, "Предупреждение", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            notData = false;
            columnBarNames.removeAll(new ArrayList<>(columnBarNames));
            sumList.removeAll(new ArrayList<>(sumList));
            tempExpenseList.removeAll(new ArrayList<>(tempExpenseList));
            tempEarningList.removeAll(new ArrayList<>(tempEarningList));
            String nameAccount = (String) namesMyAccount.getSelectedItem();
            String nextDayAfterEndDay = panelDateTo.nextDayInverseSimpleDate();

            if (selectExpense){
                makeDataAboutExpenses(startDate, endDate, nameAccount, nextDayAfterEndDay);
            } else {
                makeDataAboutEarnings(startDate, endDate, nameAccount, nextDayAfterEndDay);
            }
            if (notData) {
                return;
            }

            if (selectExpense && tempExpenseList.size() == 0) {
                String s = "Нет данных в указанный период";
                JOptionPane.showMessageDialog(PanelChart.this, s, "Предупреждение", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            if (!selectExpense && tempEarningList.size() == 0) {
                String s = "Нет данных в указанный период";
                JOptionPane.showMessageDialog(PanelChart.this, s, "Предупреждение", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            makeGraph.setEnabled(true);
        }
    }

    public void makeDataAboutExpenses(String startDate, String endDate, String nameAccount, String nextDayAfterEndDate){
        for(MyExpense x : expensesSet) tempExpenseList.add(x);
        Iterator<MyExpense> expenseIterator = tempExpenseList.iterator();
        while (expenseIterator.hasNext()){
            if (!expenseIterator.next().getNameMyAccount().equals(nameAccount)) expenseIterator.remove();
        }
        if (tempExpenseList.size() == 0) {
            notData = true;
            String s = "Нет расходов в указанный период по счету " + nameAccount;
            JOptionPane.showMessageDialog(PanelChart.this, s, "Предупреждение", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        MyExpense lastExpense = tempExpenseList.get(tempExpenseList.size() - 1);
        MyExpense firstExpense = tempExpenseList.get(0);


        String notExpense = "Нет расходов в указанный период";
        if (selectExpense && lastExpense.toString().compareTo(startDate) < 0){
            JOptionPane.showMessageDialog(PanelChart.this, notExpense, "Предупреждение", JOptionPane.INFORMATION_MESSAGE);
            notData = true;
            return;
        }
        if (selectExpense && firstExpense.toString().compareTo(endDate) > 0 &&
                !firstExpense.toString().startsWith(endDate)){
            JOptionPane.showMessageDialog(PanelChart.this, notExpense, "Предупреждение", JOptionPane.INFORMATION_MESSAGE);
            notData = true;
            return;
        }


        for(int i = 0; i < tempExpenseList.size();  ){
            if(tempExpenseList.get(i).toString().compareTo(startDate) < 0){
                tempExpenseList.remove(i);
            }
            else i++;
        }

        for (int i = 0; i < tempExpenseList.size();){
            if (nextDayAfterEndDate.compareTo(tempExpenseList.get(i).toString()) < 0){
                tempExpenseList.remove(i);
            } else i++;
        }



        if(selectCategory){
            for (String x : categoryExpense) {
                columnBarNames.add(x);
                sumList.add(0.0);
            }
            int size = columnBarNames.size();
            for (MyExpense x : tempExpenseList){
                for (int i = 0; i < size; i++){
                    if (x.getCategory().equals(columnBarNames.get(i))) {
                        sumList.set(i, sumList.get(i) + x.getSum());
                    }
                }
            }
        } else {
            String category = (String) categoryComboBox.getSelectedItem();
            Vector<String> subcategories = mapCategoryExpense.get(category);
            for (String x : subcategories){
                columnBarNames.add(x);
                sumList.add(0.0);
            }
            Iterator<MyExpense> expenseCtgIterator = tempExpenseList.iterator();
            while (expenseCtgIterator.hasNext()){
                if (!expenseCtgIterator.next().getCategory().equals(category)) expenseCtgIterator.remove();
            }

            int size = columnBarNames.size();
            for (MyExpense x : tempExpenseList){
                for (int i = 0; i < size; i++){
                    if (x.getSubcategory().equals(columnBarNames.get(i))) {
                        sumList.set(i, sumList.get(i) + x.getSum());
                    }
                }
            }
        }



    }

    public void makeDataAboutEarnings(String startDate, String endDate, String nameAccount, String nextDayAfterEndDate){
        for(MyEarning x : earningsSet) tempEarningList.add(x);
        Iterator<MyEarning> earningIterator = tempEarningList.iterator();
        while (earningIterator.hasNext()){
            if (!earningIterator.next().getNameMyAccount().equals(nameAccount)) earningIterator.remove();
        }

        if (tempEarningList.size() == 0) {
            notData = true;
            String s = "Нет доходов в указанный период по счету " + nameAccount;
            JOptionPane.showMessageDialog(PanelChart.this, s, "Предупреждение", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        MyEarning lastEarning = tempEarningList.get(tempEarningList.size() - 1);
        MyEarning firstEarning = tempEarningList.get(0);
        String notEarning = "Нет доходов в указанный период";
        if (lastEarning.toString().compareTo(startDate) < 0 ){
            JOptionPane.showMessageDialog(PanelChart.this, notEarning, "Предупреждение", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (firstEarning.toString().compareTo(endDate) > 0 &&
                !firstEarning.toString().startsWith(endDate)){
            JOptionPane.showMessageDialog(PanelChart.this, notEarning, "Предупреждение", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for(int i = 0; i < tempEarningList.size();  ){
            if(tempEarningList.get(i).toString().compareTo(startDate) < 0){
                tempEarningList.remove(i);
            }
            else i++;
        }

        for (int i = 0; i < tempEarningList.size();){
            if (nextDayAfterEndDate.compareTo(tempEarningList.get(i).toString()) < 0){
                tempEarningList.remove(i);
            } else i++;
        }
        if (selectCategory) {
            for (String x : categoryEarning){
                columnBarNames.add(x);
                sumList.add(0.0);
            }

            int size = columnBarNames.size();
            for (MyEarning x : tempEarningList){
                for (int i = 0; i < size; i++){
                    if (x.getCategory().equals(columnBarNames.get(i))) {
                        sumList.set(i, sumList.get(i) + x.getSum());
                    }
                }
            }
        } else {
            String category = (String) categoryComboBox.getSelectedItem();
            Vector<String> subcategories = mapCategoryEarning.get(category);
            for (String x : subcategories){
                columnBarNames.add(x);
                sumList.add(0.0);
            }
            Iterator<MyEarning> earningCtgIterator = tempEarningList.iterator();
            while (earningCtgIterator.hasNext()){
                if (!earningCtgIterator.next().getCategory().equals(category)) earningCtgIterator.remove();
            }

            int size = columnBarNames.size();
            for (MyEarning x : tempEarningList){
                for (int i = 0; i < size; i++){
                    if (x.getSubcategory().equals(columnBarNames.get(i))) {
                        sumList.set(i, sumList.get(i) + x.getSum());
                    }
                }
            }
        }

    }

    public class GraphActionListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            String expenseEarning;
            if (selectExpense) {
                expenseEarning = "расходов";
            } else expenseEarning = "доходов";
            String titleDialog = "Диаграмма " + expenseEarning + " за период с " + panelDateWith.simpleDate() +
                    " по " + panelDateTo.simpleDate();
            if (!selectCategory){
                titleDialog += " для категории " + categoryComboBox.getSelectedItem();
            }
            titleDialog += " для счета " + namesMyAccount.getSelectedItem();
            DialogBarChart dialogBarChart = new DialogBarChart();
            dialogBarChart.showDialog(PanelChart.this, titleDialog, columnBarNames, sumList);
        }
    }


    public class ChangeActionListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            makeGraph.setEnabled(false);
        }
    }

}
