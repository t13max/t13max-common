package com.t13max.ai.behavior4j.composites;

import com.t13max.ai.behavior4j.BTNode;

import java.util.List;

/**
 * 与 节点
 *
 * @Author t13max
 * @Date 16:13 2024/5/17
 */
public class AndNode<E> extends ConditionOperatorNode<E> {

    public AndNode() {
        super();
    }

    public AndNode(List<BTNode<E>> nodes) {
        super(nodes);
    }

    @Override
    public void childSuccess(BTNode<E> runningNode) {
        super.childSuccess(runningNode);
        if (++currentChildIndex < children.size()) {
            run(); //继续判断下一个
        } else {
            //所有成功 才成功
            onSuccess();
        }
    }

    @Override
    public void childFail(BTNode<E> runningNode) {
        super.childFail(runningNode);
        //一个失败就失败
        onFail();
    }
}
