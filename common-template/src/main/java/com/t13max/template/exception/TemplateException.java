package com.t13max.template.exception;

/**
 * 读表异常
 *
 * @author: t13max
 * @since: 11:24 2024/8/2
 */
public class TemplateException extends RuntimeException {

    public TemplateException() {
    }

    public TemplateException(String message) {
        super(message);
    }

    public TemplateException(String message, Throwable cause) {
        super(message, cause);
    }

    public TemplateException(Throwable cause) {
        super(cause);
    }

    public TemplateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
