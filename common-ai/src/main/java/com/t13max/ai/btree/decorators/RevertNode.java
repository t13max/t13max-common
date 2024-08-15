package com.t13max.ai.btree.decorators;


import com.t13max.ai.btree.BTNode;

/**
 * 反转节点 子节点的返回值取反
 *
 * @Author t13max
 * @Date 18:24 2024/5/17
 */
public class RevertNode<E> extends Decorator<E> {

    public RevertNode() {
    }

    public RevertNode(BTNode<E> node) {
        super(node);
    }

    @Override
    public void childSuccess(BTNode<E> runningNode) {
        super.childFail(runningNode);
    }

    @Override
    public void childFail(BTNode<E> runningNode) {
        super.childSuccess(runningNode);
    }
}
