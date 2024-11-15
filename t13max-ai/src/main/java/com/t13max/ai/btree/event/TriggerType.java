package com.t13max.ai.btree.event;

/**
 * 触发类型
 *
 * @Author t13max
 * @Date 13:47 2024/5/23
 */
public enum TriggerType {
    translateNode(1),
    returnNode(2),
    eventNode(3);

    TriggerType(int type) {
        this.type = type;
    }

    public int type;
}
