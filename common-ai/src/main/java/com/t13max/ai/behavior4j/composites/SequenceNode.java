package com.t13max.ai.behavior4j.composites;

import com.t13max.ai.behavior4j.BTNode;

import java.util.List;

/**
 * 顺序节点 子节点依次执行
 * 顺序依次执行其子节点，直到所有子节点成功返回，该节点也返回成功。只要其中某个子节点失败，那么该节点也失败。
 *
 * @Author t13max
 * @Date 16:35 2024/5/17
 */
public class SequenceNode<E> extends SingleRunningBranchNode<E> {

    public SequenceNode() {
        super();
    }

    public SequenceNode(List<BTNode<E>> nodes) {
        super(nodes);
    }

    @Override
    public void childSuccess(BTNode<E> runningNode) {
        super.childSuccess(runningNode);
        if (++currentChildIndex < children.size()) {
            //继续执行后续节点
            run();
        } else {
            // 所有子节点执行成功 则返回成功
            onSuccess();
        }
    }

    @Override
    public void childFail(BTNode<E> runningNode) {
        super.childFail(runningNode);
        // 当有子节点失败则直接返回失败
        onFail();
    }

    @Override
    void breakRunningChild() {
        super.breakRunningChild();
        if (++currentChildIndex < children.size()) {
            //break当前子节点 则继续后续节点
            run();
        } else {
            onFail();
        }
    }
}
