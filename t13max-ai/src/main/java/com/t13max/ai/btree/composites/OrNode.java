package com.t13max.ai.btree.composites;

import com.t13max.ai.btree.BTNode;

import java.util.List;

/**
 * 或 节点
 *
 * @Author t13max
 * @Date 16:17 2024/5/17
 */
public class OrNode<E> extends SingleRunningBranchNode<E> {

    public OrNode() {
        super();
    }

    public OrNode(List<BTNode<E>> nodes) {
        super(nodes);
    }

    @Override
    public void childFail(BTNode<E> runningNode) {
        super.childFail(runningNode);
        if (++currentChildIndex < children.size()) {
            //继续判断下一个
            run();
        } else {
            //全部失败就失败
            onFail();
        }
    }

    @Override
    public void childSuccess(BTNode<E> runningNode) {
        super.childSuccess(runningNode);
        //一个成功就成功
        onSuccess();
    }
}
