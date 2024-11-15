package com.t13max.ai.btree.decorators;


import com.t13max.ai.btree.BTNode;
import com.t13max.ai.btree.composites.SequenceNode;

import java.util.List;

/**
 * 遍历节点 不论子节点返回成功与否 都会把所有子节点执行一遍最终返回成功
 *
 * @Author t13max
 * @Date 16:34 2024/5/17
 */
public class ErgodicSequenceNode<E> extends SequenceNode<E> {

    public ErgodicSequenceNode() {
        super();
    }

    public ErgodicSequenceNode(List<BTNode<E>> nodes) {
        super(nodes);
    }

    @Override
    public void childFail(BTNode<E> runningNode) {
        //子节点返回失败 也认定为成功 继续执行后面的
        super.childSuccess(runningNode);
    }
}
