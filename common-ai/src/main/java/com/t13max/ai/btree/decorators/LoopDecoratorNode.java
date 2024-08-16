package com.t13max.ai.btree.decorators;


import com.t13max.ai.btree.BTNode;

import java.lang.reflect.InvocationTargetException;

/**
 * 循环节点
 *
 * @Author t13max
 * @Date 18:18 2024/5/17
 */
public abstract class LoopDecoratorNode<E> extends Decorator<E> {

    protected boolean needRun;

    protected boolean frame;

    public LoopDecoratorNode() {
    }

    public LoopDecoratorNode(boolean frame) {
        this.frame = frame;
    }

    public LoopDecoratorNode(BTNode<E> child, boolean frame) {
        super(child);
        this.frame = frame;
    }

    public LoopDecoratorNode(BTNode<E> child) {
        super(child);
    }

    public boolean condition() {
        return needRun;
    }

    @Override
    protected void run() {
        needRun = true;
        if (frame) {
            while (condition()) {
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
            onSuccess();
        } else {
            //这里是不是死循环了... 有空看看
            if (condition()) {
                if (child.getStatus() == Status.BT_RUNNING) {
                    child.runWithGuard();
                } else {
                    child.setParent(this);
                    if (!child.start())
                        child.onFail();
                    else
                        child.runWithOutGuard();
                }
                onRunning();
            } else {
                onSuccess();
            }
        }

    }

    @Override
    public void childRunning(BTNode<E> runningNode, BTNode<E> reporter) {
        super.childRunning(runningNode, reporter);
        needRun = false;
    }

    @Override
    protected void copy(BTNode<E> node) throws InvocationTargetException, IllegalAccessException {
        super.copy(node);

        LoopDecoratorNode<E> loopDecoratorNode = (LoopDecoratorNode<E>) node;
        loopDecoratorNode.frame = this.frame;
        loopDecoratorNode.needRun = this.needRun;
    }
}
