package com.t13max.ai.btree.composites;

import com.t13max.ai.btree.BTNode;
import com.t13max.ai.btree.BranchNode;
import com.t13max.ai.btree.condition.BaseCondition;
import com.t13max.ai.utils.MathUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * 单执行分支节点, 一次只执行一个子节点
 *
 * @Author t13max
 * @Date 15:59 2024/5/17
 */
public abstract class SingleRunningBranchNode<E> extends BranchNode<E> {

    //当前正在执行的子节点
    protected BTNode<E> runningChild;

    // 当前执行子节点 子类实现赋值此字段
    protected int currentChildIndex;

    //随机执行列表
    protected List<BTNode<E>> randomChildren;

    //条件 用于判断break
    protected BTNode<E> condition;

    public SingleRunningBranchNode() {
        super();
    }

    public SingleRunningBranchNode(List<BTNode<E>> nodes) {
        super(nodes);
    }

    @Override
    public void childRunning(BTNode<E> node, BTNode<E> reporter) {
        runningChild = node;
        onRunning();
    }

    @Override
    public void childSuccess(BTNode<E> node) {
        this.runningChild = null;
    }

    @Override
    public void childFail(BTNode<E> node) {
        this.runningChild = null;
    }

    @Override
    protected void run() {
        //不为空
        if (runningChild != null) {
            if (checkBreak()) {
                breakRunningChild();
            } else {
                if (runningChild.getStatus() == Status.BT_RUNNING) {
                    runningChild.runWithGuard();
                } else {
                    runningChild.setParent(this);
                    if (!runningChild.start())
                        runningChild.onFail();
                    else
                        runningChild.runWithOutGuard();
                }
            }
        } else {
            //否则选择节点进行执行
            if (currentChildIndex < children.size()) {
                //随机选择 这里的逻辑是给随机选择用的
                if (randomChildren != null) {
                    int last = children.size() - 1;
                    if (currentChildIndex < last) {
                        // 随机交换一个
                        int otherChildIndex = MathUtils.random(currentChildIndex, last);
                        BTNode<E> tmp = randomChildren.get(currentChildIndex);
                        randomChildren.set(currentChildIndex, randomChildren.get(otherChildIndex));
                        randomChildren.set(otherChildIndex, tmp);
                    }
                    runningChild = randomChildren.get(currentChildIndex);
                } else {
                    //选择指定节点
                    runningChild = children.get(currentChildIndex);
                }

                run();
            }
        }
    }

    @Override
    public boolean start() {
        if (!super.start()) {
            return false;
        }

        this.currentChildIndex = 0;
        runningChild = null;

        return true;
    }

    @Override
    protected void cancelRunningChildren(int startIndex) {
        this.runningChild = null;
        super.cancelRunningChildren(startIndex);
    }

    @Override
    protected void copy(BTNode<E> node) throws InvocationTargetException, IllegalAccessException {
        super.copy(node);
        SingleRunningBranchNode<E> singleRunningBranchNode = (SingleRunningBranchNode<E>) node;
        if (this.condition != null)
            singleRunningBranchNode.setCondition(this.condition.newInstance());
    }

    public void setCondition(BTNode<E> condition) {
        if (!(condition instanceof BaseCondition))
            throw new IllegalArgumentException("入参错误, 非BaseCondition条件, class=" + condition.getClass().getSimpleName());
        this.condition = condition;
    }

    List<BTNode<E>> createRandomChildren() {
        return new ArrayList<>(children);
    }

    void breakRunningChild() {
        cancelRunningChildren(currentChildIndex);
    }

    private boolean checkBreak() {
        if (runningChild != null && runningChild.getStatus() == Status.BT_RUNNING) {
            if (condition != null) {
                if (condition.getTree() == null)
                    condition.bindTree(tree);
                return ((BaseCondition) condition).condition();
            }
        }
        return false;
    }
}
