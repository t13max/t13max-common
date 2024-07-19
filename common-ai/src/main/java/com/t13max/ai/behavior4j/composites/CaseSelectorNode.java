package com.t13max.ai.behavior4j.composites;

import com.t13max.ai.behavior4j.BTNode;
import com.t13max.ai.behavior4j.decorators.CaseNode;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * 条件选择节点
 *
 * @Author t13max
 * @Date 16:28 2024/5/17
 */
public abstract class CaseSelectorNode<E> extends SelectorNode<E> {

    private Map<String, Integer> caseMap = new HashMap<>();

    protected abstract String getCondition();

    @Override
    protected int addChildToNode(BTNode<E> child) {
        if (!(child instanceof CaseNode))
            throw new IllegalArgumentException("入参错误, 条件选择节点只能有CaseNode子节点");
        int index = super.addChildToNode(child);
        caseMap.put(((CaseNode<E>) child).getCaseValue(), index);

        return index;
    }

    @Override
    protected void run() {
        //为空 直接失败
        if (children.isEmpty()) {
            onFail();
        } else {
            //获取指定条件的节点 尝试执行
            currentChildIndex = caseMap.getOrDefault(getCondition(), -1);
            if (currentChildIndex < 0) {
                onFail();
                return;
            }
            super.run();
        }
    }

    /**
     * 执行失败
     * 重写条件节点的方法 子节点失败则直接失败
     *
     * @Author t13max
     * @Date 16:32 2024/5/17
     */
    @Override
    public void childFail(BTNode<E> runningNode) {
        this.runningChild = null;
        onFail();
    }

    @Override
    protected void copy(BTNode<E> node) throws InvocationTargetException, IllegalAccessException {
        super.copy(node);
        CaseSelectorNode<E> caseSelectorNode = (CaseSelectorNode<E>) node;
        caseSelectorNode.caseMap = new HashMap<>(caseMap);
    }

}
