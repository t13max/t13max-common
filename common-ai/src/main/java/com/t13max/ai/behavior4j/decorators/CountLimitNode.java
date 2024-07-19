package com.t13max.ai.behavior4j.decorators;


import com.t13max.ai.behavior4j.BTNode;
import com.t13max.ai.behavior4j.condition.BaseCondition;

import java.lang.reflect.InvocationTargetException;

/**
 * 次数限制
 *
 * @Author t13max
 * @Date 18:14 2024/5/17
 */
public class CountLimitNode<E> extends Decorator<E> {
    private int countLimit;
    private int currentCount;

    private BTNode<E> condition;

    public CountLimitNode() {
    }

    public CountLimitNode(int countLimit) {
        this.countLimit = countLimit;
    }

    public CountLimitNode(BTNode<E> child, int countLimit) {
        super(child);
        this.countLimit = countLimit;
    }

    @Override
    public boolean start() {
        if (condition != null && condition.getTree() == null)
            condition.bindTree(tree);
        if (condition != null && ((BaseCondition) condition).condition()) {
            currentCount = 0;
        }

        return super.start();
    }

    @Override
    protected void run() {
        if (++currentCount > countLimit && countLimit > 0) {
            onSuccess();
            return;
        }

        super.run();
    }

    @Override
    protected void copy(BTNode<E> node) throws InvocationTargetException, IllegalAccessException {
        super.copy(node);
        CountLimitNode<E> countLimitNode = (CountLimitNode<E>) node;
        countLimitNode.countLimit = this.countLimit;
        countLimitNode.condition = this.condition.newInstance();
    }

    public void setCondition(BTNode<E> condition) {
        if (!(condition instanceof BaseCondition))
            throw new IllegalArgumentException("The condition class type can't support in CountLimitNode!");

        this.condition = condition;
    }
}
