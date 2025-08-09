package com.t13max.common.msg;

import com.google.protobuf.MessageLite;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

/**
 * 客户端消息
 *
 * @author: t13max
 * @since: 18:19 2024/5/30
 */
public class ClientMessagePack<T extends MessageLite> extends MessagePack<T> {

    public ClientMessagePack(int msgId, T messageLite) {
        this.msgId = msgId;
        this.messageLite = messageLite;
    }

    @Override
    public ByteBuf wrapBuffers() {
        int len = MessageConst.HEADER_LENGTH;
        byte[] data = null;
        if (messageLite != null) {
            data = messageLite.toByteArray();
        }
        if (null != data) len += data.length;
        ByteBuf buf = PooledByteBufAllocator.DEFAULT.buffer(len);
        buf.writeInt(len);
        buf.writeInt(msgId);
        if (null != data) {
            buf.writeBytes(data);
        }
        return buf;
    }
}
