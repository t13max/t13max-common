package com.t13max.ai.btree.composites;

import com.t13max.ai.btree.BTNode;

import java.util.List;

/**
 * 选择节点
 *
 * @Author t13max
 * @Date 16:31 2024/5/17
 */
public class SelectorNode<E> extends SingleRunningBranchNode<E> {

    public SelectorNode() {
        super();
    }

    public SelectorNode(List<BTNode<E>> nodes) {
        super(nodes);
    }

    /**
     * 选择节点默认实现
     * 失败则顺序执行后续的子节点
     *
     * @Author t13max
     * @Date 16:32 2024/5/17
     */
    @Override
    public void childFail(BTNode<E> runningNode) {
        super.childFail(runningNode);
        if (++currentChildIndex < children.size()) {
            run(); // run next child
        } else {
            onFail();
        }
    }

    @Override
    public void childSuccess(BTNode<E> runningNode) {
        super.childSuccess(runningNode);
        onSuccess();
    }

    /**
     * 子节点break
     * 条件节点默认实现 失败则继续执行后续子节点
     *
     * @Author t13max
     * @Date 16:33 2024/5/17
     */
    @Override
    void breakRunningChild() {
        super.breakRunningChild();
        if (++currentChildIndex < children.size()) {
            run(); // run next child
        } else {
            onFail();
        }
    }
}
