package com.t13max.bot.interfaces;


import com.t13max.ai.fsm.StateMachine;
import com.t13max.bot.consts.IStatusEnum;
import com.t13max.bot.state.IBotState;

/**
 * 业务接口
 *
 * @author: t13max
 * @since: 15:02 2024/4/7
 */
public interface IBusiness {

    //模块初始化
    void init(IBot bot);

    //模块运行
    void tick();

    boolean checkTimeout(IBotState state, int count);

    //获取状态机
    StateMachine<IBot, IBotState> getStateMachine();

    //获取当前状态
    IBotState getCurState();

    //更新状态
    void changeStatus(IStatusEnum statusEnum);

    //切换状态
    void changeStatus(IBotState state);

    //主动退出
    void exit();

    //选择小业务
    void choiceSmallBusiness();

    //打印当前业务的信息
    void printMonitorInfo();
}
