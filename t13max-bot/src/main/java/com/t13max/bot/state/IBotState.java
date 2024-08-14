package com.t13max.bot.state;

import com.t13max.ai.fsm.IState;
import com.t13max.bot.interfaces.IBot;

/**
 * 机器人状态
 *
 * @author: t13max
 * @since: 16:58 2024/8/14
 */
public interface IBotState extends IState<IBot> {

    void onException(IBot bot);
}
