package com.t13max.ai.btree.decorators;

import com.t13max.ai.btree.BTNode;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.InvocationTargetException;

/**
 * 权重节点
 *
 * @Author t13max
 * @Date 18:23 2024/5/17
 */
@Setter
@Getter
public class WeightNode<E> extends Decorator<E> {

    private int weight;

    public WeightNode() {
    }

    public WeightNode(int weight) {
        this.weight = weight;
    }

    public WeightNode(BTNode<E> child, int weight) {
        super(child);
        this.weight = weight;
    }

    @Override
    protected void copy(BTNode<E> node) throws InvocationTargetException, IllegalAccessException {
        super.copy(node);
        WeightNode<E> weightNode = (WeightNode<E>) node;
        weightNode.weight = this.weight;
    }
}
