package com.t13max.bot.consts;

import com.t13max.bot.state.EmptyState;
import com.t13max.bot.state.IBotState;

/**
 * 业务状态接口
 * 可设置权重 不支持动态配置
 *
 * @author: t13max
 * @since: 13:17 2024/4/19
 */
public interface IStatusEnum {

    int getWeight();

    IStatusEnum[] getValues();

    default IBotState newState() {
        return EmptyState.EMPTY_STATE;
    }
}
