package com.t13max.ai.behavior4j.leaf;


import com.t13max.ai.behavior4j.LeafNode;
import com.t13max.ai.behavior4j.condition.BaseCondition;

/**
 * 条件节点
 *
 * @Author t13max
 * @Date 16:05 2024/5/17
 */
public abstract class ConditionNode<E> extends LeafNode<E> implements BaseCondition {

    @Override
    public Status execute() {

        return condition() ? Status.BT_SUCCESS : Status.BT_FAILURE;
    }

    public abstract boolean condition();
}
