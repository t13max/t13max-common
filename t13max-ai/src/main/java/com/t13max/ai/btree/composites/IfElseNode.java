package com.t13max.ai.btree.composites;

import com.t13max.ai.btree.BTNode;

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
            //成功了 执行if(true)的逻辑
            ++currentChildIndex;
            run();
        } else {
            onSuccess();
        }
    }

    @Override
    public void childFail(BTNode<E> runningNode) {
        super.childFail(runningNode);
        if (currentChildIndex == 0) {
            //失败了 执行else逻辑
            currentChildIndex = 2;
            run();
        } else {
            onFail();
        }
    }

    @Override
    void breakRunningChild() {
        super.breakRunningChild();

        onFail();
    }
}
