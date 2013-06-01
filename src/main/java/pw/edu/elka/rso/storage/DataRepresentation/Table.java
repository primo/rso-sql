package pw.edu.elka.rso.storage.DataRepresentation;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;

class TableIterator implements Iterator<Record>{
    private Iterator<ByteBuffer> listIterator;
    Record record;

    public TableIterator(TableSchema table_schema, Iterator<ByteBuffer> list_iterator){
        listIterator = list_iterator;
        record = new Record(table_schema);
    }

    @Override
    public boolean hasNext() {
        return listIterator.hasNext();
    }

    @Override
    public Record next() {
        record.setByteBuffer(listIterator.next());
        return record;
    }

    @Override
    public void remove() {
        listIterator.remove();
    }
}

public class Table {
    private final TableSchema tableSchema;
    List<ByteBuffer> mainList;

    public Table(TableSchema table_schema){
      tableSchema = table_schema;
        mainList = new LinkedList<ByteBuffer>();
    }

    public Record newRecord(){
        Record record = new Record(tableSchema);
        record.setByteBuffer(ByteBuffer.allocate(tableSchema.getLength()));
        return record;
    }

    public void insert(Record record){
        mainList.add(record.byteBuffer);
        record.setByteBuffer(ByteBuffer.allocate(tableSchema.getLength()));
    }

    public Iterator<Record> tableIterator(){
        return new TableIterator(tableSchema, mainList.listIterator());
    }
}
