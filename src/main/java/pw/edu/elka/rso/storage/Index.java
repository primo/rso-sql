package pw.edu.elka.rso.storage;

import java.util.*;

import static java.lang.System.*;

interface Transformer<A, B>{
    B transform(A a);
}

//Nie przyjmuje nulli jako wartosci v
class Index<K, V>{
    Map<K, Set<V>> index;
    Transformer<V, K> transformer;
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

    void put(V value){
        K key = transformer.transform(value);
        Set<V> s = index.get(key);
        if (s == null){
            s = new TreeSet<V>(addressComparator);
            index.put(key, s);
        }
        s.add(value);
    }

    Iterator<V> get(K key){
        Set<V> s = index.get(key);
        return (s != null) ? s.iterator() : null;
    }

    Iterator<V> get(K from_key, boolean fromInclusive, K to_key, boolean toInclusive){
        Set<V> s = index.get(from_key);
        return (s != null) ? s.iterator() : null;
    }
}