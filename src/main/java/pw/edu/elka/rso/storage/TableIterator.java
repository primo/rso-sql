package pw.edu.elka.rso.storage;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Iterator;

class TwofoldIterator<T> implements Iterator<T> {
    private Iterator<? extends Collection<T>> highIterator;
    private Iterator<T> lowIterator;
    Collection<T> highCollection;

    public TwofoldIterator(Iterable<? extends Collection<T>> high_iterable){
        highIterator = high_iterable.iterator();
    }

    @Override
    public boolean hasNext() {
        return lowIterator != null && lowIterator.hasNext() || highIterator.hasNext();
    }

    @Override
    public T next() {
        if(lowIterator == null || !lowIterator.hasNext()){
            highCollection = highIterator.next();//moze rzucic wyjatkiem
            lowIterator = highCollection.iterator();
        }

        return lowIterator.next();
    }

    @Override
    public void remove() {
        lowIterator.remove();
        if(highCollection.isEmpty())
            highIterator.remove();
    }
}

public class TableIterator implements Iterator<Record>{
    Iterator<ByteBuffer> listIterator;
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