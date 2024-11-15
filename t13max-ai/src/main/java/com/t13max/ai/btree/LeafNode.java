package com.t13max.ai.btree;

import java.lang.reflect.InvocationTargetException;

/**
 * 叶子节点
 *
 * @Author t13max
 * @Date 16:02 2024/5/17
 */
public abstract class LeafNode<E> extends BTNode<E> {

    public LeafNode() {
    }

    /**
     * 子类实现具体的执行逻辑
     *
     * @Author t13max
     * @Date 14:57 2024/7/19
     */
    public abstract Status execute();

    /**
     * 节点更新逻辑。子类实现 execute（）方法.
     */
    @Override
    protected final void run() {
        Status result = execute();
        if (result == null) throw new IllegalStateException("执行结果为空");
        switch (result) {
            case BT_SUCCESS:
                onSuccess();
                return;
            case BT_FAILURE:
                onFail();
                return;
            case BT_RUNNING:
                onRunning();
                return;
            case BT_CANCEL:
                onInterrupt();
                return;
            default:
                throw new IllegalStateException("执行结果未知, result=" + result.name());
        }
    }

    @Override
    protected int addChildToNode(BTNode<E> child) {
        throw new IllegalStateException("叶子节点不能有子节点");
    }

    @Override
    public int getChildCount() {
        return 0;
    }

    @Override
    public BTNode<E> getChild(int i) {
        throw new IndexOutOfBoundsException("叶子节点没有子节点");
    }

    @Override
    public final void childRunning(BTNode<E> runningNode, BTNode<E> reporter) {
    }

    @Override
    public final void childFail(BTNode<E> runningNode) {
    }

    @Override
    public final void childSuccess(BTNode<E> runningNode) {
    }

    @Override
    protected void copy(BTNode<E> node) throws InvocationTargetException, IllegalAccessException {
        super.copy(node);
    }
}
