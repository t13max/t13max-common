package com.t13max.ai.behavior4j.attachments;

import com.t13max.ai.behavior4j.BTNode;
import com.t13max.ai.behavior4j.BehaviorTree;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.InvocationTargetException;

/**
 * 附件节点
 *
 * @Author t13max
 * @Date 18:34 2024/5/17
 */
@Setter
public abstract class AttachmentNode<E> extends BTNode<E> {

    public enum Operator {
        And,
        Or,
    }

    public static final int PHASE_NONE = 0x0;
    public static final int PHASE_START = 0x1;
    public static final int PHASE_UPDATE = 0x10;
    public static final int PHASE_PRECONDITION = 0x11;
    public static final int PHASE_SUCCESS = 0x100;
    public static final int PHASE_FAIL = 0x1000;
    public static final int PHASE_POST_EFFECT = 0x1100;

    //当前阶段
    private int phase = PHASE_NONE;

    private BTNode<E> node;

    @Getter
    private Operator operator;

    @Override
    protected void copy(BTNode<E> node) throws InvocationTargetException, IllegalAccessException {
        super.copy(node);
        AttachmentNode<E> attachmentNode = (AttachmentNode<E>) node;
        attachmentNode.phase = this.phase;
        attachmentNode.operator = this.operator;
    }

    @Override
    protected final void run() {
    }

    @Override
    protected int addChildToNode(BTNode<E> child) {
        throw new IllegalStateException("附件节点不能添加子节点");
    }

    @Override
    public int getChildCount() {
        return 0;
    }

    @Override
    public BTNode<E> getChild(int i) {
        throw new IndexOutOfBoundsException("附件节点没有子节点");
    }

    @Override
    public final void childRunning(BTNode<E> runningNode, BTNode<E> reporter) {
    }

    @Override
    public final void childFail(BTNode<E> runningNode) {
    }

    @Override
    public final void childSuccess(BTNode<E> runningNode) {
    }

    @Override
    public E getOwner() {
        return node.getOwner();
    }

    @Override
    protected Object getParam(String name) {
        BehaviorTree<E> tree = node == null ? null : node.getTree();

        return tree == null ? null : tree.getParams(name);
    }

    public boolean checkPhase(int phase) {
        return (this.phase & phase) > 0;
    }

    /**
     * 前置条件
     *
     * @return boolean
     */
    public abstract boolean preCondition();

    /*** 后置效果*/
    public abstract void effect();

}
