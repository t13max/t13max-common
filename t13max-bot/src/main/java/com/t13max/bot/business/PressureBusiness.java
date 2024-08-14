package com.t13max.bot.business;


import com.t13max.ai.fsm.DefaultStateMachine;
import com.t13max.bot.state.EmptyState;

/**
 * 压测业务
 *
 * @author: t13max
 * @since: 14:24 2024/8/13
 */
public class PressureBusiness extends AbstractBusiness {

    public PressureBusiness() {
        this.stateMachine = new DefaultStateMachine<>(bot, EmptyState.EMPTY_STATE);
    }

    /**
     * 压测tick
     * 调用状态机的tick 具体什么状态由tick内的逻辑和choiceSmallBusiness决定
     *
     * @Author t13max
     * @Date 16:08 2024/8/9
     */
    @Override
    protected void doTick() {
        this.stateMachine.update();
    }

    @Override
    public void exit() {
        onExit();
        changeStatus(EmptyState.EMPTY_STATE);
    }

}
