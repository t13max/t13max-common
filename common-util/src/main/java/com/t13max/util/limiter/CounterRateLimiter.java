package com.t13max.util.limiter;

import com.t13max.util.TimeUtil;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 计数器限流器
 *
 * @author t13max
 * @since 14:18 2024/11/5
 */
public class CounterRateLimiter implements IRateLimiter {

    private final int maxRequests;
    private final long timeWindowMillis;
    private final ReentrantLock lock = new ReentrantLock();
    private final AtomicInteger requestCount = new AtomicInteger(0);

    private long windowStart = TimeUtil.nowMills();

    public CounterRateLimiter(int maxRequests, long timeWindowMillis) {
        this.maxRequests = maxRequests;
        this.timeWindowMillis = timeWindowMillis;
    }

    @Override
    public boolean tryAcquire() {
        long now = TimeUtil.nowMills();
        // 如果超出当前时间窗口，重置计数器和窗口时间
        if (now - windowStart > timeWindowMillis) {
            lock.lock();
            try {
                if (now - windowStart > timeWindowMillis) {
                    windowStart = now;
                    requestCount.set(0);
                }
            } finally {
                lock.unlock();
            }
        }
        // 检查请求数是否超出限制
        return requestCount.incrementAndGet() <= maxRequests;
    }
}
