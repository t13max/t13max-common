package com.t13max.rpc.anno;

import java.lang.annotation.*;

/**
 * RPC接口注解
 *
 * @Author t13max
 * @Date 16:54 2024/9/11
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface RpcInterface {

    //接口所属组
    String group() default "";

    // 恢复正常阈值,成功调用次数
    int successThresholdSuccess() default 3;

    // 恢复正常阈值,总调用次数
    int successThresholdExecutions() default 3;

    // 进入熔断阈值,失败次数
    int failureThresholdFailures() default 4;

    // 进入熔断阈值,总调用次数
    int failureThresholdExecutions() default 5;

    // 从open切到half-open时间间隔,单位秒, open:接口异常,开启熔断 close:接口正常,关闭熔断 half-open:半开状态,尝试接口是否恢复
    int breakerDelay() default 5;
}
