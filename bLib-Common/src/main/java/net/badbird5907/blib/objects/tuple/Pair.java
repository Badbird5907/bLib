package net.badbird5907.blib.objects.tuple;

/**
 * pair of two types
 *
 * @param <A> value 1
 * @param <B> value 2
 */
public class Pair<A, B> {
	private A value0;
	private B value1;

	public Pair(final A v0, B v1) {
		this.value0 = v0;
		this.value1 = v1;
	}

	/**
	 * gets the first value
	 *
	 * @return
	 */
	public A getValue0() {
		return value0;
	}

	/**
	 * gets the second value
	 *
	 * @return
	 */
	public B getValue1() {
		return value1;
	}
}
