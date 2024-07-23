package com.t13max.common.object;

/**
 * 创建池化对象的工厂
 *
 * @author: t13max
 * @since: 16:30 2024/7/23
 */
public interface ObjectFactory<T extends IPooledObject> {

    T create();
}
