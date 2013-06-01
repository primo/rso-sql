package pw.edu.elka.rso.storage;

import java.nio.ByteBuffer;
import java.util.*;

public class Table implements Iterable<Record>{
    final TableSchema tableSchema;
    List<ByteBuffer> mainList;
    Map<String, Index> indexes;

    public Table(TableSchema table_schema){
        tableSchema = table_schema;
        mainList = new LinkedList<ByteBuffer>();
        indexes = new HashMap<String, Index>();
    }

    Record newRecord(){
        return new Record(tableSchema);
    }

    void insert(Record record){
        for(Index idx : indexes.values())
            idx.put(record.byteBuffer);
        mainList.add(record.byteBuffer);
        record.anew();
    }

    void delete(ByteBuffer byte_buffer, Index deletion_source){
        //@TODO Implement this.
//        for(Index idx : indexes.values())
//            idx.remove();
    }

    void createIndex(String column_name){
        Index index = new Index(tableSchema.getTableColumn(column_name), mainList);
        indexes.put(column_name, index);
    }

    public Iterator<Record> indexedIterator(String column_name) {
        Index ndx = indexes.get(column_name);
        return new TableIterator(this, ndx, ndx.estimate());
    }

    public Iterator<Record> indexedIteratorFrom(String column_name, Object from, boolean from_inclusive) {
        Index ndx = indexes.get(column_name).subIndexFrom(from, from_inclusive);
        return new TableIterator(this, ndx, ndx.estimate());
    }

    public Iterator<Record> indexedIteratorTo(String column_name, Object to, boolean to_inclusive) {
        Index ndx = indexes.get(column_name).subIndexTo(to, to_inclusive);
        return new TableIterator(this, ndx, ndx.estimate());
    }

    public Iterator<Record> indexedIterator(String column_name, Object from, boolean from_inclusive, Object to, boolean to_inclusive) {
        Index ndx = indexes.get(column_name).subIndex(from, from_inclusive, to, to_inclusive);
        return new TableIterator(this, ndx, ndx.estimate());
    }

    @Override
    public Iterator<Record> iterator() {
        return new TableIterator(this, mainList, mainList.size());
    }
}
