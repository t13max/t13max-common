package com.t13max.common.session;

import com.t13max.common.manager.ManagerBase;
import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: t13max
 * @since: 20:13 2024/5/28
 */
public class SessionManager extends ManagerBase {

    private final Map<Channel, ISession> sessionMap = new ConcurrentHashMap<>();

    /**
     * 获取当前实例对象
     *
     * @Author t13max
     * @Date 16:44 2024/5/23
     */
    public static SessionManager inst() {
        return ManagerBase.inst(SessionManager.class);
    }

    @Override
    public void init() {

    }

    public ISession getSession(Channel channel) {
        return sessionMap.get(channel);
    }

    public ISession removeSession(Channel channel) {
        return sessionMap.remove(channel);
    }

    public void putSession(ISession session) {
        this.sessionMap.put(session.getChannel(), session);
    }
}
