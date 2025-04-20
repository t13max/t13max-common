package com.t13max.util;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * @author t13max
 * @since 14:20 2025/1/15
 */
public class RecoverRandom {

    private final Random random;

    private final long seed;

    private final AtomicInteger count = new AtomicInteger();

    public RecoverRandom(long seed) {
        this.random = new Random(seed);
        this.seed = seed;
    }

    public RecoverRandom(long seed, int count) {
        this(seed);
        for (int i = 0; i < count; i++) {
            this.random.nextInt();
            this.count.incrementAndGet();
        }
    }

    public int getCount() {
        return count.get();
    }

    public long getSeed() {
        return this.seed;
    }

    public boolean gaiLvTo(double num) {
        return RandomUtil.gaiLvTo(random, num);
    }

    public int nextInt(int min, int max) {
        return RandomUtil.nextInt(random, min, max);
    }

    public int nextInt(int max) {
        return nextInt(0, max);
    }

    public long nextInt(long min, long max) {
        return RandomUtil.nextInt(random, min, max);
    }

    public double getRanNumByIntervalDouble(double min, double max) {
        return RandomUtil.getRanNumByIntervalDouble(random, min, max);
    }

    public Set<Integer> getRanListByInterval(int min, int max, int n) {
        return RandomUtil.getRanListByInterval(random, min, max, n);
    }

    public int randomIndexByWeight(int[] weight) {
        return RandomUtil.randomIndexByWeight(random, weight);
    }

    public <T> T random(List<T> list, Function<T, Integer> function) {
        return RandomUtil.random(random, list, function);
    }

    public <T> T random(T[] array, Function<T, Integer> function) {
        return RandomUtil.random(random, array, function);
    }

    public <T> List<T> randoms(List<T> list, int resultNum, Function<T, Integer> function) {
        return RandomUtil.randoms(random, list, resultNum, function);
    }

    public <T> T random(List<T> list) {
        return RandomUtil.random(random, list);
    }

    public <T> T random(T[] array) {
        return RandomUtil.random(random, array);
    }

    public <T> List<T> random(T[] array, int num) {
        return RandomUtil.random(random, array, num);
    }

    public <T> List<T> random(List<T> list, int num) {
        return RandomUtil.random(random, list, num);
    }

    public int getRandomByWeight(Map<Integer, Integer> map) {
        return RandomUtil.getRandomByWeight(random, map);
    }

    public int getRandomByWeight(List<Integer> list) {
        return RandomUtil.getRandomByWeight(random, list);
    }

    public int getRandomByWeight(int[] array) {
        return RandomUtil.getRandomByWeight(random, array);
    }

    public int getRandomByWeight(String randomString) {
        return RandomUtil.getRandomByWeight(random, randomString);
    }

    public Set<Integer> getRandomManyByWeight(String randomString, int num) {
        return RandomUtil.getRandomManyByWeight(random, randomString, num);
    }

    public Set<Integer> random(String str, int resultNum) {
        return random(str, resultNum, ",");
    }

    public Set<Integer> random(String str, int resultNum, String splitStr) {
        return RandomUtil.random(random, str, resultNum, splitStr);
    }
}
