import java.util.Comparator;


public class MyEarningComparator implements Comparator<MyEarning> {
    @Override
    public int compare(MyEarning o1, MyEarning o2) {
        String o1String = o1.toString();
        String o2String = o2.toString();
        return o1String.compareTo(o2String);
    }
}
