package net.badbird5907.blib.objects.maps.tri;

import net.badbird5907.blib.objects.QuadConsumer;
import net.badbird5907.blib.objects.tuple.Triplet;
import java.util.ConcurrentModificationException;
import java.util.Objects;
import java.util.Set;

public interface TriMap<K,V,M,A> {
    interface Entry<K,V,M,A>{
        K getKey();
        V getValue1();
        M getValue2();
        A getValue3();
    }
    Set<Entry<K,V,M,A>> entrySet();
    Set<Triplet<V,M,A>> values();
    Set<K> keySet();
    void clear();
    void putAll(TriMap<? extends K,? extends V,? extends M ,? extends A> e);
    V remove(Object k);
    V put(K key,V value1,M value2,A value3);
    Triplet<V,M,A> get(K key);
    boolean containsValue(Object val);
    boolean containsKey(Object val);
    int size();
    default void forEach(QuadConsumer<? super K,? super V,? super M,? super A> action){
        Objects.requireNonNull(action);
        for (Entry<K,V,M,A> entry : entrySet()) {
            K k;
            V v;
            M m;
            A a;
            try{
                k = entry.getKey();
                v = entry.getValue1();
                m = entry.getValue2();
                a = entry.getValue3();
            } catch (IllegalStateException e) {
                throw new ConcurrentModificationException(e);
            }
            action.accept(k,v,m,a);
        }
    }
}
