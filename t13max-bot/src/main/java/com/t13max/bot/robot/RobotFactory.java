package com.t13max.bot.robot;

import com.t13max.bot.interfaces.IBot;
import lombok.extern.log4j.Log4j2;

/**
 * 机器人工厂
 *
 * @author: t13max
 * @since: 14:32 2024/4/7
 */
@Log4j2
public class RobotFactory {

    /**
     * 创建压测用机器人
     *
     * @Author t13max
     * @Date 15:55 2024/8/8
     */
    public IBot createPressureBot(String gatewayUri, int serverId, RobotAccount robotAccount) {
        return new PressureRobot(robotAccount, gatewayUri, serverId);
    }

    /**
     * 创建智能机器人
     *
     * @Author t13max
     * @Date 15:55 2024/8/8
     */
    public IBot createSmartBot(String gatewayUri, int serverId, RobotAccount robotAccount) {
        return new SmartRobot(robotAccount, gatewayUri, serverId);
    }


}
