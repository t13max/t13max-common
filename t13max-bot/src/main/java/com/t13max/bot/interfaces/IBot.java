package com.t13max.bot.interfaces;

import com.google.protobuf.AbstractMessage;
import com.t13max.bot.consts.BotStatusEnum;
import com.t13max.bot.robot.RobotAccount;
import com.t13max.common.msg.MessagePack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.function.Consumer;

/**
 * 机器人几口
 * 未来可实现多种机器人
 *
 * @Author t13max
 * @Date 16:42 2024/8/14
 */
public interface IBot {

    Logger logger = LogManager.getLogger(IBot.class);

    long getId();

    String getGatewayUri();

    int getServerId();

    void tick();

    void close();

    void choiceBusiness();

    IBusiness getNowBusiness();

    //异步发送消息 Consumer回调版
    <Req extends MessagePack<?>, Resp extends MessagePack<?>> void sendMsgAsync(int msgId, Req messageLite, Consumer<Resp> consumer);

    //异步发送 适配新版状态机
    <Req extends MessagePack<?>, Resp extends MessagePack<?>> void sendMsgAsync(int msgId, Req messageLite);

    <T extends AbstractMessage> T getReceiveMessage(String eventName);

    <T extends AbstractMessage> List<T> getReceiveMessageList(String eventName);

    //打印机器人信息
    void printMonitorInfo();

    RobotAccount getRobotAccount();


    boolean isOnline();

    void setOnline(boolean online);

    void updateBotStatus(BotStatusEnum statusEnum);
}
