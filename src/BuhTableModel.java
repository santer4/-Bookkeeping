import javax.swing.table.DefaultTableModel;
import java.util.Locale;
import java.util.Vector;

/**
 * Created by TrifonovAA on 16.03.2017.
 */
public class BuhTableModel extends DefaultTableModel {

    public BuhTableModel(Object[][] data, Object[] columnNames){
        super(data, columnNames);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {

        Vector dataVector = getDataVector();
        Object[] dataRowArray =  dataVector.toArray();
        Vector dataFirstRow = (Vector) dataRowArray[0];
        Object[] dataFirstRowArray = dataFirstRow.toArray();
        return dataFirstRowArray[columnIndex].getClass();

    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }




}
