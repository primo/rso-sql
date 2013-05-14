import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

//Insert
class Record {
    ByteBuffer rawData;
    Map<String, TableColumn> specification;

    public Record(Map<String, TableColumn> a_specification, int buffer_capacity){
        specification = a_specification;
        rawData = ByteBuffer.allocate(buffer_capacity);
        rawData.putChar('v');//Oznaczenie wpisu jako V-Valid
    }

    void setValue(String column_name, Object object) throws IllegalArgumentException{
        TableColumn tabcol = (TableColumn) specification.get(column_name);
        rawData.position(tabcol.position);
        tabcol.putValue(rawData, object);
    }

    /*Object getValue(String column_name){
        TableColumn tabcol = (TableColumn) specification.get(column_name);
        rawData.position(tabcol.position);
        return tabcol.getValue(rawData);
    }*/
}

//Filter
class Filter {

}

public class Table {
    Map<String, TableColumn> specification;
    int bufferCapacity;
    List<ByteBuffer> rawData;

    public Table(Map<String, ColumnType> a_table_specification) {
        specification = new HashMap<String, TableColumn>();
        //byte[] receiveData = new byte[test];

        //Ustaw pozycje
        int curr_pos = 1;
        ColumnType column_spec;
        for (Map.Entry<String, ColumnType> entry : a_table_specification.entrySet()){
            column_spec = entry.getValue();

            specification.put(entry.getKey(), new TableColumn(column_spec, curr_pos));
            curr_pos += column_spec.getLength();
        }

        bufferCapacity = curr_pos;
        rawData = new LinkedList<ByteBuffer>();
        //ByteBuffer row = ByteBuffer.allocate(curr_pos);
    }

    public Record newRecord(){
        return new Record(specification, bufferCapacity);
    }

    public boolean insert(Record record){
        rawData.add(record.rawData);
        return false;
    }
}
/*
FiledAccessor
RawData
*/