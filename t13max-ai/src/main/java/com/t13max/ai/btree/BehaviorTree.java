package com.t13max.ai.btree;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @Author t13max
 * @Date 13:48 2024/5/23
 */
public class BehaviorTree<E> extends BTNode<E> {

    /*** the name behavior tree*/
    @Getter
    @Setter
    private String name;

    private BTNode<E> rootNode;

    private E owner;

    protected Map<Integer, Set<BTNode<E>>> listeners = new HashMap<>();

    @Getter
    private Map<String, Object> params = new HashMap<>();

    public BehaviorTree() {
        this(null, null, null);
    }

    public BehaviorTree(BTNode<E> root, E owner, String name) {
        this.rootNode = root;
        this.owner = owner;
        this.tree = this;
        this.name = name;
    }

    public void setRootNode(BTNode<E> rootNode) {
        this.rootNode = rootNode;
        eventsRegister(rootNode);
    }

    public void update() {
        if (rootNode.status == Status.BT_RUNNING) {
            rootNode.runWithGuard();
        } else {
            rootNode.setParent(this);
            if (rootNode.start())
                rootNode.runWithOutGuard();
        }
    }

    @Override
    public int getChildCount() {
        return rootNode == null ? 0 : 1;
    }

    @Override
    public BTNode<E> getChild(int i) {
        if (i == 0 && rootNode != null)
            return rootNode;
        throw new IndexOutOfBoundsException("index can't be >= size: " + i + " >= " + getChildCount());
    }

    @Override
    protected void run() {
        update();
    }

    @Override
    public void childSuccess(BTNode<E> node) {
        this.onSuccess();
    }

    @Override
    public void childFail(BTNode<E> node) {
        this.onFail();
    }

    @Override
    public void childRunning(BTNode<E> runningNode, BTNode<E> reporter) {
        this.onRunning();
    }

    @Override
    protected int addChildToNode(BTNode<E> child) {
        if (this.rootNode != null)
            throw new IllegalStateException("A behavior tree cannot have more than one root node");
        this.rootNode = child;
        return 0;
    }

    @Override
    public E getOwner() {
        return owner;
    }

    @Override
    public BTNode<E> getRootNode() {
        return rootNode;
    }

    @Override
    public BehaviorTree<E> newInstance() {
        BehaviorTree<E> behaviorTree = (BehaviorTree<E>) super.newInstance();
        behaviorTree.eventsRegister(behaviorTree.rootNode);

        return behaviorTree;
    }

    @Override
    protected void copy(BTNode<E> node) throws InvocationTargetException, IllegalAccessException {
        super.copy(node);
        BehaviorTree<E> tree = (BehaviorTree<E>) node;
        tree.name = name;
        tree.rootNode = rootNode.newInstance();
    }

    @Override
    public void setParent(BTNode<E> parent) {
        this.parent = parent;
        this.owner = parent.getTree() != null ? parent.getTree().owner : null;
    }

    @Override
    public void reset() {
        BehaviorTree<E> rootTree = getRootTree();
        if (rootTree != null)
            rootTree.notifyChildReset(this);

        parent = null;
        status = Status.BT_FRESH;
    }

    public void eventsRegister(BTNode<E> node) {
        node.getEvents().keySet().forEach(key -> eventRegister(key, node));
        node.setRegister(true);
    }

    public void notifyChildReset(BTNode<E> node) {
        node.getEvents().forEach(this::eventRemove);
        node.setRegister(false);
    }

    public void setOwner(E owner) {
        this.owner = owner;
    }

    /**
     * handle event
     *
     * @param event event key
     */
    public void handle(Integer event, Object... params) {
        Set<BTNode<E>> nodes = listeners.getOrDefault(event, new HashSet<>());
        for (BTNode<E> node : nodes) {
            if (node.equals(rootNode) || (node.getTree() != null && node.getStatus() == Status.BT_RUNNING)) {
                // node maybe not running when node is root node
                if (node.getStatus() != Status.BT_RUNNING)
                    node.setParent(tree);
                if (node.handleOnce(event, params))
                    break;
            }
        }
    }

    public void addParam(String name, Object value) {
        params.put(name, value);
    }

    public Object getParams(String name) {
        return params.get(name);
    }

    private void eventRegister(Integer event, BTNode<E> node) {
        Set<BTNode<E>> nodes = listeners.computeIfAbsent(event, k -> new HashSet<>());

        Objects.requireNonNull(nodes).add(node);
    }

    private void eventRemove(Integer event, BTNode<E> node) {
        Set<BTNode<E>> nodes = listeners.getOrDefault(event, new HashSet<>());
        Objects.requireNonNull(nodes).remove(node);

    }
}
