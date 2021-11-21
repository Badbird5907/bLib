package net.badbird5907.blib.objects;

import static java.util.Objects.requireNonNull;

/**
 * {@link java.util.function.BiConsumer} but with 4 arguments
 *
 * @param <A> input 1
 * @param <B> input 2
 * @param <C> input 3
 * @param <D> input 4
 */
public interface QuadConsumer<A, B, C, D> {
	void accept(A a, B b, C c, D d);

	default QuadConsumer<A, B, C, D> andThen(QuadConsumer<? super A, ? super B, ? super C, ? super D> after) {
		requireNonNull(after);
		return (a, b, c, d) -> {
			accept(a, b, c, d);
			after.accept(a, b, c, d);
		};
	}
}
