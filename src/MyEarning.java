import java.util.GregorianCalendar;

public class MyEarning {
    /**
     * Опасная штука. В случае если объекты будут содаваться в многопоточной среде
     * (swing вроде как многопоточный) нет никаких гарантий что такой подход проканает
     * Лучше разруливать значение number откуда-нибудь снаружи
     */
    private static int count;

    // Общее замечание. Если не предполагается что поле класса будет изменяемым.
    // Его нужно сразу делать final
    private GregorianCalendar gregorianCalendar;
    private String nameMyAccount;
    private String category;
    private String subcategory;
    private double sum;
    private String note;
    private int number;

    /**
     * Конструкторы с большим количеством параметров не очень удобно воспринимать
     * См. builder pattern
     */
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
