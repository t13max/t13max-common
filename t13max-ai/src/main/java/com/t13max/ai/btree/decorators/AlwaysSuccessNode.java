package com.t13max.ai.btree.decorators;


import com.t13max.ai.btree.BTNode;

/**
 * 必定成功
 *
 * @Author t13max
 * @Date 18:13 2024/5/17
 */
public class AlwaysSuccessNode<E> extends Decorator<E> {

    public AlwaysSuccessNode() {
    }

    public AlwaysSuccessNode(BTNode<E> node) {
        super(node);
    }

    @Override
    public void childFail(BTNode<E> runningNode) {
        childSuccess(runningNode);
    }

}
