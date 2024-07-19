package com.t13max.ai;


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

    private Map<String, BlackboardParam> paramMap = new HashMap<>();

    private Set<String> removeParams = new HashSet<>();

    private Map<String, BlackboardParam> addParams = new HashMap<>();

    public void update() {

        this.isUpdating = true;
        if (!paramMap.isEmpty()) {
            paramMap.forEach((key, value) -> {
                value.update();

                if (value.isExpired())
                    removeParams.add(key);
            });
        }

        this.isUpdating = false;
        if (!addParams.isEmpty()) {
            addParams.forEach(paramMap::put);
            addParams.clear();
        }
        if (!removeParams.isEmpty()) {
            removeParams.forEach(paramMap::remove);
            removeParams.clear();
        }

    }

    public void set(String key, Object value) {
        if (isUpdating)
            addParams.put(key, new BlackboardParam.SnapBoardParam(value));
        else
            paramMap.put(key, new BlackboardParam.SnapBoardParam(value));
    }

    public void set(String key, Object value, int millis) {
        if (isUpdating)
            addParams.put(key, new BlackboardParam.TimeBlackBoardParam(value, millis));
        else
            paramMap.put(key, new BlackboardParam.TimeBlackBoardParam(value, millis));
    }

    public void setApplyIfAbsent(String key, Applier value, int millis) {
        if (isUpdating)
            addParams.putIfAbsent(key, new BlackboardParam.FunctionalParam(value, millis));
        else
            paramMap.putIfAbsent(key, new BlackboardParam.FunctionalParam(value, millis));
    }

    public Optional<Object> getValue(String key) {
        if (getParam(key).isPresent())
            return Optional.ofNullable(getParam(key).get().getValue());
        return Optional.empty();
    }

    public Optional<Integer> getIntValue(String key) {
        if (getParam(key).isPresent())
            return Optional.ofNullable((Integer) getParam(key).get().getValue());
        return Optional.empty();
    }

    public Optional<Long> getLongValue(String key) {
        if (getParam(key).isPresent())
            return Optional.ofNullable((Long) getParam(key).get().getValue());
        return Optional.empty();
    }

    public Optional<Float> getFloatValue(String key) {
        if (getParam(key).isPresent())
            return Optional.ofNullable((Float) getParam(key).get().getValue());
        return Optional.empty();
    }

    public void computeIfPresent(String key, BiFunction<String, Object, Object> remappingFunction) {
        getParam(key).ifPresent(v -> {
            Object newValue = remappingFunction.apply(key, v.getValue());
            v.setValue(newValue);
        });
    }

    public boolean remove(String key) {
        if (isUpdating) {
            if (paramMap.containsKey(key))
                return removeParams.add(key);
            else
                return false;
        } else
            return paramMap.remove(key) != null;
    }

    public void clear() {

        this.paramMap.clear();
    }

    private Optional<BlackboardParam> getParam(String key) {
        BlackboardParam blackboardParam = paramMap.get(key);
        if (blackboardParam == null) {
            return Optional.empty();
        }
        return Optional.of(blackboardParam);
    }
}
