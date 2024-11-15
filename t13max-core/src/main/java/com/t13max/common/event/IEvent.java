package com.t13max.common.event;

/**
 * 事件接口
 * 如果是跨进程事件 则必须有无参构造和set方法或全参构造
 *
 * @author: t13max
 * @since: 14:54 2024/5/29
 */
public interface IEvent {

    IEventEnum getEventEnum();
}
