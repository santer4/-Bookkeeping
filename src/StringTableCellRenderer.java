import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Created by TrifonovAA on 26.04.2017.
 */
public class StringTableCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        String stringValue = (String) value;

        if(stringValue.equals("<Без подкатегории>")){
            stringValue = "";
        }
        if (stringValue.equals("<без ед. изм.>")){
            stringValue = "";
        }
        return super.getTableCellRendererComponent(table, stringValue, isSelected, hasFocus, row, column);
    }


}
