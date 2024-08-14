package com.t13max.bot.robot;

import com.t13max.bot.business.SmartBusiness;
import com.t13max.bot.consts.BotStatusEnum;
import com.t13max.bot.state.EmptyState;
import com.t13max.bot.state.StateManager;
import lombok.extern.log4j.Log4j2;

/**
 * 智能机器人 自动达成目标
 *
 * @author: t13max
 * @since: 15:07 2024/8/8
 */
@Log4j2
public class SmartRobot extends AbstractRobot {

    public SmartRobot(RobotAccount robotAccount, String gatewayUri, int serverId) {
        super(robotAccount, gatewayUri, serverId);
        this.nowBusiness = new SmartBusiness();
    }

    @Override
    protected void doTick() {
        switch (botStatus) {
            case INIT -> {
                //暂时没有其他操作 直接执行登录
                updateBotStatus(BotStatusEnum.LOGIN);
            }
            case LOGIN -> {
                this.nowBusiness.getStateMachine().changeState(StateManager.inst().getState(StateManager.LOGIN_STATE));
            }
            case BUSINESS -> {
                tickBusiness();
            }
            case STANDBY -> {

                log.info("STANDBY...");
            }
            default -> {
                log.error("未知状态, 无逻辑执行");
            }
        }

    }

    /**
     * 执行业务tick
     * 这个方法由行为树控制调用 具体怎么执行完全由行为树决定
     *
     * @Author t13max
     * @Date 15:32 2024/8/9
     */
    public void tickBusiness() {
        try {
            //执行业务tick
            nowBusiness.tick();
        } catch (Exception e) {
            //异常处理
            e.printStackTrace();
        }
    }

    @Override
    public void printMonitorInfo() {

    }

    /**
     * 选择业务
     * 不选择 以行为树为准
     *
     * @Author t13max
     * @Date 15:49 2024/8/8
     */
    @Override
    public void choiceBusiness() {
        //标记当前状态为失败
        this.nowBusiness.changeStatus(EmptyState.FAILED_STATE);
    }

}
