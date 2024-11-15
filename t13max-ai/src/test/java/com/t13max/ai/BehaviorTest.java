package com.t13max.ai;

import com.t13max.ai.agent.TestAgent;
import com.t13max.ai.btree.BehaviorTree;
import com.t13max.ai.btree.utils.BehaviorTreeManager;
import org.junit.Test;

import java.util.Objects;

/**
 * @Author t13max
 * @Date 13:51 2024/5/23
 */
public class BehaviorTest {

    @Test
    public void testLoadTree() {
        BehaviorTreeManager.getInstance().bindDataPath(Objects.requireNonNull(BehaviorTest.class.getClassLoader().getResource("")).getPath());
        BehaviorTree<TestAgent> behaviorTree = BehaviorTreeManager.getInstance().createBehaviorTree("TestBT");
        behaviorTree.setOwner(new TestAgent());
        behaviorTree.update();
    }

}
