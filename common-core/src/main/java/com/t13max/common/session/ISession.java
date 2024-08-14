package com.t13max.common.session;

import com.google.protobuf.MessageLite;
import io.netty.channel.Channel;

/**
 * @author: t13max
 * @since: 19:42 2024/5/23
 */
public interface ISession {

    long getUuid();

    void setUuid(long uuid);

    long getRoleId();

    void setRoleId(long roleId);

    Channel getChannel();

    void sendMessage(int msgId, int resCode, MessageLite messageLite);

    void sendMessage(int msgId, MessageLite messageLite);

    void sendError(int msgId, int errorCode);
}
