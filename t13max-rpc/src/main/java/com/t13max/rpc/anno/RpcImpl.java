package com.t13max.rpc.anno;

import java.lang.annotation.*;

/**
 * RPC服务实现类注解
 *
 * @Author t13max
 * @Date 16:54 2024/9/11
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface RpcImpl {

}
