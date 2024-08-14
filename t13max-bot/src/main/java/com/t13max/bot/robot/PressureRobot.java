package com.t13max.bot.robot;

import com.t13max.bot.business.PressureBusiness;
import com.t13max.bot.consts.BotStatusEnum;
import com.t13max.bot.interfaces.IBusiness;
import com.t13max.bot.state.StateManager;
import lombok.Getter;
import lombok.Setter;

/**
 * 压测机器人
 * 随机执行各种业务
 *
 * @author: t13max
 * @since: 13:53 2024/4/7
 */
@Getter
@Setter
public class PressureRobot extends AbstractRobot {

    public PressureRobot(RobotAccount robotAccount, String gatewayUri, int serverId) {
        super(robotAccount, gatewayUri, serverId);
        this.nowBusiness = new PressureBusiness();
    }

    protected void doTick() {
        switch (botStatus) {
            case INIT -> //暂时没有其他操作 直接执行登录
                    updateBotStatus(BotStatusEnum.LOGIN);
            case LOGIN -> this.nowBusiness.changeStatus(StateManager.inst().getState(StateManager.LOGIN_STATE));
            case SELECT_BUSINESS -> choiceBusiness();
            case BUSINESS -> doBusiness();
            case STANDBY -> logger.info("STANDBY...");
        }
    }

    @Override
    public void updateBotStatus(BotStatusEnum statusEnum) {
        this.botStatus = statusEnum;
    }

    /**
     * 执行业务
     *
     * @Author t13max
     * @Date 14:30 2024/4/17
     */
    private void doBusiness() {
        this.nowBusiness.tick();
    }

    /**
     * 选择业务
     *
     * @Author t13max
     * @Date 14:30 2024/4/17
     */
    public void choiceBusiness() {
        this.changeBusiness(this.randomBusiness());
    }

    /**
     * 随机选择业务
     *
     * @Author t13max
     * @Date 15:45 2024/8/13
     */
    private IBusiness randomBusiness() {
        //  todo atb 随机选择业务 这里需要修改
        return null;
    }

    @Override
    public void printMonitorInfo() {
        if (this.nowBusiness == null) {
            logger.info("serverId: {}, robot id: {}, business: {}", this.serverId, robotAccount.getUsername(), 0);
            return;
        }
        logger.info("serverId: {},robot id: {}, business: {}", this.serverId, robotAccount.getUsername(), this.nowBusiness.getClass().getSimpleName());
        this.nowBusiness.printMonitorInfo();
    }

    /**
     * 强制改变业务
     *
     * @Author t13max
     * @Date 16:49 2024/4/24
     */
    public void changeBusiness(IBusiness business) {
        if (this.nowBusiness != null) this.nowBusiness.exit();
        this.nowBusiness = business;
        this.nowBusiness.init(this);
        this.updateBotStatus(BotStatusEnum.BUSINESS);
    }

}
