package com.t13max.data.mongo.util;

import lombok.experimental.UtilityClass;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;

/**
 * @author: t13max
 * @since: 13:57 2024/5/29
 */
@UtilityClass
public class UuidUtil {

    // 起始时间戳(2024-05-29 00:00:00)
    private final static long START_TIME = 1716912000000L;
    //机器号位
    private final static int MACHINE_BITS = 10;
    //序列位
    private final static int SEQ_BITS = 12;
    //最大序号
    private final static long MAX_SEQ = (1L << SEQ_BITS) - 1;

    private final static int MACHINE_SHIFT = SEQ_BITS;

    private final static int TIME_SHIFT = SEQ_BITS + MACHINE_BITS;

    // 当前实例的机器id
    private final static long machineId = 1;
    //State
    private final static AtomicLong state = new AtomicLong((System.currentTimeMillis() - START_TIME) << SEQ_BITS);

    public static long getNextId() {

        for (; ; ) {

            long now = System.currentTimeMillis() - START_TIME;
            long s = state.get();
            long ts = s >>> SEQ_BITS;
            long seq = s & MAX_SEQ;

            // 时钟回拨 简单等待到不回拨
            if (now < ts) {
                //log.warn("时钟回拨, id生成受阻, 等待中...");
                now = waitNextMillis(ts);
            }

            if (ts < now) {
                // 进入新毫秒 序列从0开始 通过CAS设置到新毫秒的state
                long desired = (now << SEQ_BITS);
                //成功cas的直接返回第一个id
                if (state.compareAndSet(s, desired)) {
                    return buildId(now, 0);
                }
                //否则进入下一次循环 走同毫秒内的递增逻辑
                continue;
            }

            // 同毫秒内递增
            if (seq < MAX_SEQ) {
                //序号加1
                long nextSeq = seq + 1;
                long desired = (ts << SEQ_BITS) | nextSeq;
                //成功递增的返回
                if (state.compareAndSet(s, desired)) {
                    return buildId(ts, nextSeq);
                }
                //否则重试
                continue;
            }

            // 本毫秒序列已满 等到下一毫秒 再CAS到0
            long nextTs = waitNextMillis(ts);
            long desired = (nextTs << SEQ_BITS);
            if (state.compareAndSet(s, desired)) {
                return buildId(nextTs, 0);
            }
        }
    }

    private static long waitNextMillis(long lastTs) {
        long t;
        do {
            t = System.currentTimeMillis() - START_TIME;
            Thread.yield();
        } while (t <= lastTs);
        return t;
    }

    private static long buildId(long ts, long seq) {
        return (ts << TIME_SHIFT) | (machineId << MACHINE_SHIFT) | seq;
    }

    public static void main(String[] args) {

        testGenerateId();
        //checkId();
    }

    private static void testGenerateId() {
        for (int i = 0; i < 60; i++) {
            Thread.startVirtualThread(() -> {
                for (int j = 0; j < 60; j++) {
                    System.out.println(UuidUtil.getNextId() + "L,");
                }
            });
        }

        LockSupport.park();
    }

    private static void checkId() {
        long[] array = new long[]{

        };

        Set<Long> set = new HashSet<>();

        for (long id : array) {
            set.add(id);
        }

        System.out.println(array.length + " " + set.size());
    }

}
