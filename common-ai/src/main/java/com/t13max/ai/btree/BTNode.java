package com.t13max.ai.btree;

import com.t13max.ai.btree.attachments.AttachmentNode;
import com.t13max.ai.btree.event.EventNode;
import com.t13max.ai.btree.event.TriggerType;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 行为树节点基类
 *
 * @Author t13max
 * @Date 15:58 2024/5/17
 */
@Log4j2
public abstract class BTNode<E> {

    @Getter
    @Setter
    protected int id;
    //节点状态
    protected Status status = Status.BT_FRESH;
    //打断状态
    protected Status interruptStatus;
    //节点所属行为树
    @Getter
    protected BehaviorTree<E> tree;
    //父结点
    protected BTNode<E> parent;
    //附件
    protected List<AttachmentNode<E>> attachments = new ArrayList<>();
    //事件节点
    @Getter
    protected Map<Integer, EventNode<E>> events = new HashMap<>();
    //是否注册了事件
    @Setter
    private boolean register;
    //
    private EventNode<E> translation;
    //日志信息
    @Setter
    protected String traceInfo;

    public final void addEvent(int eventKey, EventNode<E> eventNode) {
        events.put(eventKey, eventNode);
    }

    public final int addChild(BTNode<E> child) {
        return addChildToNode(child);
    }

    public abstract int getChildCount();

    public abstract BTNode<E> getChild(int i);

    protected abstract int addChildToNode(BTNode<E> child);

    public final void addAttachment(AttachmentNode<E> attachment) {
        attachments.add(attachment);
        attachment.setNode(this);
    }

    public void setParent(BTNode<E> parent) {
        this.parent = parent;
        this.tree = parent.tree;
    }

    public final Status getStatus() {
        return status;
    }

    public E getOwner() {
        if (tree == null)
            throw new IllegalStateException("行为树为空");
        return tree.getOwner();
    }

    protected abstract void run();

    public final void runWithGuard() {
        if (traceInfo != null)
            log.debug("Running {}", traceInfo);

        //translate
        if (translation != null) {
            translation.setParent(this);
            translation.runWithGuard();
            return;
        }

        // 存在附件
        if (attachments.isEmpty()) {
            run();
        } else {
            //校验前置条件
            if (predicate(AttachmentNode.PHASE_UPDATE)) {
                run();
            } else {
                onFail();
            }
        }
    }

    public final void runWithOutGuard() {
        if (traceInfo != null)
            log.debug("Start {}", traceInfo);

        //translate
        if (translation != null) {
            translation.setParent(this);
            translation.runWithOutGuard();
            return;
        }

        run();
    }

    public boolean start() {
        interruptStatus = null;

        return predicate(AttachmentNode.PHASE_START);
    }

    public void end() {
    }

    public final void onRunning() {
        status = Status.BT_RUNNING;

        if (parent != null)
            parent.childRunning(this, this);

        BehaviorTree<E> rootTree = getRootTree();
        if (rootTree != null && needEventRegister()) {
            rootTree.eventsRegister(this);
        }

    }

    /**
     * 节点执行返回成功调用
     *
     * @Author t13max
     * @Date 15:56 2024/5/17
     */
    public final void onSuccess() {
        status = Status.BT_SUCCESS;
        if (!attachments.isEmpty()) {
            attachments.stream()
                    .filter(attachment -> attachment.checkPhase(AttachmentNode.PHASE_SUCCESS))
                    .forEach(AttachmentNode::effect);
        }

        end();
        if (parent != null)
            parent.childSuccess(this);
    }

    /**
     * 节点执行返回失败调用
     *
     * @Author t13max
     * @Date 15:57 2024/5/17
     */
    public final void onFail() {
        status = Status.BT_FAILURE;

        if (!attachments.isEmpty()) {
            attachments.stream().filter(attachment -> attachment.checkPhase(AttachmentNode.PHASE_FAIL)).forEach(AttachmentNode::effect);
        }

        end();

        if (parent != null) parent.childFail(this);
    }

    /**
     * 打断
     *
     * @Author t13max
     * @Date 15:57 2024/5/17
     */
    public final void onInterrupt() {
        status = Status.BT_CANCEL;

        //向上打断
        if (interruptStatus != null) {
            switch (interruptStatus) {
                case BT_SUCCESS:
                    onSuccess();
                    return;
                case BT_FAILURE:
                    onFail();
                    return;
                case BT_RUNNING:
                    onRunning();
                    return;
                default:
                    break;
            }
        }

        if (parent != null)
            parent.childInterrupt(this);
    }

    /**
     * 取消
     *
     * @Author t13max
     * @Date 15:57 2024/5/17
     */
    public final void onCancel() {
        cancelRunningChildren(0);
        status = Status.BT_CANCEL;
    }

    /**
     * 当子节点执行SUCCESS时调用
     *
     * @Author t13max
     * @Date 15:57 2024/5/17
     */
    public abstract void childSuccess(BTNode<E> node);

    /**
     * 当子节点执行FAILED时调用
     *
     * @Author t13max
     * @Date 15:57 2024/5/17
     */
    public abstract void childFail(BTNode<E> node);

    public abstract void childRunning(BTNode<E> runningNode, BTNode<E> reporter);

    public void childInterrupt(BTNode<E> node) {
        onInterrupt();
    }

    public void resetAllChildren() {
        for (int i = 0, n = getChildCount(); i < n; i++) {
            BTNode<E> child = getChild(i);
            child.resetAllChildren();
            child.reset();
        }
    }

    public void reset() {
        BehaviorTree<E> rootTree = getRootTree();
        if (rootTree != null)
            rootTree.notifyChildReset(this);

        parent = null;
        status = Status.BT_FRESH;
        tree = null;
    }

    /**
     * 中断该节点所有子节点的执行
     *
     * @Author t13max
     * @Date 15:57 2024/5/17
     */
    public void interrupt(Status status) {
        this.interruptStatus = status;
    }

    public BTNode<E> getRootNode() {
        BehaviorTree<E> behaviorTree = tree;
        while (behaviorTree.parent != null) {
            behaviorTree = behaviorTree.parent.tree;
        }
        return behaviorTree.getRootNode();
    }

    public boolean handleOnce(Integer event, Object... param) {
        EventNode<E> eventNode = events.get(event);
        if (eventNode == null || !eventNode.canTranslate())
            return false;
        eventNode.cleanup();
        eventNode.addEventParam(param);
        if (eventNode.getTriggerType() == TriggerType.eventNode.type) {
            //处理事件节点
            eventNode.setParent(this);
            eventNode.start();
            eventNode.runWithOutGuard();
        } else {
            resetAllChildren();
            translation = eventNode;
            translation.setTriggered(true);
            if (translation.status == Status.BT_RUNNING) translation.resetAllChildren();
        }

        return true;
    }

    public void nodeReturn() {
        translation = null;
    }

    @SuppressWarnings("unchecked")
    public BTNode<E> newInstance() {
        try {
            BTNode<E> clone = this.getClass().getDeclaredConstructor().newInstance();

            copy(clone);

            clone.attachments = attachments == null ? null : attachments.stream()
                    .map(attachment -> (AttachmentNode<E>) attachment.newInstance())
                    .peek(attachment -> attachment.setNode(clone))
                    .collect(Collectors.toList());
            clone.events = events == null ? null : events.values().stream()
                    .map(event -> (EventNode<E>) event.newInstance()).collect(Collectors.toMap(EventNode::getEvent, a -> a));
            return clone;
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException |
                 NoSuchMethodException e) {
            e.printStackTrace();

        }

        return null;
    }

    protected void copy(BTNode<E> node) throws InvocationTargetException, IllegalAccessException {
        node.id = this.id;
        node.traceInfo = this.traceInfo;
    }

    public BehaviorTree<E> getRootTree() {
        BehaviorTree<E> behaviorTree = tree;
        if (behaviorTree == null)
            return null;
        while (behaviorTree.parent != null) {
            behaviorTree = behaviorTree.parent.tree;
        }
        return behaviorTree;
    }

    public void bindTree(BehaviorTree<E> tree) {
        this.tree = tree;
    }

    protected void cancelRunningChildren(int startIndex) {
        for (int i = startIndex, n = getChildCount(); i < n; i++) {
            BTNode<E> child = getChild(i);
            if (child.status == Status.BT_RUNNING)
                child.onCancel();
        }
    }

    protected Object getParam(String name) {
        return this.tree == null ? null : this.tree.getParams(name);
    }

    private boolean needEventRegister() {
        return !events.isEmpty() && !register;
    }

    private boolean predicate(int phase) {
        Boolean success = null;
        for (AttachmentNode<E> attachment : attachments) {
            if (attachment.checkPhase(phase)) {
                if (success == null) {
                    success = attachment.preCondition();
                } else if (AttachmentNode.Operator.And == attachment.getOperator()) {
                    success &= attachment.preCondition();
                    if (!success) {
                        break;
                    }
                } else if (AttachmentNode.Operator.Or == attachment.getOperator()) {
                    success = success || attachment.preCondition();
                }
            }
        }
        return success == null || success;
    }

    public enum Status {
        /*** 节点尚未执行或已被重置 */
        BT_FRESH,
        /*** 节点下次还会执行 */
        BT_RUNNING,
        /*** 节点失败返回 */
        BT_FAILURE,
        /*** 节点成功返回 */
        BT_SUCCESS,
        /*** 节点执行取消 */
        BT_CANCEL
    }
}
