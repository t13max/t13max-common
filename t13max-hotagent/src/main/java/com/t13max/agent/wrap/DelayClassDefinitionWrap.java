package com.t13max.agent.wrap;

import lombok.Getter;
import lombok.Setter;

/**
 * @author: t13max
 * @since: 15:42 2024/8/12
 */
@Setter
@Getter
public class DelayClassDefinitionWrap {

    //类对象
    private Class<?> clazz;
    //老的二进制
    private byte[] bytes;

    public DelayClassDefinitionWrap(Class<?> clazz, byte[] bytes) {
        if (clazz == null) {
            throw new NullPointerException("DelayClassDefinitionWrap中class不能为空");
        } else {
            this.clazz = clazz;
            this.bytes = bytes;
        }
    }

}
