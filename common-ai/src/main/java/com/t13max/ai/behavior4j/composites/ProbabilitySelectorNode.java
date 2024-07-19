package com.t13max.ai.behavior4j.composites;

import com.t13max.ai.behavior4j.BTNode;
import com.t13max.ai.behavior4j.decorators.WeightNode;
import com.t13max.ai.utils.MathUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * 概率选择节点
 * 根据概率“直接”选择并执行某个子节点
 * 无论其返回成功还是失败，概率选择节点也将返回同样的结果。
 *
 * @Author t13max
 * @Date 15:54 2024/5/17
 */
public class ProbabilitySelectorNode<E> extends SingleRunningBranchNode<E> {

    //权重表
    private List<Integer> probabilities = new ArrayList<>();

    public ProbabilitySelectorNode() {
        super();
    }

    public ProbabilitySelectorNode(List<BTNode<E>> nodes) {
        super(nodes);
    }

    @Override
    public boolean start() {
        if (!super.start()) {
            return false;
        }

        return true;
    }

    @Override
    protected void run() {
        //先进行随机选择
        if (!children.isEmpty()) {
            int total = probabilities.stream().reduce(Integer::sum).orElse(0);

            int randWeight = MathUtils.random(0, total);
            for (int i = 0; i < probabilities.size(); i++) {
                if (randWeight < probabilities.get(i)) {
                    //赋值父类的要执行的index字段
                    currentChildIndex = i;
                    break;
                }
                randWeight -= probabilities.get(i);
            }
            //最后调用父类的执行
            super.run();
        }
    }

    @Override
    protected int addChildToNode(BTNode<E> child) {
        if (!(child instanceof WeightNode))
            throw new IllegalArgumentException("概率选择节点 只接受WeightNode子节点");
        int index = super.addChildToNode(child);
        probabilities.add(index, ((WeightNode<E>) child).getWeight());
        return index;
    }

    @Override
    public void childFail(BTNode<E> runningNode) {
        super.childFail(runningNode);
        onFail();
    }

    @Override
    public void childSuccess(BTNode<E> runningNode) {
        super.childSuccess(runningNode);
        onSuccess();
    }

    @Override
    protected void copy(BTNode<E> node) throws InvocationTargetException, IllegalAccessException {
        super.copy(node);
        ProbabilitySelectorNode<E> probabilitySelectorNode = (ProbabilitySelectorNode<E>) node;
        probabilitySelectorNode.probabilities = new ArrayList<>(this.probabilities);
    }
}
