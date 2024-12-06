package com.t13max.ai.btree.composites;

import com.t13max.ai.btree.BTNode;

/**
 * 任务节点
 *
 * @Author t13max
 * @Date 16:39 2024/5/17
 */
public class TaskNode<E> extends SingleRunningBranchNode<E> {

    @Override
    public void childSuccess(BTNode<E> node) {
        super.childSuccess(node);
        onSuccess();
    }

    @Override
    public void childFail(BTNode<E> node) {
        super.childFail(node);
        onFail();
    }
}
