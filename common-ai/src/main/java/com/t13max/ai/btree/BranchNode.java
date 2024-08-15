package com.t13max.ai.btree;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 分支节点基类
 * 分支 或 叶子
 *
 * @Author t13max
 * @Date 16:02 2024/5/17
 */
public abstract class BranchNode<E> extends BTNode<E> {

    //分支节点存在多个子节点
    protected List<BTNode<E>> children;

    public BranchNode() {
        this.children = new ArrayList<>();
    }

    public BranchNode(List<BTNode<E>> children) {
        this.children = children;
    }

    @Override
    protected void copy(BTNode<E> node) throws InvocationTargetException, IllegalAccessException {
        super.copy(node);
        BranchNode<E> branchNode = (BranchNode<E>) node;
        branchNode.children = children.stream().map(BTNode::newInstance).collect(Collectors.toList());
    }

    public int getChildCount() {
        return children.size();
    }

    public BTNode<E> getChild(int i) {
        return children.get(i);
    }

    protected int addChildToNode(BTNode<E> child) {
        children.add(child);
        return children.size() - 1;
    }
}
