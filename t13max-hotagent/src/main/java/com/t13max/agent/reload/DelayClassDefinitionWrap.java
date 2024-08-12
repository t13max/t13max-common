package com.t13max.agent.reload;

import lombok.Getter;
import lombok.Setter;

/**
 * @author: t13max
 * @since: 15:42 2024/8/12
 */
@Setter
@Getter
public class DelayClassDefinitionWrap {
    private Class<?> clazz;
    private byte[] bytes;

    public DelayClassDefinitionWrap(Class<?> clazz, byte[] bytes) {
        if (clazz == null) {
            throw new NullPointerException("DelayClassDefinitionWrap中cls不能为空");
        } else {
            this.clazz = clazz;
            this.bytes = bytes;
        }
    }

}
