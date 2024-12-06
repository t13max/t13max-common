package com.t13max.ai.btree.utils;

import com.t13max.ai.btree.BTNode;
import com.t13max.ai.btree.attachments.AttachmentNode;
import com.t13max.ai.btree.attachments.PreActionNode;
import com.t13max.ai.btree.composites.*;
import com.t13max.ai.btree.decorators.*;
import com.t13max.ai.btree.event.EventNode;
import com.t13max.ai.btree.event.TriggerType;
import com.t13max.ai.btree.leaf.*;
import org.dom4j.Element;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @Author t13max
 * @Date 13:49 2024/5/23
 */
public class BehaviorTreeLoader {

    private static final String ATTR_CLASS = "class";

    static <T> List<BTNode<T>> loadElement(Element element) {
        List<BTNode<T>> nodeList = new ArrayList<>();
        Iterator<Element> it = element.elementIterator();

        while (it.hasNext()) {
            Element subElement = it.next();
            if ("node".equals(subElement.getName())) {
                BTNode<T> subNode = loadNode(subElement);
                nodeList.add(subNode);
            } else if ("attachment".equals(subElement.getName())) {
                // event attachment
                if (EventNode.exportClass().equals(subElement.attributeValue(ATTR_CLASS))) {
                    BTNode<T> subNode = loadEventNode(subElement);
                    nodeList.add(subNode);
                } else {
                    BTNode<T> subNode = loadAttachment(subElement);
                    nodeList.add(subNode);
                }
            }
        }

        return nodeList;
    }

    private static <T> BTNode<T> loadCustomConnector(Element element) {
        Iterator<Element> it = element.elementIterator();
        while (it.hasNext()) {
            Element subElement = it.next();
            if ("custom".equals(subElement.getName())) {
                List<BTNode<T>> nodeList = loadElement(subElement);
                if (!nodeList.isEmpty())
                    return nodeList.get(0);
            }
        }

        return null;
    }

    private static <T> BTNode<T> loadNode(Element nodeElement) {
        String classType = nodeElement.attributeValue(ATTR_CLASS);

        List<BTNode<T>> nodes = loadElement(nodeElement);
        BTNode<T> node;
        switch (classType) {
            case "Parallel":
                String policy = nodeElement.attributeValue("policy");
                String coordinator = nodeElement.attributeValue("coordinator");
                node = new ParallelNode<>(ParallelNode.Policy.valueOf(policy), ParallelNode.Coordinator.valueOf(coordinator));
                break;
            case "Sequence": {
                node = new SequenceNode<>();
                BTNode<T> condition = loadCustomConnector(nodeElement);
                if (condition != null)
                    ((SingleRunningBranchNode<T>) node).setCondition(condition);
                break;
            }
            case "RandomSequence": {
                node = new RandomSequenceNode<>();
                BTNode<T> condition = loadCustomConnector(nodeElement);
                if (condition != null)
                    ((SingleRunningBranchNode<T>) node).setCondition(condition);
                break;
            }
            case "Selector": {
                node = new SelectorNode<>();
                BTNode<T> condition = loadCustomConnector(nodeElement);
                if (condition != null)
                    ((SingleRunningBranchNode<T>) node).setCondition(condition);
                break;
            }
            case "RandomSelector": {
                node = new RandomSelectorNode<>();
                BTNode<T> condition = loadCustomConnector(nodeElement);
                if (condition != null)
                    ((SingleRunningBranchNode<T>) node).setCondition(condition);
                break;
            }
            case "ProbabilitySelector": {
                node = new ProbabilitySelectorNode<>();
                BTNode<T> condition = loadCustomConnector(nodeElement);
                if (condition != null)
                    ((SingleRunningBranchNode<T>) node).setCondition(condition);
                break;
            }
            case "CaseSelectorNode": {
                String impl = nodeElement.attributeValue("impl");
                node = createNode(impl);
                if (!(node instanceof CaseSelectorNode)) {
                    throw new IllegalArgumentException("实现类配置错误, class=" + impl);
                }
                BTNode<T> condition = loadCustomConnector(nodeElement);
                if (condition != null)
                    ((CaseSelectorNode<T>) node).setCondition(condition);
                break;
            }
            case "IfElse": {
                node = new IfElseNode<>();
                break;
            }
            case "AlwaysFailure":
                node = new AlwaysFailureNode<>();
                break;
            case "AlwaysSuccess":
                node = new AlwaysSuccessNode<>();
                break;
            case "Repeat": {
                String count = nodeElement.attributeValue("count");
                String isFrame = nodeElement.attributeValue("doneWithinFrame");
                node = new RepeatNode<>(Integer.parseInt(count), Boolean.parseBoolean(isFrame));
                break;
            }
            case "Not":
                node = new RevertNode<>();
                break;
            case "UntilFailure":{
                node = new UntilFailureNode<>();
                break;
            }
            case "UntilSuccess":{
                node = new UntilSuccessNode<>();
                break;
            }
            case "AssignmentNode":
            case "ActionNode": {
                String impl = nodeElement.attributeValue("impl");
                node = createNode(impl);
                if (!(node instanceof ActionNode)) {
                    throw new IllegalArgumentException("实现类配置错误, class=" + impl);
                }
                break;
            }
            case "Noop":
                node = new NoopNode<>();
                break;
            case "End":
                String endOutside = nodeElement.attributeValue("endOutside");
                String endStatus = nodeElement.attributeValue("endStatus");
                node = new EndNode<>(endStatus, Boolean.parseBoolean(endOutside));
                break;
            case "FailureNode":
                node = new FailureNode<>();
                break;
            case "Success":
                node = new SuccessNode<>();
                break;
            case "DecoratorWeight":
                String weight = nodeElement.attributeValue("weight");
                node = new WeightNode<>(Integer.parseInt(weight));
                break;
            case "CaseNode":
                String caseValue = nodeElement.attributeValue("CaseValue");
                node = new CaseNode<>(caseValue.replace("\"", ""));
                break;
            case "ReferencedBehavior":
                String subTreeName = nodeElement.attributeValue("referenceBehavior");
                node = new ReferenceNode<>(subTreeName.replace("\"", ""));
                break;
            case "ConditionNode": {
                String impl = nodeElement.attributeValue("impl");
                node = createNode(impl);
                if (!(node instanceof ConditionNode)) {
                    throw new IllegalArgumentException("实现类配置错误, class=" + impl);
                }
                break;
            }
            case "And":
                node = new AndNode<>();
                break;
            case "Or":
                node = new OrNode<>();
                break;
            case "DecoratorCountLimit": {
                String limitCount = nodeElement.attributeValue("count");
                node = new CountLimitNode<>(Integer.parseInt(limitCount));
                BTNode<T> condition = loadCustomConnector(nodeElement);
                if (condition != null)
                    ((CountLimitNode<T>) node).setCondition(condition);
                break;
            }
            case "DecoratorLoopUntil": {
                String count = nodeElement.attributeValue("count");
                node = new LoopUntilNode<>(Integer.parseInt(count));
                break;
            }
            case "Task": {
                node = new TaskNode<>();
                break;
            }
            default:
                throw new IllegalArgumentException("未知节点类型 : " + classType);
        }

        nodes.forEach(subNode -> {
            if (subNode instanceof AttachmentNode) {
                node.addAttachment((AttachmentNode<T>) subNode);
            } else if (subNode instanceof EventNode<T> eventNode) {
                node.addEvent(eventNode.getEvent(), eventNode);
            } else {
                node.addChild(subNode);
            }
        });
        String id = nodeElement.attributeValue("id");
        node.setId(Integer.parseInt(id));
        String traceInfo = nodeElement.attributeValue("trace");
        node.setTraceInfo(traceInfo);
        return node;
    }

    private static <T> BTNode<T> loadAttachment(Element attachment) {
        String classType = attachment.attributeValue(ATTR_CLASS);
        String clazzAttr = attachment.attributeValue("class");
        BTNode<T> newNode = createNode(clazzAttr);
        if (!(newNode instanceof AttachmentNode<T> attachmentNode)) {
            throw new IllegalArgumentException("class配置错误, class=" + clazzAttr);
        }
        Iterator<Element> it = attachment.elementIterator();
        while (it.hasNext()) {
            Element property = it.next();
            String phase = property.attributeValue("Phase");
            if (phase != null) {
                switch (phase) {
                    case "Enter":
                        attachmentNode.setPhase(AttachmentNode.PHASE_START);
                        break;
                    case "Update":
                        attachmentNode.setPhase(AttachmentNode.PHASE_UPDATE);
                        break;
                    case "Success":
                        attachmentNode.setPhase(AttachmentNode.PHASE_SUCCESS);
                        break;
                    case "Failure":
                        attachmentNode.setPhase(AttachmentNode.PHASE_FAIL);
                        break;
                    case "Both":
                        if ("PreActionNode".equals(classType))
                            attachmentNode.setPhase(AttachmentNode.PHASE_PRECONDITION);
                        else
                            attachmentNode.setPhase(AttachmentNode.PHASE_POST_EFFECT);
                        break;
                }
            }

            String operator = property.attributeValue("BinaryOperator");
            if (operator != null) {
                PreActionNode.Operator op = PreActionNode.Operator.valueOf(operator);
                attachmentNode.setOperator(op);
            }
        }
        String id = attachment.attributeValue("id");
        attachmentNode.setId(Integer.parseInt(id));
        String traceInfo = attachment.attributeValue("trace");
        attachmentNode.setTraceInfo(traceInfo);
        return attachmentNode;
    }

    private static <T> BTNode<T> loadEventNode(Element attachment) {
        String referenceName = null;
        int triggerType = 0;
        boolean triggerOnce = true;
        int eventKey = 0;
        List<String> paramName = new ArrayList<>();
        for (Element element : attachment.elements()) {
            if ("Params".equals(element.getName())) {
                for (Element param : element.elements()) {
                    paramName.add(param.attributeValue("Name"));
                }
            }
            if (element.attributeValue("ReferenceFilename") != null) {
                referenceName = element.attributeValue("ReferenceFilename");
            }
            if (element.attributeValue("TriggerMode") != null) {
                triggerType = switch (element.attributeValue("TriggerMode")) {
                    case "Transfer" -> TriggerType.TRANSLATE_NODE.type;
                    case "Return" -> TriggerType.RETURN_NODE.type;
                    case "Event" -> TriggerType.EVENT_NODE.type;
                    default -> throw new IllegalArgumentException("不支持的 event TriggerMode 类型");
                };

            }
            if (element.attributeValue("TriggeredOnce") != null) {
                triggerOnce = Boolean.parseBoolean(element.attributeValue("TriggeredOnce"));
            }
            if (element.attributeValue("TaskId") != null) {
                eventKey = Integer.parseInt(element.attributeValue("TaskId"));
            }
        }
        EventNode<T> eventNode = new EventNode<>(referenceName, triggerType, triggerOnce);
        eventNode.setEvent(eventKey);
        eventNode.getParamList().addAll(paramName);
        return eventNode;
    }

    private static <E> BTNode<E> createNode(String impl) {
        BTNode<E> result = null;
        try {
            Class<?> clazz = Class.forName(impl);
            Constructor<?> declaredConstructor = clazz.getDeclaredConstructor();
            Object newInstance = declaredConstructor.newInstance();
            if (newInstance instanceof BTNode) {
                result = (BTNode<E>) newInstance;
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            e.printStackTrace();
        }
        return result;
    }
}
