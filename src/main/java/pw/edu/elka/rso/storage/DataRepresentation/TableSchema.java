package pw.edu.elka.rso.storage.DataRepresentation;

import java.util.HashMap;
import java.util.Map;


public class TableSchema{
    Map<String, TableColumn> specification = new HashMap<String, TableColumn>();
    int currentPosition = 1;

    void addColumn(String column_name, ColumnType column_type, int length){
        TableColumn table_column;

        switch (column_type) {
            case INT:
                table_column = new IntTableColumn(currentPosition);
                break;
            case DOUBLE:
                table_column = new DoubleTableColumn(currentPosition);
                break;
            case CHAR:
                table_column = new CharTableColumn(length, currentPosition);
                break;
            default:
                return;
        }
        currentPosition += table_column.length;
        specification.put(column_name, table_column);
    }

    TableColumn getTableColumn(String name){
        return specification.get(name);
    }

    int getLength(){
        return currentPosition;
    }
}
