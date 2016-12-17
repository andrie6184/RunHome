package com.runnerfun.tools;


/**
 * Triple
 * Created by andrie on 27/11/2016.
 */

public class Triple<A, B, C> {
    public final A first;
    public final B second;
    public final C third;

    public Triple(A a, B b, C c){
        first = a;
        second = b;
        third = c;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Triple)) {
            return false;
        }
        Triple<?, ?, ?> p = (Triple<?, ?, ?>) o;
        return objectsEqual(p.first, first)
                && objectsEqual(p.second, second) && objectsEqual(p.third, third);
    }

    private static boolean objectsEqual(Object a, Object b) {
        return a == b || (a != null && a.equals(b));
    }

    @Override
    public int hashCode() {
        return (first == null ? 0 : first.hashCode())
                ^ (second == null ? 0 : second.hashCode()
                ^ (third == null ? 0 : third.hashCode()));
    }

}
