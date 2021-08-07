package net.badbird5907.blib.objects.maps.tri;

import net.badbird5907.blib.objects.tuple.Triplet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * {@link HashMap} but with three values and one key
 * <b>I know it's not the right terminology; don't bully me</b>
 * @param <K> key
 * @param <V> value 1
 * @param <M> value 2
 * @param <A> value 3
 */
public class HashTriMap<K, V, M, A> implements TriMap {
	Map<K, Triplet<V, M, A>> base = new HashMap<>();
	transient Set<Entry<K, V, M, A>> entrySet;

	@Override
	public Set<Entry> entrySet() {
		Set<Entry> set = new HashSet<>();
		base.forEach((k, pair) -> set.add(new Entry() {
			@Override
			public Object getKey() {
				return k;
			}

			@Override
			public Object getValue1() {
				return pair.getValue0();
			}

			@Override
			public Object getValue2() {
				return pair.getValue1();
			}

			@Override
			public Object getValue3() {
				return pair.getValue2();
			}
		}));
		return set;
	}

	@Override
	public Set<Triplet> values() {
		return new HashSet<>(base.values());
	}

	@Override
	public Set keySet() {
		return base.keySet();
	}

	@Override
	public void clear() {
		base.clear();
	}

	@Override
	public void putAll(TriMap e) {
		e.forEach((k, v, m, a) -> base.put((K) k, new Triplet<>((V) v, (M) m, (A) a)));
	}

	@Override
	public Object remove(Object k) {
		return base.remove(k);
	}

	@Override
	public Object put(Object key, Object value1, Object value2,Object value3) {
		return base.put((K) key, new Triplet<>((V) value1, (M) value2, (A) value3));
	}

	@Override
	public Triplet get(Object key) {
		return base.get(key);
	}

	@Override
	public boolean containsValue(Object val) {
		AtomicBoolean contains = new AtomicBoolean(false);
		base.forEach((k, pair) -> {
			if ((pair.getValue1() == val) || (pair.getValue0() == val)) contains.set(true);
		});
		return contains.get();
	}

	@Override
	public boolean containsKey(Object val) {
		return base.containsKey(val);
	}

	@Override
	public int size() {
		return base.size();
	}
}
