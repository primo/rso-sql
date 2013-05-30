package pw.edu.elka.rso.storage;

import java.nio.ByteBuffer;
import java.util.*;

public class Table implements Iterable<Record>{
    private final TableSchema tableSchema;
    List<ByteBuffer> mainList;
    Map<String, Index<ByteBuffer, ByteBuffer>> indexes;

    public Table(TableSchema table_schema){
        tableSchema = table_schema;
        mainList = new LinkedList<ByteBuffer>();
        indexes = new HashMap<String, Index<ByteBuffer, ByteBuffer>>();
    }

    Record newRecord(){
        return new Record(tableSchema);
    }

    void insert(Record record){
        mainList.add(record.byteBuffer);
        record.anew();
    }

    void createIndex(String column_name){
        Index<ByteBuffer, ByteBuffer> index = new Index<ByteBuffer, ByteBuffer>(tableSchema.getTableColumn(column_name), mainList);
        indexes.put(column_name, index);
    }

    public Iterator<Record> indexedItearator(String column_name, Object from, boolean from_inclusive, Object to, boolean to_inclusive) {
        Record record = new Record(tableSchema);
        record.setValue(column_name, from);
        ByteBuffer from_bb = tableSchema.getTableColumn(column_name).transform(record.byteBuffer);
        record.anew();
        record.setValue(column_name, to);
        ByteBuffer to_bb = tableSchema.getTableColumn(column_name).transform(record.byteBuffer);

        Index<ByteBuffer, ByteBuffer> ndx = indexes.get(column_name).subIndex(from_bb, from_inclusive, to_bb, to_inclusive);
        return new TableIterator(tableSchema, ndx, ndx.estimate());
    }


    public Iterator<Record> indexedItearator(String column_name) {
        Index<ByteBuffer, ByteBuffer> ndx = indexes.get(column_name);
        return new TableIterator(tableSchema, ndx, ndx.estimate());
    }

    @Override
    public Iterator<Record> iterator() {
        return new TableIterator(tableSchema, mainList, mainList.size());
    }
}
