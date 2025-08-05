package com.t13max.common.util;


import lombok.Getter;

/**
 * @author t13max
 * @since 12:12 2025/1/15
 */
@Getter
public class DTO<K extends Enum<K>, T> {

    private final K err;
    private final T data;

    private DTO(K err, T data) {
        this.err = err;
        this.data = data;
    }

    public static <K extends Enum<K>, T> DTO<K, T> ok(K code, T data) {
        return new DTO<>(code, data);
    }

    public static <K extends Enum<K>, T> DTO<K, T> ok() {
        return new DTO<>(null, null);
    }

    public static <K extends Enum<K>, T> DTO<K, T> ok(T data) {
        return new DTO<>(null, data);
    }

    public static <K extends Enum<K>, T> DTO<K, T> error(K err) {
        return new DTO<>(err, null);
    }

    public static <K extends Enum<K>, T> DTO<K, T> error(K err, T data) {
        return new DTO<>(err, data);
    }

    public boolean isOk() {
        return this.err == null || this.err.ordinal() == 0;
    }

    public boolean isError() {
        return this.err != null && this.err.ordinal() != 0;
    }
}
