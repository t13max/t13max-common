package com.t13max.ai.behavior4j.decorators;


import com.t13max.ai.behavior4j.BTNode;

/**
 * 必定失败
 *
 * @Author t13max
 * @Date 18:13 2024/5/17
 */
public class AlwaysFailureNode<E> extends Decorator<E> {

    public AlwaysFailureNode() {
    }

    public AlwaysFailureNode(BTNode<E> node) {
        super(node);
    }

    @Override
    public void childSuccess(BTNode<E> runningNode) {
        childFail(runningNode);
    }

}
