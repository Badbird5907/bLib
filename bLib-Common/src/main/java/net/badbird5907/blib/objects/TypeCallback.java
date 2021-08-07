package net.badbird5907.blib.objects;

/**
 * {@link Callback} but with a return type
 * @param <A>
 */
public interface TypeCallback<A> {
	A call();
}
