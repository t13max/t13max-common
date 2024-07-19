package com.t13max.ai.behavior4j.event;

import com.t13max.ai.behavior4j.BTNode;
import com.t13max.ai.behavior4j.BehaviorTree;
import com.t13max.ai.behavior4j.decorators.ReferenceNode;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * 事件节点
 *
 * @Author t13max
 * @Date 13:47 2024/5/23
 */
@Getter
@Setter
public class EventNode<E> extends ReferenceNode<E> {

    private int event;

    @Getter
    private int triggerType;

    private boolean triggerOnce;

    private boolean triggered;

    private List<String> paramList = new ArrayList<>();

    public EventNode() {
    }

    public EventNode(int triggerType, boolean triggerOnce) {
        this.triggerType = triggerType;
        this.triggerOnce = triggerOnce;
    }

    public EventNode(String subtree, int triggerType, boolean triggerOnce) {
        super(subtree);
        this.triggerType = triggerType;
        this.triggerOnce = triggerOnce;
    }

    public static String exportClass() {
        return "Event";
    }

    @Override
    protected void run() {
        super.run();
        if (triggerType != TriggerType.eventNode.type) {
            if (child.getStatus() == Status.BT_SUCCESS)
                childSuccess(child);
            else if (child.getStatus() == Status.BT_FAILURE)
                childFail(child);
        }
    }

    @Override
    public void childRunning(BTNode<E> runningNode, BTNode<E> reporter) {
        super.childRunning(runningNode, reporter);
    }

    @Override
    public void childFail(BTNode<E> runningNode) {
        super.childFail(runningNode);
    }

    @Override
    public void childSuccess(BTNode<E> runningNode) {
        super.childSuccess(runningNode);
    }

    @Override
    public void end() {
        super.end();
        if (triggerType == TriggerType.returnNode.type)
            parent.nodeReturn();
    }

    @Override
    protected void copy(BTNode<E> node) throws InvocationTargetException, IllegalAccessException {
        super.copy(node);
        EventNode<E> eventNode = (EventNode<E>) node;

        eventNode.event = this.event;
        eventNode.triggerType = this.triggerType;
        eventNode.triggerOnce = this.triggerOnce;
        eventNode.paramList.addAll(this.paramList);
    }

    public void addEventParam(Object... param) {
        for (int i = 0; i < paramList.size() && i < param.length; i++) {
            getSubTree().addParam(paramList.get(i), param[i]);
        }
    }

    public boolean canTranslate() {
        return !triggered || !triggerOnce;
    }

    public void cleanup() {
        getSubTree().getParams().clear();
    }

    private BehaviorTree<E> getSubTree() {
        return (BehaviorTree<E>) child;
    }
}
