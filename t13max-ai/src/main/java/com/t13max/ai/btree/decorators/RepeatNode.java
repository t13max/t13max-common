package com.t13max.ai.btree.decorators;


import com.t13max.ai.btree.BTNode;

import java.lang.reflect.InvocationTargetException;

/**
 * 循环执行指定次数
 *
 * @Author t13max
 * @Date 18:18 2024/5/17
 */
public class RepeatNode<E> extends LoopDecoratorNode<E> {

    //已经循环的次数
    private int count;
    //需要循环的次数
    private int times;

    public RepeatNode(int times, boolean frame) {
        super(frame);
        this.times = times;
        //需要在一帧内完成多次执行 则次数必须大于0
        if (times < 0 && frame)
            throw new IllegalArgumentException("需要循环执行则次数必须大于零");
    }

    public RepeatNode(BTNode<E> child, int times, boolean frame) {
        super(child, frame);
        this.times = times;
    }

    @Override
    public boolean start() {
        if (!super.start()) {
            return false;
        }
        count = times;
        return true;
    }

    @Override
    public boolean condition() {
        //需要继续执行切次数大于零
        return needRun && count != 0;
    }

    @Override
    public void childSuccess(BTNode<E> runningNode) {
        if (count > 0)
            count--;
        if (count == 0) {
            //执行够次数了
            super.childSuccess(runningNode);
            needRun = false;
        } else
            //小于0则是无限循环
            needRun = true;
    }

    @Override
    public void childFail(BTNode<E> runningNode) {
        childSuccess(runningNode);
    }

    @Override
    protected void copy(BTNode<E> node) throws InvocationTargetException, IllegalAccessException {
        super.copy(node);
        RepeatNode<E> repeatNode = (RepeatNode<E>) node;
        repeatNode.times = this.times;
    }
}
