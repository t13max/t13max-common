package com.t13max.ai.utils;

import java.util.Random;

/**
 * @Author t13max
 * @Date 13:50 2024/5/23
 */
public class MathUtils {

    private static Random random = new Random();

    public static int random (int start, int end) {
        return start + random.nextInt(end - start + 1);
    }
}
