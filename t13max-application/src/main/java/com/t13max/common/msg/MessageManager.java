package com.t13max.common.msg;

import com.google.protobuf.MessageLite;
import com.t13max.common.exception.CommonException;
import com.t13max.common.manager.ManagerBase;
import com.t13max.common.session.ISession;
import com.t13max.common.util.Log;
import com.t13max.util.PackageUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author: t13max
 * @since: 20:30 2024/5/28
 */
public class MessageManager extends ManagerBase {

    private final Map<Integer, IMessage<?>> messageMap = new HashMap<>();
    private final Map<Integer, Method> parserMap = new HashMap<>();
    private final Map<Integer, Class<?>> classMap = new HashMap<>();

    @Override
    public void init() {

        try {
            Set<Class<?>> classSet = PackageUtil.scan("com.t13max");
            //创建实例
            for (Class<?> clazz : classSet) {
                // 只需要加载TemplateHelper注解数据
                if (!IMessage.class.isAssignableFrom(clazz) || Modifier.isAbstract(clazz.getModifiers())) {
                    continue;
                }

                // 创建实例
                Object inst = clazz.getDeclaredConstructor().newInstance();
                Message annotation = clazz.getAnnotation(Message.class);
                if (annotation == null) {
                    continue;
                }

                IMessage<?> message = (IMessage<?>) inst;
                messageMap.put(annotation.value(), message);

                Method[] declaredMethods = inst.getClass().getDeclaredMethods();
                for (Method declaredMethod : declaredMethods) {
                    if (declaredMethod.getName().equals("doMessage")) {
                        Class<?>[] parameterTypes = declaredMethod.getParameterTypes();
                        for (Class<?> parameterType : parameterTypes) {
                            if (parameterType.isInterface() || !MessageLite.class.isAssignableFrom(parameterType))
                                continue;
                            Method parseForm = getParseMethod(parameterType);
                            parserMap.put(annotation.value(), parseForm);
                            classMap.put(annotation.value(), parameterType);
                            break;
                        }
                    }
                }
            }

        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new CommonException(e);
        }

    }

    private Method getParseMethod(Class<?> parameterType) {
        try {
            return parameterType.getMethod("getDefaultInstance");
        } catch (NoSuchMethodException e) {
            throw new CommonException(e);
        }
    }

    /**
     * 获取当前实例对象
     *
     * @Author t13max
     * @Date 16:44 2024/5/23
     */
    public static MessageManager inst() {
        return ManagerBase.inst(MessageManager.class);
    }

    public <T extends MessageLite> IMessage<T> getMessage(int msgId) {
        return (IMessage<T>) messageMap.get(msgId);
    }

    public Method getParseMethod(int msgId) {
        return parserMap.get(msgId);
    }

    public <T extends MessageLite> T parseMessage(int msgId, byte[] data) {
        Method parseMethod = this.getParseMethod(msgId);
        if (parseMethod == null) {
            Log.MSG.error("parseMethod不存在, msgId={}", msgId);
            return null;
        }

        Class<?> clazz = this.classMap.get(msgId);
        if (clazz == null) {
            Log.MSG.error("clazz不存在, msgId={}", msgId);
            return null;
        }

        T messageLite = null;
        try {

            MessageLite instance = (MessageLite) parseMethod.invoke(clazz);
            messageLite = (T) instance.getParserForType().parseFrom(data);
        } catch (Exception e) {
            //后续添加异常处理
            Log.MSG.error("doMessage error, msgId={}, error={}", msgId, e.getMessage());
        }
        return messageLite;
    }

    public <T extends MessageLite> void doMessage(ISession session, MessagePack<T> messagePack) {
        int msgId = messagePack.getMsgId();
        IMessage<T> message = this.getMessage(msgId);
        if (message == null) {
            Log.MSG.error("msg不存在, msgId={}", msgId);
            return;
        }
        Log.MSG.info("receiveMessage, uuid={}, msgId={}, message={}", session.getUuid(), msgId, message, getClass().getSimpleName());
        message.doMessage(session, messagePack);
    }

    /**
     * 手动添加消息 比如登录那种特殊消息
     *
     * @Author t13max
     * @Date 19:44 2024/5/30
     */
    public <T extends MessageLite> void addMessage(int msgId, IMessage<T> message, Class<T> clazz) {
        this.messageMap.put(msgId, message);
        this.classMap.put(msgId, clazz);
        this.parserMap.put(msgId, getParseMethod(clazz));
    }

    public <T extends MessageLite> void sendMessage(ISession session, MessagePack<T> messagePack) {
        Channel channel = session.getChannel();
        if (!channel.isActive()) {
            Log.MSG.error("sendMessage failed, channel inactive, uuid={}, class={}, MessagePack={}", session.getUuid(), messagePack.getMessageLite().getClass().getSimpleName(), messagePack);
            return;
        }
        ByteBuf byteBuf = messagePack.wrapBuffers();
        channel.writeAndFlush(byteBuf);
        Log.MSG.info("sendMessage, uuid={}, msgId={}, class={}, MessagePack={}", session.getUuid(), messagePack.getMsgId(), messagePack.getMessageLite().getClass().getSimpleName(), messagePack);

    }


}
