package com.t13max.rpc.anno;

import java.lang.annotation.*;

/**
 * RPC客户端接口调用限流器
 *
 * @Author t13max
 * @Date 17:40 2024/10/30
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface RPCRateLimiter {

    //限流默认值,单位:次/秒
    int DEFAULT_RATE = 10000;

    // 每秒接口调用数
    int rateLimitPerSeconds() default DEFAULT_RATE;
}
