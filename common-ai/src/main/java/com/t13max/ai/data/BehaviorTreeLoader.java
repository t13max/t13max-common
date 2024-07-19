package com.t13max.ai.data;

import com.t13max.ai.behavior4j.BTNode;
import com.t13max.ai.behavior4j.attachments.AttachmentNode;
import com.t13max.ai.behavior4j.attachments.PreActionNode;
import com.t13max.ai.behavior4j.composites.*;
import com.t13max.ai.behavior4j.decorators.*;
import com.t13max.ai.behavior4j.event.EventNode;
import com.t13max.ai.behavior4j.event.TriggerType;
import com.t13max.ai.behavior4j.leaf.EndNode;
import com.t13max.ai.behavior4j.leaf.FailureNode;
import com.t13max.ai.behavior4j.leaf.NoopNode;
import com.t13max.ai.behavior4j.leaf.SuccessNode;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @Author t13max
 * @Date 13:49 2024/5/23
 */
public class BehaviorTreeLoader {
    private static final String ATTR_CLASS = "class";

    static <T> List<BTNode<T>> loadElement(Element element, ScriptDefine script) {
        List<BTNode<T>> nodeList = new ArrayList<>();
        Iterator<Element> it = element.elementIterator();

        while (it.hasNext()) {
            Element subElement = it.next();
            if ("node".equals(subElement.getName())) {
                BTNode<T> subNode = loadNode(subElement, script);
                nodeList.add(subNode);
            } else if ("attachment".equals(subElement.getName())) {
                // event attachment
                if (EventNode.exportClass().equals(subElement.attributeValue(ATTR_CLASS))) {
                    BTNode<T> subNode = loadEventNode(subElement);
                    nodeList.add(subNode);
                } else {
                    BTNode<T> subNode = loadAttachment(subElement, script);
                    nodeList.add(subNode);
                }
            }
        }

        return nodeList;
    }

    private static <T> BTNode<T> loadCustomConnector(Element element, ScriptDefine script) {
        Iterator<Element> it = element.elementIterator();
        while (it.hasNext()) {
            Element subElement = it.next();
            if ("custom".equals(subElement.getName())) {
                List<BTNode<T>> nodeList = loadElement(subElement, script);
                if (!nodeList.isEmpty())
                    return nodeList.get(0);
            }
        }

        return null;
    }

    private static <T> BTNode<T> loadNode(Element nodeElement, ScriptDefine script) {
        String classType = nodeElement.attributeValue(ATTR_CLASS);

        List<BTNode<T>> nodes = loadElement(nodeElement, script);
        BTNode<T> node;
        switch (classType) {
            case "Parallel":
                String policy = nodeElement.attributeValue("Policy");
                String coordinator = nodeElement.attributeValue("Coordinator");
                node = new ParallelNode<>(ParallelNode.Policy.valueOf(policy), ParallelNode.Coordinator.valueOf(coordinator));
                break;
            case "Sequence": {
                node = new SequenceNode<>();
                BTNode<T> condition = loadCustomConnector(nodeElement, script);
                if (condition != null)
                    ((SingleRunningBranchNode<T>) node).setCondition(condition);
                break;
            }
            case "RandomSequence": {
                node = new RandomSequenceNode<>();
                BTNode<T> condition = loadCustomConnector(nodeElement, script);
                if (condition != null)
                    ((SingleRunningBranchNode<T>) node).setCondition(condition);
                break;
            }
            case "Selector": {
                node = new SelectorNode<>();
                BTNode<T> condition = loadCustomConnector(nodeElement, script);
                if (condition != null)
                    ((SingleRunningBranchNode<T>) node).setCondition(condition);
                break;
            }
            case "RandomSelector": {
                node = new RandomSelectorNode<>();
                BTNode<T> condition = loadCustomConnector(nodeElement, script);
                if (condition != null)
                    ((SingleRunningBranchNode<T>) node).setCondition(condition);
                break;
            }
            case "ProbabilitySelector": {
                node = new ProbabilitySelectorNode<>();
                BTNode<T> condition = loadCustomConnector(nodeElement, script);
                if (condition != null)
                    ((SingleRunningBranchNode<T>) node).setCondition(condition);
                break;
            }
            case "CaseSelectorNode": {
                node = script.getNode(Integer.parseInt(nodeElement.attributeValue("id")));
                BTNode<T> condition = loadCustomConnector(nodeElement, script);
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
                String count = nodeElement.attributeValue("Count");
                String isFrame = nodeElement.attributeValue("DoneWithinFrame");
                node = new RepeatNode<>(Integer.parseInt(count), Boolean.parseBoolean(isFrame));
                break;
            }
            case "Not":
                node = new RevertNode<>();
                break;
            case "UntilFailure":
                node = new UntilFailureNode<>();
                break;
            case "UntilSuccess":
                node = new UntilSuccessNode<>();
                break;
            case "AssignmentNode":
            case "ActionNode":
                node = script.getNode(Integer.parseInt(nodeElement.attributeValue("id")));
                break;
            case "Noop":
                node = new NoopNode<>();
                break;
            case "End":
                String endOutside = nodeElement.attributeValue("EndOutside");
                String endStatus = nodeElement.attributeValue("EndStatus");
                node = new EndNode<>(endStatus, Boolean.parseBoolean(endOutside));
                break;
            case "FailureNode":
                node = new FailureNode<>();
                break;
            case "Success":
                node = new SuccessNode<>();
                break;
            case "Log":
                node = new LogNode<>();
                break;
            case "DecoratorWeight":
                String weight = nodeElement.attributeValue("Weight");
                node = new WeightNode<>(Integer.parseInt(weight));
                break;
            case "CaseNode":
                String caseValue = nodeElement.attributeValue("CaseValue");
                node = new CaseNode<>(caseValue.replace("\"", ""));
                break;
            case "ReferencedBehavior":
                String subTreeName = nodeElement.attributeValue("ReferenceBehavior");
                node = new ReferenceNode<>(subTreeName.replace("\"", ""));
                break;
            case "ConditionNode": {
                node = script.getNode(Integer.parseInt(nodeElement.attributeValue("id")));
                break;
            }
            case "And":
                node = new AndNode<>();
                break;
            case "Or":
                node = new OrNode<>();
                break;
            case "DecoratorCountLimit": {
                String limitCount = nodeElement.attributeValue("Count");
                node = new CountLimitNode<>(Integer.parseInt(limitCount));
                BTNode<T> condition = loadCustomConnector(nodeElement, script);
                if (condition != null)
                    ((CountLimitNode<T>) node).setCondition(condition);
                break;
            }
            case "DecoratorLoopUntil": {
                String count = nodeElement.attributeValue("Count");
                node = new LoopUntilNode<>(Integer.parseInt(count));
                break;
            }
            case "Task": {
                node = new TaskNode<>();
                break;
            }
            default:
                throw new IllegalArgumentException("can't support node type : " + classType);
        }
        if (node != null) {
            nodes.forEach(subNode -> {
                if (subNode instanceof AttachmentNode) {
                    node.addAttachment((AttachmentNode<T>) subNode);
                } else if (subNode instanceof EventNode) {
                    EventNode<T> eventNode = (EventNode<T>) subNode;
                    node.addEvent(eventNode.getEvent(), eventNode);
                } else {
                    node.addChild(subNode);
                }
            });
        }
        if (node != null) {
            String id = nodeElement.attributeValue("id");
            node.setId(Integer.parseInt(id));
            String traceInfo = nodeElement.attributeValue("trace");
            node.setTraceInfo(traceInfo);
        }
        return node;
    }

    private static <T> BTNode<T> loadAttachment(Element attachment, ScriptDefine script) {
        String classType = attachment.attributeValue(ATTR_CLASS);

        AttachmentNode<T> attachmentNode = (AttachmentNode<T>) script.getNode(Integer.parseInt(attachment.attributeValue("id")));
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
        if (attachmentNode != null) {
            String id = attachment.attributeValue("id");
            attachmentNode.setId(Integer.parseInt(id));
            String traceInfo = attachment.attributeValue("trace");
            attachmentNode.setTraceInfo(traceInfo);
        }
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
                switch (element.attributeValue("TriggerMode")) {
                    case "Transfer":
                        triggerType = TriggerType.translateNode.type;
                        break;
                    case "Return":
                        triggerType = TriggerType.returnNode.type;
                        break;

                    case "Event":
                        triggerType = TriggerType.eventNode.type;
                        break;

                    default:
                        throw new IllegalArgumentException("不支持的 event TriggerMode 类型");
                }

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
}
