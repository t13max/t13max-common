package com.t13max.bot.robot;

import com.google.protobuf.AbstractMessage;
import com.t13max.bot.consts.BotStatusEnum;
import com.t13max.bot.interfaces.IBot;
import com.t13max.bot.interfaces.IBusiness;
import com.t13max.common.msg.MessagePack;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * @Author t13max
 * @Date 16:36 2024/8/14
 */
@Getter
public abstract class AbstractRobot implements IBot {

    //机器人状态
    protected BotStatusEnum botStatus = BotStatusEnum.INIT;
    //账号信息
    protected final RobotAccount robotAccount;
    //ip
    protected final String gatewayUri;
    //服务器id
    protected final int serverId;
    //是否已上线
    @Setter
    protected boolean online;
    //服务器时间
    @Setter
    protected long serverTime;
    //当前正在执行的业务
    protected IBusiness nowBusiness;
    //收到的想消息缓存
    protected final Map<String, LinkedBlockingQueue<AbstractMessage>> receiveMessageMap = new ConcurrentHashMap<>(16);
    //机器人锁
    protected final Lock lock = new ReentrantLock();

    public AbstractRobot(RobotAccount robotAccount, String gatewayUri, int serverId) {
        this.robotAccount = robotAccount;
        this.gatewayUri = gatewayUri;
        this.serverId = serverId;
    }

    @Override
    public long getId() {
        return this.robotAccount.getId();
    }

    @Override
    public void tick() {
        if (lock.tryLock()) {
            try {
                doTick();
            } catch (Exception exception) {
                //bot的异常处理 此处省略一系列牛逼逼的处理操作 大约3000行代码
                exception.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    protected abstract void doTick();

    /**
     * 修改机器人状态
     *
     * @Author t13max
     * @Date 14:19 2024/4/17
     */
    @Override
    public void updateBotStatus(BotStatusEnum statusEnum) {

    }

    @Override
    public void close() {

    }

    @Override
    public <Req extends MessagePack<?>, Resp extends MessagePack<?>> void sendMsgAsync(int msgId, Req messageLite) {

    }

    @Override
    public <Req extends MessagePack<?>, Resp extends MessagePack<?>> void sendMsgAsync(int msgId, Req messageLite, Consumer<Resp> consumer) {

    }

    /**
     * 接受消息 存入messageMap
     *
     * @Author t13max
     * @Date 10:25 2024/8/6
     */
    protected <Resp extends AbstractMessage> void receiveMessage(String eventName, Resp resp) {
        LinkedBlockingQueue<AbstractMessage> messageQueue = this.receiveMessageMap.computeIfAbsent(eventName, k -> new LinkedBlockingQueue<>());
        if (!messageQueue.offer(resp)) {
            logger.error("服务器消息推送队列满了! eventName={}", eventName);
        }
    }

    @Override
    public <T extends AbstractMessage> T getReceiveMessage(String eventName) {
        LinkedBlockingQueue<AbstractMessage> messageQueue = this.receiveMessageMap.get(eventName);
        if (messageQueue == null || messageQueue.isEmpty()) {
            return null;
        }
        AbstractMessage message = messageQueue.poll();
        return (T) message;
    }

    @Override
    public <T extends AbstractMessage> List<T> getReceiveMessageList(String eventName) {
        LinkedBlockingQueue<AbstractMessage> messageQueue = this.receiveMessageMap.get(eventName);
        if (messageQueue == null || messageQueue.isEmpty()) {
            return Collections.emptyList();
        }
        List<AbstractMessage> list = new LinkedList<>();
        messageQueue.drainTo(list);
        return (List<T>) list;
    }

    /**
     * 处理业务无法处理的异常
     *
     * @Author t13max
     * @Date 10:52 2024/8/6
     */
    protected <Req extends AbstractMessage, Resp extends AbstractMessage> void onException(Throwable throwable, int msgId, Req messageLite) {

    }
}
