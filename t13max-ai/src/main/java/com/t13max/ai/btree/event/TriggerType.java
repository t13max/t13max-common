package com.t13max.ai.btree.event;

/**
 * 触发类型
 *
 * @Author t13max
 * @Date 13:47 2024/5/23
 */
public enum TriggerType {
    //状态变化触发
    TRANSLATE_NODE(1),
    //结束时触发
    RETURN_NODE(2),
    //事件触发
    EVENT_NODE(3);

    TriggerType(int type) {
        this.type = type;
    }

    public final int type;
}
