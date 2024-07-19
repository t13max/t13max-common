package com.t13max.common.event;

import java.util.Set;

/**
 * 监听接口
 *
 * @author: t13max
 * @since: 14:57 2024/5/29
 */
public interface IEventListener {

    Set<IEventEnum> getInterestedEvent();

    void onEvent(It13maxEvent gameEvent);

    int getPriority();
}
