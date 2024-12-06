package com.t13max.ai.btree.decorators;

import com.t13max.ai.btree.BTNode;

import java.lang.reflect.InvocationTargetException;

/**
 * 装饰器 有且只有一个子节点
 *
 * @Author t13max
 * @Date 18:03 2024/5/17
 */
public abstract class Decorator<E> extends BTNode<E> {

    protected BTNode<E> child;

    public Decorator() {
    }

    public Decorator(BTNode<E> child) {
        this.child = child;
    }

    @Override
    protected int addChildToNode(BTNode<E> child) {
        if (this.child != null)
            throw new IllegalStateException("装饰器不能添加多个子节点");
        this.child = child;
        return 0;
    }

    @Override
    public int getChildCount() {
        return child == null ? 0 : 1;
    }

    @Override
    public BTNode<E> getChild(int i) {
        if (i == 0 && child != null)
            return child;
        throw new IndexOutOfBoundsException("索引非法 " + i + " >= " + getChildCount());
    }

    @Override
    protected void run() {
        if (child.getStatus() == Status.BT_RUNNING) {
            child.runWithGuard();
        } else {
            child.setParent(this);
            if (!child.start())
                child.onFail();
            else
                child.runWithOutGuard();
        }
    }

    @Override
    public void childRunning(BTNode<E> runningNode, BTNode<E> reporter) {
        onRunning();
    }

    @Override
    public void childFail(BTNode<E> runningNode) {
        onFail();
    }

    @Override
    public void childSuccess(BTNode<E> runningNode) {
        onSuccess();
    }

    @Override
    protected void copy(BTNode<E> node) throws InvocationTargetException, IllegalAccessException {
        super.copy(node);
        Decorator<E> decorator = (Decorator<E>) node;
        decorator.child = child.newInstance();
    }
}
