package com.t13max.ai;

import com.t13max.ai.agent.TestAgent;
import com.t13max.ai.behavior4j.BehaviorTree;
import com.t13max.ai.behavior4j.plugins.DefaultDataSource;
import com.t13max.ai.behavior4j.data.BehaviorTreeManager;
import org.junit.Test;

import java.util.Objects;

/**
 * @Author t13max
 * @Date 13:51 2024/5/23
 */
public class BehaviorTest {

    @Test
    public void testLoadTree() {
        BehaviorTreeManager.getInstance().bindDataSource(new DefaultDataSource(), Objects.requireNonNull(BehaviorTest.class.getClassLoader().getResource("")).getPath());
        BehaviorTree<TestAgent> behaviorTree = BehaviorTreeManager.getInstance().createBehaviorTree("TestBT");
        behaviorTree.setAgent(new TestAgent());
        behaviorTree.update();
    }

}
