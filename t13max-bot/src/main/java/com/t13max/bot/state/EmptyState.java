package com.t13max.bot.state;


import com.t13max.ai.fsm.IState;
import com.t13max.ai.fsm.IStateEvent;
import com.t13max.bot.interfaces.IBot;

/**
 * 空状态 啥也不干
 *
 * @author: t13max
 * @since: 15:34 2024/8/8
 */
public abstract class EmptyState implements IBotState {

    //空状态 啥也不干
    public static final EmptyState EMPTY_STATE = new EmptyState() {
        @Override
        public boolean match(IBot bot) {
            return bot.getNowBusiness().getCurState() == EMPTY_STATE;
        }
    };

    //成功状态 也是空的 但是表示上一个状态圆满完成
    public static final EmptyState SUCCESS_STATE = new EmptyState() {
        @Override
        public boolean match(IBot bot) {
            return bot.getNowBusiness().getCurState() == SUCCESS_STATE;
        }
    };

    //失败状态 也是空的 但是表示上一个状态没能顺利完成
    public static final EmptyState FAILED_STATE = new EmptyState() {
        @Override
        public boolean match(IBot bot) {
            return bot.getNowBusiness().getCurState() == FAILED_STATE;
        }
    };

    @Override
    public void enter(IBot entity) {

    }

    @Override
    public void update(IBot entity) {

    }

    @Override
    public void exit(IBot entity) {

    }

    @Override
    public boolean onEvent(IBot entity, IStateEvent event) {
        return false;
    }

    @Override
    public void onException(IBot bot) {

    }

    @Override
    public boolean stateSwitch(IState<IBot> state) {
        return IBotState.super.stateSwitch(state);
    }

    /**
     * 判断指定机器人是否为指定状态
     *
     * @Author t13max
     * @Date 15:52 2024/8/9
     */
    public abstract boolean match(IBot bot);
}
