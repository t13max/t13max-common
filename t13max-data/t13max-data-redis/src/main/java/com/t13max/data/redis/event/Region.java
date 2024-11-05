package com.t13max.data.redis.event;

/**
 * 作用域
 * 当前进程 全局 指定进程 同类进程 等
 *
 * @author: t13max
 * @since: 10:32 2024/8/13
 */
public enum Region {

    //全局
    GLOBAL,

    //当前进程内
    IN_PROCESS,

    //同类进程
    SAME_SERVICE,

}
