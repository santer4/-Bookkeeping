import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.Locale;
import java.math.BigDecimal;
import java.math.RoundingMode;



public class DoubleTableCellRenderer extends DefaultTableCellRenderer{
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Double dValue = (Double) value;

        String doubleStr = null;

        String stringValue = dValue.toString();
        if (stringValue.matches("-?\\d+\\.\\d{5,}")){
            dValue = new BigDecimal(dValue).setScale(2, RoundingMode.UP).doubleValue();
            doubleStr = String.format(Locale.FRANCE, "%,.2f", dValue);
        }

        if (stringValue.matches("\\d+\\.0001")){
            doubleStr = String.format(Locale.FRANCE, "%,.0f", dValue);

        }
        if (stringValue.matches("-?\\d+\\.\\d{1,2}")){
            doubleStr = String.format(Locale.FRANCE, "%,.2f", dValue);
        }
        if (stringValue.matches("-?\\d+\\.\\d{3}")) {
            doubleStr = String.format(Locale.FRANCE, "%,.3f", dValue);
        }

        return super.getTableCellRendererComponent(table, doubleStr, isSelected, hasFocus, row, column);
    }


}