package com.t13max.common.event;

/**
 * 抽象事件类
 *
 * @author: t13max
 * @since: 14:58 2024/5/29
 */
public class AbstractEvent implements It13maxEvent {

    protected IEventEnum eventEnum;

    @Override
    public IEventEnum getEventEnum() {
        return eventEnum;
    }
}
