package com.t13max.ai.behavior4j.decorators;

import com.t13max.ai.behavior4j.BTNode;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.InvocationTargetException;

/**
 * 条件选择节点
 *
 * @Author t13max
 * @Date 16:29 2024/5/17
 */
@Setter
@Getter
public class CaseNode<E> extends Decorator<E> {

    //条件值
    private String caseValue;

    public CaseNode() {
    }

    public CaseNode(String caseValue) {
        this.caseValue = caseValue;
    }

    public CaseNode(BTNode<E> child, String caseValue) {
        super(child);
        this.caseValue = caseValue;
    }

    @Override
    protected void copy(BTNode<E> node) throws InvocationTargetException, IllegalAccessException {
        super.copy(node);
        CaseNode<E> caseNode = (CaseNode<E>) node;
        caseNode.caseValue = this.caseValue;
    }
}
