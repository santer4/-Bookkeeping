import java.util.GregorianCalendar;


public class MyEarning {
    private GregorianCalendar gregorianCalendar;
    private String nameMyAccount;
    private String category;
    private String subcategory;
    private double sum;
    private String note;
    private static int count;
    private int number;

    public MyEarning(GregorianCalendar gregorianCalendar, String nameMyAccount,
                     String category, String subcategory, double sum, String note) {
        this.gregorianCalendar = gregorianCalendar;
        this.nameMyAccount = nameMyAccount;
        this.category = category;
        this.subcategory = subcategory;
        this.sum = sum;
        this.note = note;
        number = count;
        count++;
    }

    @Override
    public String toString() {
        String strDate = String.format("%1$tY.%1$tm.%1$td", getGregorianCalendar());
        return strDate +"/" + nameMyAccount + "/" + category + "/" + subcategory
                + "/" + sum + "/" + note + "/" + number;
    }

    public GregorianCalendar getGregorianCalendar() {
        return gregorianCalendar;
    }

    public String getNameMyAccount() {
        return nameMyAccount;
    }

    public String getCategory() {
        return category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public double getSum() {
        return sum;
    }

    public String getNote() {
        return note;
    }

    public void setNameMyAccount(String nameMyAccount) {
        this.nameMyAccount = nameMyAccount;
    }

    public int getNumber() {
        return number;
    }
}
