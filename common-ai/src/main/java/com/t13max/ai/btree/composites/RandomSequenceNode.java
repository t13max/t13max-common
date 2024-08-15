package com.t13max.ai.btree.composites;

import com.t13max.ai.btree.BTNode;

import java.util.List;

/**
 * 随机序列节点
 * 随机序列节点每次执行子节点时随机的决定其执行顺序
 *
 * @Author t13max
 * @Date 16:34 2024/5/17
 */
public class RandomSequenceNode<E> extends SequenceNode<E> {

    public RandomSequenceNode() {
        super();
    }

    public RandomSequenceNode(List<BTNode<E>> nodes) {
        super(nodes);
    }

    @Override
    public boolean start() {
        if (!super.start()) {
            return false;
        }

        //节点进入时 赋值randomChildren 用于随机选择执行
        if (randomChildren == null) randomChildren = createRandomChildren();

        return true;
    }
}
