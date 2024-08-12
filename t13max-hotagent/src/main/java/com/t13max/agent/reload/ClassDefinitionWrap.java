package com.t13max.agent.reload;

import lombok.Getter;

/**
 * @author: t13max
 * @since: 15:41 2024/8/12
 */
@Getter
public class ClassDefinitionWrap {
    private final Class<?> clazz;
    private final String className;
    private final byte[] entryBytes;
    private final byte[] originalBytes;

    public ClassDefinitionWrap(Class<?> clazz, byte[] entryBytes, byte[] bytes, String className) {
        this.clazz = clazz;
        this.entryBytes = entryBytes;
        this.originalBytes = bytes;
        this.className = className;
    }

}
