package com.t13max.ai.nodes;


import com.t13max.ai.behavior4j.attachments.PreActionNode;

/**
 * @Author t13max
 * @Date 13:51 2024/5/23
 */
public class TestPreconditionSuccess extends PreActionNode {

    @Override
    public boolean preCondition() {
        return true;
    }
}
