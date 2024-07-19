package com.t13max.ai.behavior4j.decorators;


import com.t13max.ai.behavior4j.BTNode;

import java.lang.reflect.InvocationTargetException;

/**
 * 循环直到N次
 *
 * @Author t13max
 * @Date 13:46 2024/5/23
 */
public class LoopUntilNode<E> extends Decorator<E> {
    private int count;
    private int currentCount;

    public LoopUntilNode() {
    }

    public LoopUntilNode(int count) {
        this.count = count;
    }

    public LoopUntilNode(BTNode<E> child, int count) {
        super(child);
        this.count = count;
    }

    @Override
    protected void run() {
        currentCount++;
        super.run();
    }

    @Override
    public void childRunning(BTNode<E> runningNode, BTNode<E> reporter) {
        if (currentCount < count)
            onRunning();
        else
            onSuccess();
    }

    @Override
    public void childFail(BTNode<E> runningNode) {
        if (currentCount < count)
            onRunning();
        else
            onSuccess();
    }

    @Override
    public void childSuccess(BTNode<E> runningNode) {
        if (currentCount < count)
            onRunning();
        else
            onSuccess();
    }

    @Override
    protected void copy(BTNode<E> node) throws InvocationTargetException, IllegalAccessException {
        super.copy(node);
        LoopUntilNode<E> loopUntilNode = (LoopUntilNode<E>) node;
        loopUntilNode.count = this.count;
    }
}
