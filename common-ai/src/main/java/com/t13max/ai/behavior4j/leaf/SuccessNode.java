package com.t13max.ai.behavior4j.leaf;


import com.t13max.ai.behavior4j.LeafNode;

/**
 * 成功
 *
 * @Author t13max
 * @Date 13:48 2024/5/23
 */
public class SuccessNode<E> extends LeafNode<E> {

    public SuccessNode() {
    }

    public Status execute() {
        return Status.BT_SUCCESS;
    }
}
