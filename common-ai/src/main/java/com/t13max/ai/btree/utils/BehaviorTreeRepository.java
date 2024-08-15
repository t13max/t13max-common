package com.t13max.ai.btree.utils;

import com.t13max.ai.btree.BehaviorTree;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author t13max
 * @Date 13:49 2024/5/23
 */
public class BehaviorTreeRepository {

    private final Map<String, BehaviorTree<?>> behaviorTreeMap;

    public BehaviorTreeRepository() {
        this(512);
    }

    public BehaviorTreeRepository(int initialCapacity) {
        behaviorTreeMap = new ConcurrentHashMap<>(initialCapacity);
    }

    public void registerTree(String treeName, BehaviorTree<?> archetypeTree) {
        if (archetypeTree == null) {
            throw new IllegalArgumentException("The registered archetype must not be null.");
        }
        behaviorTreeMap.put(treeName, archetypeTree);
    }

    public boolean contain(String btName) {

        return behaviorTreeMap.containsKey(btName);
    }

    @SuppressWarnings("unchecked")
    protected <T> BehaviorTree<T> getBehaviorTree(String treeReference) {

        return (BehaviorTree<T>) behaviorTreeMap.get(treeReference);
    }

    Map<String, BehaviorTree<?>> getRepository() {
        return this.behaviorTreeMap;
    }


    public void removeTrees(String treeName) {
        behaviorTreeMap.remove(treeName);
    }
}
