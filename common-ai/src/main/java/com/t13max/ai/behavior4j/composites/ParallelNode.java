package com.t13max.ai.behavior4j.composites;

import com.t13max.ai.behavior4j.BTNode;
import com.t13max.ai.behavior4j.BranchNode;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * 并行节点
 *
 * @Author t13max
 * @Date 18:33 2024/5/17
 */
public class ParallelNode<E> extends BranchNode<E> {

    /*** 节点策略*/
    private Policy policy;
    /*** 协调器*/
    private Coordinator coordinator;

    private boolean hasRunningChild;
    private Boolean result;
    private int currentChildIndex;

    public ParallelNode() {
    }

    public ParallelNode(Policy policy, Coordinator coordinator) {
        this.policy = policy;
        this.coordinator = coordinator;
    }

    public ParallelNode(Policy policy, Coordinator coordinator, List<BTNode<E>> children) {
        super(children);
        this.policy = policy;
        this.coordinator = coordinator;
    }

    @Override
    public void run() {
        coordinator.execute(this);
    }

    @Override
    public void childRunning(BTNode<E> task, BTNode<E> reporter) {
        hasRunningChild = true;
    }

    @Override
    public void childSuccess(BTNode<E> runningTask) {
        result = policy.onChildSuccess(this);
    }

    @Override
    public void childFail(BTNode<E> runningTask) {
        result = policy.onChildFail(this);
    }

    @Override
    public void resetAllChildren() {
//        for (int i = 0, n = getChildCount(); i < n; i++) {
//            BTNode<E> child = getChild(i);
//            child.reset();
//        }
        super.resetAllChildren();
    }

    @Override
    protected void copy(BTNode<E> node) throws InvocationTargetException, IllegalAccessException {
        super.copy(node);
        ParallelNode<E> parallelNode = (ParallelNode<E>) node;
        parallelNode.policy = this.policy;
        parallelNode.coordinator = this.coordinator;
    }

    public enum Policy {
        /*** 顺序策略 所有子节点执行成功 返回成功 */
        SEQUENCE() {
            @Override
            public Boolean onChildSuccess(ParallelNode<?> parallel) {
                switch (parallel.coordinator) {
                    case JOIN:
                        if (parallel.hasRunningChild)
                            return null;
                        for (int index = parallel.currentChildIndex; index < parallel.children.size(); index++) {
                            BTNode<?> child = parallel.children.get(index);
                            if (child.getStatus() == Status.BT_RUNNING)
                                return null;
                            if (index == parallel.children.size() - 1)
                                return child.getStatus() == Status.BT_SUCCESS ? Boolean.TRUE : null;
                        }

                    case RESUME:
                    default:
                        return !parallel.hasRunningChild && parallel.currentChildIndex == parallel.children.size() - 1 ? Boolean.TRUE : null;
                }

            }

            @Override
            public Boolean onChildFail(ParallelNode<?> parallel) {
                return Boolean.FALSE;
            }
        },
        /*** 选择策略*/
        SELECTOR() {
            @Override
            public Boolean onChildSuccess(ParallelNode<?> parallel) {
                return Boolean.TRUE;
            }

            @Override
            public Boolean onChildFail(ParallelNode<?> parallel) {
                switch (parallel.coordinator) {
                    case JOIN:
                        if (parallel.hasRunningChild)
                            return null;
                        for (int index = parallel.currentChildIndex; index < parallel.children.size(); index++) {
                            BTNode<?> child = parallel.children.get(index);
                            if (child.getStatus() == Status.BT_RUNNING)
                                return null;
                            if (index == parallel.children.size() - 1)
                                return child.getStatus() == Status.BT_FAILURE ? Boolean.FALSE : null;
                        }

                    case RESUME:
                    default:
                        return !parallel.hasRunningChild && parallel.currentChildIndex == parallel.children.size() - 1 ? Boolean.FALSE : null;
                }
            }
        };

        public abstract Boolean onChildSuccess(ParallelNode<?> parallel);

        public abstract Boolean onChildFail(ParallelNode<?> parallel);
    }

    public enum Coordinator {
        /*** 子节点每次都会恢复或重新执行 */
        RESUME() {
            @Override
            public <E> void execute(ParallelNode<E> parallel) {
                parallel.hasRunningChild = false;
                parallel.result = null;
                for (parallel.currentChildIndex = 0; parallel.currentChildIndex < parallel.children.size(); parallel.currentChildIndex++) {
                    BTNode<E> child = parallel.children.get(parallel.currentChildIndex);
                    if (child.getStatus() == Status.BT_RUNNING) {
                        child.runWithGuard();
                    } else {
                        child.setParent(parallel);
                        if (!child.start())
                            child.onFail();
                        else
                            child.runWithOutGuard();
                    }

                    if (parallel.result != null) {
                        parallel.cancelRunningChildren(parallel.hasRunningChild ? 0 : parallel.currentChildIndex + 1);
                        if (parallel.result)
                            parallel.onSuccess();
                        else
                            parallel.onFail();
                        return;
                    }
                }
                parallel.onRunning();
            }
        },
        /*** 子节点执行成功或失败后需等到并行节点成功失败后才能重新执行 */
        JOIN() {
            @Override
            public <E> void execute(ParallelNode<E> parallel) {
                parallel.hasRunningChild = false;
                parallel.result = null;
                for (parallel.currentChildIndex = 0; parallel.currentChildIndex < parallel.children.size(); parallel.currentChildIndex++) {
                    BTNode<E> child = parallel.children.get(parallel.currentChildIndex);
                    switch (child.getStatus()) {
                        case BT_RUNNING:
                            child.runWithGuard();
                            break;
                        case BT_SUCCESS:
                        case BT_FAILURE:
                            break;
                        default:
                            child.setParent(parallel);
                            if (!child.start())
                                child.onFail();
                            else
                                child.runWithOutGuard();
                            break;
                    }

                    if (parallel.result != null) {
                        parallel.cancelRunningChildren(parallel.hasRunningChild ? 0 : parallel.currentChildIndex + 1);
                        parallel.resetAllChildren();
                        if (parallel.result)
                            parallel.onSuccess();
                        else
                            parallel.onFail();
                        return;
                    }
                }
                parallel.onRunning();
            }
        };

        public abstract <E> void execute(ParallelNode<E> parallel);

    }
}
