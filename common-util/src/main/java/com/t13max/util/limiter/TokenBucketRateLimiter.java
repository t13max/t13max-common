package com.t13max.util.limiter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 令牌桶限流器
 *
 * @author t13max
 * @since 14:15 2024/11/5
 */
public class TokenBucketRateLimiter implements IRateLimiter {

    private final static ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private final int maxTokens;
    private final int refillRate;
    private AtomicInteger tokens;

    public TokenBucketRateLimiter(int maxTokens, int refillRate) {
        this.maxTokens = maxTokens;
        this.refillRate = refillRate;
        executorService.scheduleAtFixedRate(() -> {
            if (tokens.get() < maxTokens) {
                tokens.incrementAndGet();
            }
        }, 0, 1000 / refillRate, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean tryAcquire() {
        return tokens.getAndDecrement() > 0;
    }
}
