package com.t13max.common.msg;

import com.google.protobuf.MessageLite;
import com.t13max.common.session.ISession;

/**
 * 消息接口
 *
 * @author: t13max
 * @since: 19:41 2024/5/23
 */
public interface IMessage<T extends MessageLite> {

    void doMessage(ISession session, MessagePack<T> messagePack);
}
