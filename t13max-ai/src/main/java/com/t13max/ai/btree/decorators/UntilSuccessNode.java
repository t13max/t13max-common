package com.t13max.ai.btree.decorators;


import com.t13max.ai.btree.BTNode;

/**
 * 装饰器将重复执行包装的子节点，直到该子节点返回成功
 *
 * @Author t13max
 * @Date 18:23 2024/5/17
 */
public class UntilSuccessNode<E> extends LoopDecoratorNode<E> {

    public UntilSuccessNode() {
    }

    public UntilSuccessNode(BTNode<E> node) {
        super(node);
    }

    @Override
    public void childSuccess(BTNode<E> runningNode) {
        onSuccess();
        needRun = false;
    }

    @Override
    public void childFail(BTNode<E> runningNode) {
        needRun = true;
    }
}
