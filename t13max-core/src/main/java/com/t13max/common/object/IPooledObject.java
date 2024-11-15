package com.t13max.common.object;

/**
 * 池化对象接口
 *
 * @author: t13max
 * @since: 16:29 2024/7/23
 */
public interface IPooledObject {

    //借用的时候调用
    void borrowObject();

    //归还的时候调用
    void returnObject();
}
