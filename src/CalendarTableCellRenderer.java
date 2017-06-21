import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Alexandr on 26.04.2017.
 */
public class CalendarTableCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        GregorianCalendar calendar = (GregorianCalendar) value;

        String strDate = String.format("%1$td.%1$tm.%1$tY", calendar);
        return super.getTableCellRendererComponent(table, strDate, isSelected, hasFocus, row, column);
    }


}
