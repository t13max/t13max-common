package com.t13max.util.limiter;


/**
 * 限流器
 *
 * @author t13max
 * @since 14:14 2024/11/5
 */
public interface IRateLimiter {

    boolean tryAcquire();
}
