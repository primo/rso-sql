package pw.edu.elka.rso.storage;

import java.util.*;

import static java.lang.System.*;

interface Transformer<A, B>{
    B transform(A a);
}

//Nie przyjmuje nulli jako wartosci v
class Index<K, V> implements Iterable<V>{
    NavigableMap<K, Set<V>> index;
    Transformer<V, K> transformer;
    double avgSetSize = 0;
    static Comparator<Object> addressComparator = new Comparator<Object>() {
        @Override
        public int compare(Object o1, Object o2) {
            Integer address1 = identityHashCode(o1);
            return address1.compareTo(identityHashCode(o2));
        }
    };

    public Index(Transformer<V, K> a_transformer, Iterable<V> iterable){
        index = new TreeMap<K, Set<V>>();
        transformer = a_transformer;
        if(iterable != null)
            for (V value : iterable)
                put(value);
    }

    public Index(Transformer<V, K> a_transformer, NavigableMap<K, Set<V>> a_index){
        index = a_index;
        transformer = a_transformer;
    }

    void put(V value){
        avgSetSize *= index.size();
        K key = transformer.transform(value);
        Set<V> s = index.get(key);
        if (s == null){
            s = new TreeSet<V>(addressComparator);
            index.put(key, s);
        }
        s.add(value);
        avgSetSize += 1;
        avgSetSize /= 1;
    }

    Iterator<V> get(K key){
        Set<V> s = index.get(key);
        return (s != null) ? s.iterator() : null;
    }

    Index<K, V> subIndex(K from_key, boolean fromInclusive, K to_key, boolean toInclusive){
        return new Index<K, V>(transformer, index.subMap(from_key, fromInclusive, to_key, toInclusive));
    }

    double estimate(){
        return index.size() * avgSetSize;
    }

    @Override
    public Iterator<V> iterator() {
        return new TwofoldIterator<V>(index.values());
    }
}