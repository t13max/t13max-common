package com.t13max.ai.behavior4j.composites;

import com.t13max.ai.behavior4j.BTNode;

import java.util.List;

/**
 * if else 节点
 * 0是判断的哪个条件 1是true会执行的 2是false会执行的
 * 最终结果要先判断执行哪个 再看执行的那个的结果
 *
 * @Author t13max
 * @Date 16:24 2024/5/17
 */
public class IfElseNode<E> extends SingleRunningBranchNode<E> {

    public IfElseNode() {
        super();
    }

    public IfElseNode(List<BTNode<E>> nodes) {
        super(nodes);
    }

    @Override
    public void childSuccess(BTNode<E> runningNode) {
        super.childSuccess(runningNode);
        if (currentChildIndex == 0) {
            ++currentChildIndex;
            run(); // Run success condition child
        } else {
            onSuccess(); //  return onSuccess status
        }
    }

    @Override
    public void childFail(BTNode<E> runningNode) {
        super.childFail(runningNode);
        if (currentChildIndex == 0) {
            currentChildIndex = 2;
            run(); // Run failure condition child
        } else {
            onFail(); // return onFail status
        }
    }

    @Override
    void breakRunningChild() {
        super.breakRunningChild();

        onFail();
    }
}
