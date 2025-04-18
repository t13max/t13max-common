package com.t13max.agent.wrap;

import lombok.Getter;

/**
 * 类定义包装
 *
 * @author: t13max
 * @since: 15:41 2024/8/12
 */
@Getter
public class ClassDefinitionWrap {

    //目标class对象
    private final Class<?> clazz;
    //类名
    private final String className;
    //新的class二进制
    private final byte[] newBytes;
    //老的class二进制
    private final byte[] oldBytes;

    public ClassDefinitionWrap(Class<?> clazz, byte[] newBytes, byte[] oldBytes, String className) {
        this.clazz = clazz;
        this.newBytes = newBytes;
        this.oldBytes = oldBytes;
        this.className = className;
    }

}
