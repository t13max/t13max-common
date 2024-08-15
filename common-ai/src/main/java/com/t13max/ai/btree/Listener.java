package com.t13max.ai.btree;

/**
 * @Author t13max
 * @Date 13:49 2024/5/23
 */
public interface Listener<E> {

    void statusUpdated(BTNode<E> node, BTNode.Status previousStatus);

    void childAdded(BTNode<E> node, int index);
}
