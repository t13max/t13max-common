package com.t13max.ai.behavior4j.leaf;


import com.t13max.ai.behavior4j.LeafNode;

/**
 * 失败
 *
 * @Author t13max
 * @Date 13:48 2024/5/23
 */
public class FailureNode<E> extends LeafNode<E> {

    public FailureNode() {
    }

    public Status execute() {
        return Status.BT_FAILURE;
    }
}
