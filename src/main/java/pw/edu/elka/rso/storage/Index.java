package pw.edu.elka.rso.storage;

import java.nio.ByteBuffer;
import java.util.*;

import static java.lang.System.*;

//Nie przyjmuje nulli jako wartosci v
class Index implements Iterable<ByteBuffer>{
    NavigableMap<Object, Collection<ByteBuffer>> index;
    TableColumn column;
    static Comparator<Object> innerCollectionComparator = new Comparator<Object>() {
        @Override
        public int compare(Object o1, Object o2) {
            Integer address1 = identityHashCode(o1);
            return address1.compareTo(identityHashCode(o2));
        }
    };

    public Index(TableColumn a_transformer, Iterable<ByteBuffer> iterable){
        index = new TreeMap<Object, Collection<ByteBuffer>>();
        column = a_transformer;
        if(iterable != null)
            for (ByteBuffer value : iterable)
                put(value);
    }

    public Index(TableColumn a_column, NavigableMap<Object, Collection<ByteBuffer>> a_index){
        index = a_index;
        column = a_column;
    }

    void put(ByteBuffer value){
        Object key = column.getValue(value);
        Collection<ByteBuffer> s = index.get(key);
        if (s == null){
            s = new TreeSet<ByteBuffer>(innerCollectionComparator);
            //s = new LinkedList<ByteBuffer>();
            index.put(key, s);
        }
        s.add(value);
    }

    void remove(ByteBuffer value){
        Object key = column.getValue(value);
        Collection<ByteBuffer> s = index.get(key);
        if (s != null)
            s.remove(value);
    }

    Iterator<ByteBuffer> get(Object key){
        Collection<ByteBuffer> s = index.get(key);
        return (s != null) ? s.iterator() : null;
    }

    Index subIndexFrom(Object from_key, boolean fromInclusive){
        return new Index(column, index.tailMap(from_key, fromInclusive));
    }

    Index subIndexTo(Object to_key, boolean toInclusive){
        return new Index(column, index.headMap(to_key, toInclusive));
    }

    Index subIndex(Object from_key, boolean fromInclusive, Object to_key, boolean toInclusive){
        return new Index(column, index.subMap(from_key, fromInclusive, to_key, toInclusive));
    }

    double estimate(){
        return index.size();
    }

    @Override
    public Iterator<ByteBuffer> iterator() {
        return new TwofoldIterator<ByteBuffer>(index.values());
    }
}