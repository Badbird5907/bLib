package net.badbird5907.blib.objects.tuple;

public class Triplet<A,B,C> {
    private A value0;
    private B value1;
    private C value2;
    public Triplet(final A v0,final B v1,final C v2){
        this.value0 = v0;
        this.value1 = v1;
        this.value2 = v2;
    }

    public A getValue0() {
        return value0;
    }

    public B getValue1() {
        return value1;
    }

    public C getValue2() {
        return value2;
    }
}
