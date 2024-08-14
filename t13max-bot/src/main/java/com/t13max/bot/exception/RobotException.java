package com.t13max.bot.exception;

/**
 * @author: t13max
 * @since: 16:27 2024/4/7
 */
public class RobotException extends RuntimeException {

    private final int errorCode;

    public RobotException() {
        this.errorCode = 0;
    }

    public RobotException(int errorCode) {
        this.errorCode = errorCode;
    }

    public RobotException(String message) {
        this();
    }
}
