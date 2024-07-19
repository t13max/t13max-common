package com.t13max.ai.behavior4j.composites;

import com.t13max.ai.behavior4j.BTNode;

import java.util.List;

/**
 * 随机选择节点
 *
 * @Author t13max
 * @Date 16:37 2024/5/17
 */
public class RandomSelectorNode<E> extends SelectorNode<E> {

    public RandomSelectorNode() {
        super();
    }

    public RandomSelectorNode(List<BTNode<E>> nodes) {
        super(nodes);
    }

    @Override
    public boolean start() {
        if (!super.start()) {
            return false;
        }
        if (randomChildren == null) randomChildren = createRandomChildren();

        return true;
    }
}
