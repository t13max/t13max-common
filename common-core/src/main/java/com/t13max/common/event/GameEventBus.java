package com.t13max.common.event;


import com.t13max.common.manager.ManagerBase;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 后续要加入redis发布订阅 弄成跨进程事件系统
 *
 * @author: t13max
 * @since: 14:56 2024/5/29
 */
public class GameEventBus extends ManagerBase {

    private final Map<IEventEnum, LinkedList<IEventListener>> listenersMap = new ConcurrentHashMap<>();

    public static GameEventBus inst() {
        return inst(GameEventBus.class);
    }

    public void register(List<IEventListener> eventListenerList) {
        for (IEventListener eventListener : eventListenerList) {
            register(eventListener);
        }
    }

    public void register(IEventListener eventListener) {
        Set<IEventEnum> interestedEvent = eventListener.getInterestedEvent();
        for (IEventEnum eventEnum : interestedEvent) {
            LinkedList<IEventListener> listeners = listenersMap.computeIfAbsent(eventEnum, k -> new LinkedList<>());
            listeners.add(eventListener);
            listeners.sort(Comparator.comparingInt(IEventListener::getPriority));
        }
    }

    public void unregister(IEventListener eventListener) {
        Set<IEventEnum> interestedEvent = eventListener.getInterestedEvent();
        for (IEventEnum eventEnum : interestedEvent) {
            LinkedList<IEventListener> listeners = listenersMap.get(eventEnum);
            if (listeners == null) {
                continue;
            }
            listeners.remove(eventListener);
        }
    }

    public void postEvent(IEvent event) {
        IEventEnum eventEnum = event.getEventEnum();

        LinkedList<IEventListener> listeners = listenersMap.get(eventEnum);
        if (listeners == null) {
            return;
        }

        for (IEventListener listener : listeners) {
            listener.onEvent(event);
        }
    }
}
