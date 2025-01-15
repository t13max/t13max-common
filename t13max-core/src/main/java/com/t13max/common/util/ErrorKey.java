package com.t13max.common.util;

/**
 * @author t13max
 * @since 13:08 2025/1/15
 */
public interface ErrorKey<T extends Enum<T>>{

    boolean isOk();

    boolean isError();
}
