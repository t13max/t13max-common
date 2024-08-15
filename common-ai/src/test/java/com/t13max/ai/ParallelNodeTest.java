package com.t13max.ai;

import com.t13max.ai.btree.BTNode;
import com.t13max.ai.btree.BehaviorTree;
import com.t13max.ai.btree.composites.ParallelNode;
import com.t13max.ai.btree.composites.ParallelNode.Coordinator;
import com.t13max.ai.btree.composites.ParallelNode.Policy;
import com.t13max.ai.nodes.TestExecuteNode;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author t13max
 * @Date 13:51 2024/5/23
 */
public class ParallelNodeTest {
    private final BehaviorTree<Object> behaviorTree = new BehaviorTree<>();
    private final TestExecuteNode<Object> node1 = new TestExecuteNode<>(BTNode.Status.BT_RUNNING);
    private final TestExecuteNode<Object> node2 = new TestExecuteNode<>(BTNode.Status.BT_RUNNING);
    private final TestExecuteNode<Object> node3 = new TestExecuteNode<>(BTNode.Status.BT_RUNNING);
    private final TestExecuteNode<Object> node4 = new TestExecuteNode<>(BTNode.Status.BT_RUNNING);
    private final List<BTNode<Object>> nodes = new ArrayList<>();

    @Before
    public void setUp() {
        nodes.add(node1);
        nodes.add(node2);
        nodes.add(node3);
        nodes.add(node4);
    }

    /**
     * 策略: Policy.SEQUENCE
     * 协调器: Coordinator.RESUME
     */
    @Test
    public void testSequenceWithResume() {
        ParallelNode<Object> root = new ParallelNode<>(Policy.SEQUENCE, Coordinator.RESUME, nodes);
        behaviorTree.addChild(root);

        behaviorTree.update();
        Assert.assertEquals(1, node1.getExecuteCount());
        Assert.assertEquals(1, node2.getExecuteCount());
        Assert.assertEquals(1, node3.getExecuteCount());
        Assert.assertEquals(1, node4.getExecuteCount());
        Assert.assertEquals(BTNode.Status.BT_RUNNING, root.getStatus());

        node1.setExecuteStatus(BTNode.Status.BT_SUCCESS);
        behaviorTree.update();
        Assert.assertEquals(2, node1.getExecuteCount());
        Assert.assertEquals(2, node2.getExecuteCount());
        Assert.assertEquals(2, node3.getExecuteCount());
        Assert.assertEquals(2, node4.getExecuteCount());
        Assert.assertEquals(BTNode.Status.BT_RUNNING, root.getStatus());

        node2.setExecuteStatus(BTNode.Status.BT_SUCCESS);
        behaviorTree.update();
        Assert.assertEquals(3, node1.getExecuteCount());
        Assert.assertEquals(3, node2.getExecuteCount());
        Assert.assertEquals(3, node3.getExecuteCount());
        Assert.assertEquals(3, node4.getExecuteCount());
        Assert.assertEquals(BTNode.Status.BT_RUNNING, root.getStatus());

        node3.setExecuteStatus(BTNode.Status.BT_SUCCESS);
        behaviorTree.update();
        Assert.assertEquals(4, node1.getExecuteCount());
        Assert.assertEquals(4, node2.getExecuteCount());
        Assert.assertEquals(4, node3.getExecuteCount());
        Assert.assertEquals(4, node4.getExecuteCount());
        Assert.assertEquals(BTNode.Status.BT_RUNNING, root.getStatus());

        node4.setExecuteStatus(BTNode.Status.BT_SUCCESS);
        behaviorTree.update();
        Assert.assertEquals(5, node1.getExecuteCount());
        Assert.assertEquals(5, node2.getExecuteCount());
        Assert.assertEquals(5, node3.getExecuteCount());
        Assert.assertEquals(5, node4.getExecuteCount());
        Assert.assertEquals(BTNode.Status.BT_SUCCESS, root.getStatus());

        node2.setExecuteStatus(BTNode.Status.BT_FAILURE);
        behaviorTree.update();
        Assert.assertEquals(6, node1.getExecuteCount());
        Assert.assertEquals(6, node2.getExecuteCount());
        Assert.assertEquals(5, node3.getExecuteCount());
        Assert.assertEquals(5, node4.getExecuteCount());
        Assert.assertEquals(BTNode.Status.BT_FAILURE, root.getStatus());
    }

    /**
     * 策略: Policy.SEQUENCE
     * 协调器: Coordinator.JOIN
     */
    @Test
    public void testSequenceWithJoin() {
        ParallelNode<Object> root = new ParallelNode<>(Policy.SEQUENCE, Coordinator.JOIN, nodes);
        behaviorTree.addChild(root);

        behaviorTree.update();
        Assert.assertEquals(1, node1.getExecuteCount());
        Assert.assertEquals(1, node2.getExecuteCount());
        Assert.assertEquals(1, node3.getExecuteCount());
        Assert.assertEquals(1, node4.getExecuteCount());
        Assert.assertEquals(BTNode.Status.BT_RUNNING, root.getStatus());

        node1.setExecuteStatus(BTNode.Status.BT_SUCCESS);
        behaviorTree.update();
        Assert.assertEquals(2, node1.getExecuteCount());
        Assert.assertEquals(2, node2.getExecuteCount());
        Assert.assertEquals(2, node3.getExecuteCount());
        Assert.assertEquals(2, node4.getExecuteCount());
        Assert.assertEquals(BTNode.Status.BT_RUNNING, root.getStatus());

        node4.setExecuteStatus(BTNode.Status.BT_SUCCESS);
        behaviorTree.update();
        Assert.assertEquals(2, node1.getExecuteCount());
        Assert.assertEquals(3, node2.getExecuteCount());
        Assert.assertEquals(3, node3.getExecuteCount());
        Assert.assertEquals(3, node4.getExecuteCount());
        Assert.assertEquals(BTNode.Status.BT_RUNNING, root.getStatus());

        node2.setExecuteStatus(BTNode.Status.BT_SUCCESS);
        behaviorTree.update();
        Assert.assertEquals(2, node1.getExecuteCount());
        Assert.assertEquals(4, node2.getExecuteCount());
        Assert.assertEquals(4, node3.getExecuteCount());
        Assert.assertEquals(3, node4.getExecuteCount());
        Assert.assertEquals(BTNode.Status.BT_RUNNING, root.getStatus());

        node3.setExecuteStatus(BTNode.Status.BT_SUCCESS);
        behaviorTree.update();
        Assert.assertEquals(2, node1.getExecuteCount());
        Assert.assertEquals(4, node2.getExecuteCount());
        Assert.assertEquals(5, node3.getExecuteCount());
        Assert.assertEquals(3, node4.getExecuteCount());
        Assert.assertEquals(BTNode.Status.BT_SUCCESS, root.getStatus());

        node2.setExecuteStatus(BTNode.Status.BT_FAILURE);
        behaviorTree.update();
        Assert.assertEquals(3, node1.getExecuteCount());
        Assert.assertEquals(5, node2.getExecuteCount());
        Assert.assertEquals(5, node3.getExecuteCount());
        Assert.assertEquals(3, node4.getExecuteCount());
        Assert.assertEquals(BTNode.Status.BT_FAILURE, root.getStatus());
    }

    /**
     * 策略: Policy.SELECTOR
     * 协调器: Coordinator.RESUME
     */
    @Test
    public void testSelectorWithResume() {
        ParallelNode<Object> root = new ParallelNode<>(Policy.SELECTOR, Coordinator.RESUME, nodes);
        behaviorTree.addChild(root);

        behaviorTree.update();
        Assert.assertEquals(1, node1.getExecuteCount());
        Assert.assertEquals(1, node2.getExecuteCount());
        Assert.assertEquals(1, node3.getExecuteCount());
        Assert.assertEquals(1, node4.getExecuteCount());
        Assert.assertEquals(BTNode.Status.BT_RUNNING, root.getStatus());

        node1.setExecuteStatus(BTNode.Status.BT_SUCCESS);
        behaviorTree.update();
        Assert.assertEquals(2, node1.getExecuteCount());
        Assert.assertEquals(1, node2.getExecuteCount());
        Assert.assertEquals(1, node3.getExecuteCount());
        Assert.assertEquals(1, node4.getExecuteCount());
        Assert.assertEquals(BTNode.Status.BT_SUCCESS, root.getStatus());

        node1.setExecuteStatus(BTNode.Status.BT_RUNNING);
        node2.setExecuteStatus(BTNode.Status.BT_FAILURE);
        behaviorTree.update();
        Assert.assertEquals(3, node1.getExecuteCount());
        Assert.assertEquals(2, node2.getExecuteCount());
        Assert.assertEquals(2, node3.getExecuteCount());
        Assert.assertEquals(2, node4.getExecuteCount());
        Assert.assertEquals(BTNode.Status.BT_RUNNING, root.getStatus());

        node3.setExecuteStatus(BTNode.Status.BT_SUCCESS);
        behaviorTree.update();
        Assert.assertEquals(4, node1.getExecuteCount());
        Assert.assertEquals(3, node2.getExecuteCount());
        Assert.assertEquals(3, node3.getExecuteCount());
        Assert.assertEquals(2, node4.getExecuteCount());
        Assert.assertEquals(BTNode.Status.BT_SUCCESS, root.getStatus());

        node1.setExecuteStatus(BTNode.Status.BT_RUNNING);
        node2.setExecuteStatus(BTNode.Status.BT_RUNNING);
        node3.setExecuteStatus(BTNode.Status.BT_RUNNING);
        node4.setExecuteStatus(BTNode.Status.BT_FAILURE);
        behaviorTree.update();
        Assert.assertEquals(5, node1.getExecuteCount());
        Assert.assertEquals(4, node2.getExecuteCount());
        Assert.assertEquals(4, node3.getExecuteCount());
        Assert.assertEquals(3, node4.getExecuteCount());
        Assert.assertEquals(BTNode.Status.BT_RUNNING, root.getStatus());

        node1.setExecuteStatus(BTNode.Status.BT_FAILURE);
        node2.setExecuteStatus(BTNode.Status.BT_FAILURE);
        node3.setExecuteStatus(BTNode.Status.BT_FAILURE);
        node4.setExecuteStatus(BTNode.Status.BT_FAILURE);
        behaviorTree.update();
        Assert.assertEquals(6, node1.getExecuteCount());
        Assert.assertEquals(5, node2.getExecuteCount());
        Assert.assertEquals(5, node3.getExecuteCount());
        Assert.assertEquals(4, node4.getExecuteCount());
        Assert.assertEquals(BTNode.Status.BT_FAILURE, root.getStatus());
    }

    /**
     * 策略: Policy.SELECTOR
     * 协调器: Coordinator.JOIN
     */
    @Test
    public void testSelectorWithJoin() {
        ParallelNode<Object> root = new ParallelNode<>(Policy.SELECTOR, Coordinator.JOIN, nodes);
        behaviorTree.addChild(root);

        behaviorTree.update();
        Assert.assertEquals(1, node1.getExecuteCount());
        Assert.assertEquals(1, node2.getExecuteCount());
        Assert.assertEquals(1, node3.getExecuteCount());
        Assert.assertEquals(1, node4.getExecuteCount());
        Assert.assertEquals(BTNode.Status.BT_RUNNING, root.getStatus());

        node1.setExecuteStatus(BTNode.Status.BT_SUCCESS);
        behaviorTree.update();
        Assert.assertEquals(2, node1.getExecuteCount());
        Assert.assertEquals(1, node2.getExecuteCount());
        Assert.assertEquals(1, node3.getExecuteCount());
        Assert.assertEquals(1, node4.getExecuteCount());
        Assert.assertEquals(BTNode.Status.BT_SUCCESS, root.getStatus());

        node1.setExecuteStatus(BTNode.Status.BT_RUNNING);
        node2.setExecuteStatus(BTNode.Status.BT_FAILURE);
        behaviorTree.update();
        Assert.assertEquals(3, node1.getExecuteCount());
        Assert.assertEquals(2, node2.getExecuteCount());
        Assert.assertEquals(2, node3.getExecuteCount());
        Assert.assertEquals(2, node4.getExecuteCount());
        Assert.assertEquals(BTNode.Status.BT_RUNNING, root.getStatus());

        node3.setExecuteStatus(BTNode.Status.BT_SUCCESS);
        behaviorTree.update();
        Assert.assertEquals(4, node1.getExecuteCount());
        Assert.assertEquals(2, node2.getExecuteCount());
        Assert.assertEquals(3, node3.getExecuteCount());
        Assert.assertEquals(2, node4.getExecuteCount());
        Assert.assertEquals(BTNode.Status.BT_SUCCESS, root.getStatus());

        node1.setExecuteStatus(BTNode.Status.BT_RUNNING);
        node2.setExecuteStatus(BTNode.Status.BT_RUNNING);
        node3.setExecuteStatus(BTNode.Status.BT_RUNNING);
        node4.setExecuteStatus(BTNode.Status.BT_FAILURE);
        behaviorTree.update();
        Assert.assertEquals(5, node1.getExecuteCount());
        Assert.assertEquals(3, node2.getExecuteCount());
        Assert.assertEquals(4, node3.getExecuteCount());
        Assert.assertEquals(3, node4.getExecuteCount());
        Assert.assertEquals(BTNode.Status.BT_RUNNING, root.getStatus());

        node1.setExecuteStatus(BTNode.Status.BT_FAILURE);
        node2.setExecuteStatus(BTNode.Status.BT_FAILURE);
        node3.setExecuteStatus(BTNode.Status.BT_FAILURE);
        node4.setExecuteStatus(BTNode.Status.BT_FAILURE);
        behaviorTree.update();
        Assert.assertEquals(6, node1.getExecuteCount());
        Assert.assertEquals(4, node2.getExecuteCount());
        Assert.assertEquals(5, node3.getExecuteCount());
        Assert.assertEquals(3, node4.getExecuteCount());
        Assert.assertEquals(BTNode.Status.BT_FAILURE, root.getStatus());
    }
}
