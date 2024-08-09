package com.t13max.ai.behavior4j.decorators;


import com.t13max.ai.behavior4j.BTNode;
import com.t13max.ai.behavior4j.utils.BehaviorTreeManager;

import java.lang.reflect.InvocationTargetException;

/**
 * 引用子树节点
 *
 * @Author t13max
 * @Date 13:47 2024/5/23
 */
public class ReferenceNode<E> extends Decorator<E> {

    private String subtree;

    public ReferenceNode() {
    }

    public ReferenceNode(String subtree) {
        this.subtree = subtree;
        addChild(createSubtreeRootNode());
    }

    @Override
    protected void copy(BTNode<E> node) throws InvocationTargetException, IllegalAccessException {
        super.copy(node);
        ReferenceNode<E> referenceNode = (ReferenceNode<E>) node;
        referenceNode.subtree = this.subtree;
    }

    private BTNode<E> createSubtreeRootNode() {

        return BehaviorTreeManager.getInstance().createBehaviorTree(subtree);
    }

}
