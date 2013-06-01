package pw.edu.elka.rso.storage.DataRepresentation;

import java.util.HashMap;
import java.util.Map;

public class TableSchema{
    Map<String, TableColumn> specification = new HashMap<String, TableColumn>();
    int rLength = 1;

    public void addColumn(String column_name, ColumnType column_type, int length){
        TableColumn table_column;

        switch (column_type) {
            case INT:
                table_column = new IntTableColumn(rLength);
                break;
            case DOUBLE:
                table_column = new DoubleTableColumn(rLength);
                break;
            case CHAR:
                table_column = new CharTableColumn(length, rLength);
                break;
            default:
                return;
        }
        rLength += table_column.length;
        specification.put(column_name, table_column);
    }

    public TableColumn getTableColumn(String name){
        return specification.get(name);
    }

    public int getLength(){
        return currentPosition;
    }
}
