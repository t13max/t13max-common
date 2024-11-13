package com.t13max.util.collection;

import lombok.Getter;

/**
 * 二元组
 *
 * @author: t13max
 * @since: 11:28 2024/8/2
 */
@Getter
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
}
