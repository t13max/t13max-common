package com.t13max.common.event;


import com.t13max.common.manager.ManagerBase;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 后续要加入redis发布订阅 弄成跨进程事件系统
 *
 * @author: t13max
 * @since: 14:56 2024/5/29
 */
public class GameEventBus extends ManagerBase {

    private final Map<IEventEnum, LinkedList<IEventListener>> listenersMap = new ConcurrentHashMap<>();

    private final ExecutorService asyncExecutor = Executors.newFixedThreadPool(4);

    public static GameEventBus inst() {
        return inst(GameEventBus.class);
    }

    /**
     * 注册监听
     *
     * @Author t13max
     * @Date 10:29 2024/8/13
     */
    public void register(List<IEventListener> eventListenerList) {
        for (IEventListener eventListener : eventListenerList) {
            register(eventListener);
        }
    }

    /**
     * 注册监听
     *
     * @Author t13max
     * @Date 10:29 2024/8/13
     */
    public void register(IEventListener eventListener) {
        Set<IEventEnum> interestedEvent = eventListener.getInterestedEvent();
        for (IEventEnum eventEnum : interestedEvent) {
            LinkedList<IEventListener> listeners = listenersMap.computeIfAbsent(eventEnum, k -> new LinkedList<>());
            listeners.add(eventListener);
            listeners.sort(Comparator.comparingInt(IEventListener::getPriority));
        }
    }

    /**
     * 注销监听
     *
     * @Author t13max
     * @Date 10:29 2024/8/13
     */
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

    /**
     * 抛出事件 同步触发
     *
     * @Author t13max
     * @Date 10:29 2024/8/13
     */
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

    /**
     * 异步执行事件
     *
     * @Author t13max
     * @Date 10:40 2024/8/13
     */
    public void postEventAsync(IEvent event) {
        asyncExecutor.execute(() -> this.postEvent(event));
    }
}
