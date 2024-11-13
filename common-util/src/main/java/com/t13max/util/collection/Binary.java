package com.t13max.util.collection;

/**
 * 二元组
 *
 * @author: t13max
 * @since: 11:28 2024/8/2
 */
public class Binary<X, Y> {

    private X x;

    private Y y;

    public Binary() {
    }

    public Binary(X x, Y y) {
        this.x = x;
        this.y = y;
    }

    public Binary(Binary<X,Y> binary) {
        this.x = binary.x;
        this.y = binary.y;
    }

    public X getX() {
        return x;
    }

    public void setX(X x) {
        this.x = x;
    }

    public Y getY() {
        return y;
    }

    public void setY(Y y) {
        this.y = y;
    }
}
