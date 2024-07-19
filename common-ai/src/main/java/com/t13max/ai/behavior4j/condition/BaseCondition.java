package com.t13max.ai.behavior4j.condition;

/**
 * 条件接口
 *
 * @Author t13max
 * @Date 16:05 2024/5/17
 */
public interface BaseCondition {

    default boolean condition() {
        return false;
    }
}
