package net.badbird5907.blib.objects.tuple;

/**
 * three types
 * @param <A>
 * @param <B>
 * @param <C>
 */
public class Triplet<A,B,C> {
    private A value0;
    private B value1;
    private C value2;
    public Triplet(final A v0,final B v1,final C v2){
        this.value0 = v0;
        this.value1 = v1;
        this.value2 = v2;
    }

    /**
     * gets the first value
     * @return
     */
    public A getValue0() {
        return value0;
    }

    /**
     * gets the second value
     * @return
     */
    public B getValue1() {
        return value1;
    }

    /**
     * gets the third value
     * @return
     */
    public C getValue2() {
        return value2;
    }
}
