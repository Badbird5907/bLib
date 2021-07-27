package net.badbird5907.blib.objects.maps.pair;

import net.badbird5907.blib.objects.TriConsumer;
import net.badbird5907.blib.objects.tuple.Pair;
import java.util.*;

public interface PairMap<K,V,M>{
    interface Entry<K,V,M>{
        K getKey();
        V getValue1();
        M getValue2();
    }
    Set<Entry<K,V,M>> entrySet();
    Set<Pair<V,M>> values();
    Set<K> keySet();
    void clear();
    void putAll(PairMap<? extends K,? extends V,? extends M > e);
    V remove(Object k);
    V put(K key,V value1,M value2);
    Pair<V,M> get(K key);
    boolean containsValue(Object val);
    boolean containsKey(Object val);
    int size();
    default void forEach(TriConsumer<? super K,? super V,? super M> action){
        Objects.requireNonNull(action);
        for (Entry<K,V,M> entry : entrySet()) {
            K k;
            V v;
            M m;
            try{
                k = entry.getKey();
                v = entry.getValue1();
                m = entry.getValue2();
            } catch (IllegalStateException e) {
                throw new ConcurrentModificationException(e);
            }
            action.accept(k,v,m);
        }
    }
}
