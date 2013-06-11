package pw.edu.elka.rso.storage.DataRepresentation;


import java.io.Serializable;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.security.InvalidParameterException;
import java.util.*;

public class TableSchema implements Cloneable, Serializable {
    // It is vital that we use LinkedHashMap because we want to maintain the insertion order during the
    // iteration
    HashMap<String, TableColumn> specification = new LinkedHashMap<String, TableColumn>();
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

    public ColumnType getColumnType(String name) {
        if (null != specification.get(name))
            return  specification.get(name).columnType;
        return null;
    }

    public int getLength(){
        return rLength;
    }

    public TableSchema copy() {
        TableSchema ts = new TableSchema();
        ts.specification = (HashMap<String, TableColumn>) this.specification.clone();
        ts.rLength = this.rLength;
        return ts;
    }

    public TableSchema copy( Set<String> columns) {
        TableSchema ts = new TableSchema();
        for (String s: columns) {
            if (!this.specification.containsKey(s)) {
                throw new InvalidParameterException("Source table schema doesn't contain the key: " + s);
            }
            TableColumn columnTable = this.specification.get(s);
            ts.specification.put(s, columnTable);
            ts.rLength += columnTable.length;
        }
        return ts;
    }

    public TableSchema copy( Set<String> columns, Map<String,String> renames) {
        TableSchema ts = new TableSchema();

        for (String s: columns) {
            if (!this.specification.containsKey(s)) {
                throw new InvalidParameterException("Source table schema doesn't contain the key: " + s);
            }
            TableColumn columnTable = this.specification.get(s);
            ts.specification.put(renames.get(s), columnTable);
            ts.rLength += columnTable.length;
        }
        return ts;
    }

    public TableSchema copy(String prefix) {
        TableSchema ts = new TableSchema();

        for (String s: specification.keySet()) {
            TableColumn columnTable = this.specification.get(s);
            ts.specification.put(prefix+"."+s, columnTable);
            ts.rLength += columnTable.length;
        }
        return ts;
    }

    public TableSchema appendSchema(TableSchema tableSchema) throws CloneNotSupportedException {
        Set<String> keys = tableSchema.specification.keySet();
        for (String k: keys) {
            if (this.specification.containsKey(k)) {
                throw new InvalidParameterException("TableScheme to be appended duplicates keys from the source.");
            }
        }
        // Shift the columns from the appended scheme by the offset in the original schema
        final int baseOffset = this.rLength-1;
        for (String k : keys) {
            TableColumn temp = (TableColumn)tableSchema.getTableColumn(k).clone();
            temp.position += baseOffset;
            this.rLength += temp.length;
            this.specification.put(k, temp);
        }
        return this;
    }

    public TableSchema appendSchema(TableSchema tableSchema, String prefix) throws CloneNotSupportedException {
        Set<String> keys = tableSchema.specification.keySet();
        for (String k: keys) {
            if (this.specification.containsKey(prefix.concat(k))) {
                throw new InvalidParameterException("TableScheme to be appended duplicates keys from the source.");
            }
        }
        final int baseOffset = this.rLength-1;
        for (String k : keys) {
            TableColumn temp = (TableColumn)tableSchema.getTableColumn(k).clone();
            temp.position += baseOffset;
            this.rLength += temp.length;
            this.specification.put(prefix+"."+k, temp);
        }
        return this;
    }

    public TableSchema appendScheme(TableSchema tableSchema, List<SimpleEntry<String,String>> renames) throws CloneNotSupportedException {
        // ! does not validate duplicated columns between schemas etc...
        final int baseOffset = this.rLength-1;
        for (Entry<String,String> p : renames) {
            TableColumn temp = (TableColumn)tableSchema.getTableColumn(p.getValue()).clone();
            temp.position += baseOffset;
            this.rLength += temp.length;
            this.specification.put(p.getKey(), temp);
        }
        return this;
    }

    public Set<String> getColumnNames() {
        return this.specification.keySet();
    }
}
