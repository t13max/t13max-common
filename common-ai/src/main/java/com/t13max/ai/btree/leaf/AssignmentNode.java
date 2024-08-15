package com.t13max.ai.btree.leaf;

/**
 * @Author t13max
 * @Date 13:48 2024/5/23
 */
public abstract class AssignmentNode<E> extends ActionNode<E> {

    @Override
    public Status execute() {
        assignment();

        return Status.BT_SUCCESS;
    }

    protected abstract void assignment();

}
