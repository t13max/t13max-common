package com.t13max.common.exception;

/**
 * 基础异常
 * 非业务逻辑 非数据库 等等 就是调用架构方面出现的异常
 *
 * @author: t13max
 * @since: 14:11 2024/5/23
 */
public class CommonException extends RuntimeException {

    public CommonException(String message) {
        super(message);
    }

    public CommonException(Throwable cause) {
        super(cause);
    }
}
