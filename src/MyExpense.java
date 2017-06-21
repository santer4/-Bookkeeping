import java.util.GregorianCalendar;


public class MyExpense extends MyEarning{
    private double quantity;
    private String unitMeasure;

    public MyExpense(String category, String subcategory, String nameMyAccount,
                     GregorianCalendar gregorianCalendar,
                     double quantity, String unitMeasure, double sum, String note){

        super(gregorianCalendar, nameMyAccount, category, subcategory, sum, note);

        this.quantity = quantity;
        this.unitMeasure = unitMeasure;
    }

    @Override
    public String toString() {

        String strDate = String.format("%1$tY.%1$tm.%1$td", getGregorianCalendar());
        String strQuantity = Double.toString(quantity);
        if (strQuantity.matches("\\d+\\.0001")){
            strQuantity = Integer.toString(new Double(quantity).intValue());
        }
        return strDate + "/" + getNameMyAccount() + "/" + getCategory() + "/" + getSubcategory() +
                "/" + strQuantity + "/"
                + unitMeasure + "/" + getSum() + "/" + getNote() + "/" + getNumber();
    }



    public double getQuantity() {
        return quantity;
    }

    public String getUnitMeasure() {
        return unitMeasure;
    }
}
