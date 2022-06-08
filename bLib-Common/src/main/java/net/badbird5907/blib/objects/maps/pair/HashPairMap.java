package net.badbird5907.blib.objects.maps.pair;

import net.badbird5907.blib.objects.tuple.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * {@link HashMap} but with 2 values
 * <b>I know it's not the right terminology dont bully me</b>
 *
 * @param <K> key
 * @param <V> value1
 * @param <M> value2
 */
public class HashPairMap<K, V, M> implements PairMap {
	Map<K, Pair<V, M>> base = new ConcurrentHashMap<>();
	transient Set<Entry<K, V, M>> entrySet;

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
		}));
		return set;
	}

	@Override
	public Set<Pair> values() {
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
	public void putAll(PairMap e) {
		e.forEach((k, v, m) -> base.put((K) k, new Pair<>((V) v, (M) m)));
	}

	@Override
	public Object remove(Object k) {
		return base.remove(k);
	}

	@Override
	public Object put(Object key, Object value1, Object value2) {
		return base.put((K) key, new Pair<>((V) value1, (M) value2));
	}

	@Override
	public Pair get(Object key) {
		return base.get(key);
	}

	@Override
	public boolean containsValue(Object val) {
		AtomicBoolean contains = new AtomicBoolean(false);
		base.forEach((k, pair) -> {
			if (pair.getValue1() == val || pair.getValue0() == val)
				contains.set(true);
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
