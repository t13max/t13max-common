package com.t13max.ai.btree.leaf;


import com.t13max.ai.btree.LeafNode;

/**
 * noop节点 啥也不干 直接返回成功
 * @Author t13max
 * @Date 14:56 2024/7/19
 */
public class NoopNode<E> extends LeafNode<E> {

    public Status execute() {
        return Status.BT_SUCCESS;
    }
}
