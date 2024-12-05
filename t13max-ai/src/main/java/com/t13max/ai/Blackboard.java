package com.t13max.ai;


import com.t13max.util.func.Applier;

import java.util.*;
import java.util.function.BiFunction;

/**
 * ai数据黑板，共享行为数据，数据结果不可控
 *
 * @Author t13max
 * @Date 13:50 2024/5/23
 */
public class Blackboard {

    private boolean isUpdating;

    private final Map<String, BlackboardValue> valueMap = new HashMap<>();

    private final Set<String> removeValues = new HashSet<>();

    private final Map<String, BlackboardValue> addValueMap = new HashMap<>();

    public void update() {

        this.isUpdating = true;
        if (!valueMap.isEmpty()) {
            valueMap.forEach((key, value) -> {
                value.update();

                if (value.isExpired())
                    removeValues.add(key);
            });
        }

        this.isUpdating = false;
        if (!addValueMap.isEmpty()) {
            valueMap.putAll(addValueMap);
            addValueMap.clear();
        }
        if (!removeValues.isEmpty()) {
            removeValues.forEach(valueMap::remove);
            removeValues.clear();
        }

    }

    public void put(String key, Object value) {
        if (isUpdating)
            addValueMap.put(key, new BlackboardValue.SnapBoardValue(value));
        else
            valueMap.put(key, new BlackboardValue.SnapBoardValue(value));
    }

    public void put(String key, Object value, int millis) {
        if (isUpdating)
            addValueMap.put(key, new BlackboardValue.TimeBlackBoardValue(value, millis));
        else
            valueMap.put(key, new BlackboardValue.TimeBlackBoardValue(value, millis));
    }

    public void putApplyIfAbsent(String key, Applier value, int millis) {
        if (isUpdating)
            addValueMap.putIfAbsent(key, new BlackboardValue.FunctionalValue(value, millis));
        else
            valueMap.putIfAbsent(key, new BlackboardValue.FunctionalValue(value, millis));
    }

    public Optional<Object> getValue(String key) {
        if (getBlackboardValue(key).isPresent())
            return Optional.ofNullable(getBlackboardValue(key).get().getValue());
        return Optional.empty();
    }

    public Optional<Integer> getIntValue(String key) {
        if (getBlackboardValue(key).isPresent())
            return Optional.ofNullable((Integer) getBlackboardValue(key).get().getValue());
        return Optional.empty();
    }

    public Optional<Long> getLongValue(String key) {
        if (getBlackboardValue(key).isPresent())
            return Optional.ofNullable((Long) getBlackboardValue(key).get().getValue());
        return Optional.empty();
    }

    public Optional<Float> getFloatValue(String key) {
        if (getBlackboardValue(key).isPresent())
            return Optional.ofNullable((Float) getBlackboardValue(key).get().getValue());
        return Optional.empty();
    }

    public void computeIfPresent(String key, BiFunction<String, Object, Object> remappingFunction) {
        getBlackboardValue(key).ifPresent(v -> {
            Object newValue = remappingFunction.apply(key, v.getValue());
            v.setValue(newValue);
        });
    }

    public boolean remove(String key) {
        if (isUpdating) {
            if (valueMap.containsKey(key))
                return removeValues.add(key);
            else
                return false;
        } else
            return valueMap.remove(key) != null;
    }

    public void clear() {

        this.valueMap.clear();
    }

    public <V> void merge(String key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        BlackboardValue blackboardValue = this.valueMap.get(key);
        if (blackboardValue == null) {
            put(key, value);
            return;
        }
        V oldValue = (V) blackboardValue.getValue();
        V newValue = (oldValue == null) ? value :
                remappingFunction.apply(oldValue, value);
        if (newValue == null) {
            remove(key);
        } else {
            put(key, newValue);
        }
    }

    private Optional<BlackboardValue> getBlackboardValue(String key) {
        BlackboardValue blackboardValue = valueMap.get(key);
        if (blackboardValue == null) {
            return Optional.empty();
        }
        return Optional.of(blackboardValue);
    }
}
