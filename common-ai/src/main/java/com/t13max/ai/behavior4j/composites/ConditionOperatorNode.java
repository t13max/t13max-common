package com.t13max.ai.behavior4j.composites;

import com.t13max.ai.behavior4j.BTNode;
import com.t13max.ai.behavior4j.leaf.ConditionNode;

import java.util.List;

/**
 * 条件节点
 *
 * @Author t13max
 * @Date 16:14 2024/5/17
 */
public class ConditionOperatorNode<E> extends SingleRunningBranchNode<E> {

    public ConditionOperatorNode() {
        super();
    }

    public ConditionOperatorNode(List<BTNode<E>> nodes) {
        super(nodes);
    }

    @Override
    protected int addChildToNode(BTNode<E> child) {
        if (!((child instanceof ConditionNode) || (child instanceof ConditionOperatorNode)))
            throw new IllegalArgumentException("入参错误, ConditionOperatorNode节点只接受ConditionNode或ConditionOperatorNode");
        return super.addChildToNode(child);
    }
}
