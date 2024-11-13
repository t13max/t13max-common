package com.t13max.util.collection;

/**
 * 三元组
 *
 * @author: t13max
 * @since: 11:27 2024/8/2
 */
public class Triple<X, Y, Z> extends Binary<X, Y> {

    private Z z;

    public Triple() {

    }

    public Triple(X x, Y y, Z z) {
        super(x, y);
        this.z = z;
    }

    public Triple(Triple<X, Y, Z> triple) {
        super(triple.getX(), triple.getY());
        this.z = triple.z;
    }

    public Z getZ() {
        return z;
    }

    public void setZ(Z z) {
        this.z = z;
    }
}
