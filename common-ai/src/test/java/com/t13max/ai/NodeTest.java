package com.t13max.ai;

import com.t13max.ai.agent.TestAgent;
import com.t13max.ai.behavior4j.BTNode;
import com.t13max.ai.behavior4j.BTNode.Status;
import com.t13max.ai.behavior4j.BehaviorTree;
import com.t13max.ai.behavior4j.attachments.AttachmentNode;
import com.t13max.ai.behavior4j.composites.SelectorNode;
import com.t13max.ai.behavior4j.composites.SequenceNode;
import com.t13max.ai.behavior4j.leaf.EndNode;
import com.t13max.ai.behavior4j.plugins.DefaultDataSource;
import com.t13max.ai.data.BehaviorTreeManager;
import com.t13max.ai.nodes.TestExecuteNode;
import com.t13max.ai.nodes.TestPreconditionFail;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author t13max
 * @Date 13:51 2024/5/23
 */
public class NodeTest {
    private final BehaviorTree<TestAgent> behaviorTree = new BehaviorTree<>(null, null, "testBT");
    private final TestExecuteNode<TestAgent> executeNode1 = new TestExecuteNode<>();
    private final TestExecuteNode<TestAgent> executeNode2 = new TestExecuteNode<>();

    @Before
    public void preTest() {
        BehaviorTreeManager
                .getInstance()
                .bindDataSource(new DefaultDataSource(), Objects.requireNonNull(BehaviorTest.class.getClassLoader().getResource("")).getPath());
        behaviorTree.setAgent(new TestAgent());
    }

    @Test
    public void testSequence() {
        List<BTNode<TestAgent>> nodes = new ArrayList<>();
        nodes.add(executeNode1);
        nodes.add(executeNode2);
        SequenceNode<TestAgent> sequenceNode = new SequenceNode<>(nodes);
        behaviorTree.addChild(sequenceNode);
        behaviorTree.update();

        Assert.assertEquals(1, executeNode1.getExecuteCount());
        Assert.assertEquals(1, executeNode2.getExecuteCount());
        Assert.assertEquals(Status.BT_SUCCESS, behaviorTree.getStatus());

        executeNode1.setExecuteStatus(Status.BT_FAILURE);
        behaviorTree.update();

        Assert.assertEquals(2, executeNode1.getExecuteCount());
        Assert.assertEquals(1, executeNode2.getExecuteCount());
        Assert.assertEquals(Status.BT_FAILURE, behaviorTree.getStatus());
    }

    @Test
    public void testSelector() {
        List<BTNode<TestAgent>> nodes = new ArrayList<>();
        nodes.add(executeNode1);
        nodes.add(executeNode2);
        SelectorNode<TestAgent> selectorNode = new SelectorNode<>(nodes);
        behaviorTree.addChild(selectorNode);
        behaviorTree.update();

        Assert.assertEquals(1, executeNode1.getExecuteCount());
        Assert.assertEquals(0, executeNode2.getExecuteCount());
        Assert.assertEquals(Status.BT_SUCCESS, behaviorTree.getStatus());

        executeNode1.setExecuteStatus(Status.BT_FAILURE);
        behaviorTree.update();

        Assert.assertEquals(2, executeNode1.getExecuteCount());
        Assert.assertEquals(1, executeNode2.getExecuteCount());
        Assert.assertEquals(Status.BT_SUCCESS, behaviorTree.getStatus());

        executeNode2.setExecuteStatus(Status.BT_RUNNING);
        behaviorTree.update();

        Assert.assertEquals(3, executeNode1.getExecuteCount());
        Assert.assertEquals(2, executeNode2.getExecuteCount());
        Assert.assertEquals(Status.BT_RUNNING, behaviorTree.getStatus());

        executeNode1.setExecuteStatus(Status.BT_SUCCESS);
        behaviorTree.update();

        Assert.assertEquals(3, executeNode1.getExecuteCount());
        Assert.assertEquals(3, executeNode2.getExecuteCount());
        Assert.assertEquals(Status.BT_RUNNING, behaviorTree.getStatus());
    }

    @Test
    public void testAttachment() {
        List<BTNode<TestAgent>> nodes = new ArrayList<>();
        nodes.add(executeNode1);
        nodes.add(executeNode2);
        SelectorNode<TestAgent> selectorNode = new SelectorNode<>(nodes);
        behaviorTree.addChild(selectorNode);
        behaviorTree.update();

        Assert.assertEquals(1, executeNode1.getExecuteCount());
        Assert.assertEquals(0, executeNode2.getExecuteCount());

        TestPreconditionFail<TestAgent> attachmentNode = new TestPreconditionFail<>();
        attachmentNode.setPhase(AttachmentNode.PHASE_PRECONDITION);
        executeNode1.addAttachment(attachmentNode);
        behaviorTree.update();

        Assert.assertEquals(1, executeNode1.getExecuteCount());
        Assert.assertEquals(1, executeNode2.getExecuteCount());

        attachmentNode.setPhase(AttachmentNode.PHASE_NONE);
        executeNode1.setExecuteStatus(Status.BT_RUNNING);
        behaviorTree.update();

        Assert.assertEquals(2, executeNode1.getExecuteCount());
        Assert.assertEquals(1, executeNode2.getExecuteCount());


        attachmentNode.setPhase(AttachmentNode.PHASE_START);
        executeNode1.setExecuteStatus(Status.BT_SUCCESS);
        behaviorTree.update();

        Assert.assertEquals(3, executeNode1.getExecuteCount());
        Assert.assertEquals(1, executeNode2.getExecuteCount());
    }

    @Test
    public void testEndNode() {
        List<BTNode<TestAgent>> nodes = new ArrayList<>();
        EndNode<TestAgent> endNode = new EndNode<>(EndNode.SUCCESS, false);
        nodes.add(executeNode1);
        nodes.add(endNode);
        nodes.add(executeNode2);
        SequenceNode<TestAgent> rootNode = new SequenceNode<>();
        SequenceNode<TestAgent> sequenceNode = new SequenceNode<>(nodes);
        rootNode.addChild(sequenceNode);
        behaviorTree.addChild(rootNode);
        behaviorTree.update();

        Assert.assertEquals(1, executeNode1.getExecuteCount());
        Assert.assertEquals(0, executeNode2.getExecuteCount());
        Assert.assertEquals(Status.BT_CANCEL, sequenceNode.getStatus());
        Assert.assertEquals(Status.BT_SUCCESS, rootNode.getStatus());
        Assert.assertEquals(Status.BT_SUCCESS, behaviorTree.getStatus());

//        BehaviorTree<TestAgent> outsideTree = new BehaviorTree<>("outsideTree");
//        outsideTree.setAgent(new TestAgent());
//        SequenceNode<TestAgent> outsideRoot = new SequenceNode<>();
//        Decorator<TestAgent> reference = new ReferenceNode<>(behaviorTree.getName());
//        outsideRoot.addChild(reference);
//        outsideTree.addChild(outsideRoot);
//        outsideTree.update();

//        Assert.assertEquals(1, executeNode1.getExecuteCount());
//        Assert.assertEquals(0, executeNode2.getExecuteCount());
//        Assert.assertEquals(Status.BT_SUCCESS, behaviorTree.getStatus());
//        Assert.assertEquals(Status.BT_SUCCESS, outsideTree.getStatus());
//
//        endNode.setInterruptOutside(true);
//        outsideTree.update();
//
//        Assert.assertEquals(3, executeNode1.getExecuteCount());
//        Assert.assertEquals(0, executeNode2.getExecuteCount());
//        Assert.assertEquals(Status.BT_CANCEL, behaviorTree.getStatus());
//        Assert.assertEquals(Status.BT_SUCCESS, outsideTree.getStatus());
    }

}
