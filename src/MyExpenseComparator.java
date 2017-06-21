import java.util.Comparator;


public class MyExpenseComparator implements Comparator<MyExpense> {
    @Override
    public int compare(MyExpense o1, MyExpense o2) {
        String o1String = o1.toString();
        String o2String = o2.toString();
        return o1String.compareTo(o2String);
    }
}
