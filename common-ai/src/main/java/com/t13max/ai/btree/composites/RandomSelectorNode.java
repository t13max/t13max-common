package com.t13max.ai.btree.composites;

import com.t13max.ai.btree.BTNode;

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
