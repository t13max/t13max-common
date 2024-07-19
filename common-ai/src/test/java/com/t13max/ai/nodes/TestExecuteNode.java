package com.t13max.ai.nodes;

import com.t13max.ai.behavior4j.leaf.ActionNode;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author t13max
 * @Date 13:51 2024/5/23
 */
@Getter
@Setter
public class TestExecuteNode<E> extends ActionNode<E> {
    private Status executeStatus = Status.BT_SUCCESS;
    private int executeCount = 0;

    public TestExecuteNode() {
    }

    public TestExecuteNode(Status executeStatus) {
        this.executeStatus = executeStatus;
    }

    public Status execute() {
        executeCount++;

        return executeStatus;
    }
}
