package pw.edu.elka.rso.storage;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

public class Table implements Iterable<Record>{
    private final TableSchema tableSchema;
    List<ByteBuffer> mainList;
    Map<String, Index> indexes;

    public Table(TableSchema table_schema){
      tableSchema = table_schema;
        mainList = new LinkedList<ByteBuffer>();
    }

    Record newRecord(){
        Record record = new Record(tableSchema);
        ByteBuffer bb = ByteBuffer.allocate(tableSchema.getLength());
        bb.order(ByteOrder.BIG_ENDIAN);
        record.setByteBuffer(bb);
        return record;
    }

    void insert(Record record){
        mainList.add(record.byteBuffer);
        record.setByteBuffer(ByteBuffer.allocate(tableSchema.getLength()));
    }

    void createIndex(String column_name){
        Index index = new Index<ByteBuffer, ByteBuffer>(tableSchema.getTableColumn(column_name), mainList);
        indexes.put(column_name, index);
    }

    @Override
    public Iterator<Record> iterator() {
        return new TableIterator(tableSchema, mainList.listIterator());
    }
}
