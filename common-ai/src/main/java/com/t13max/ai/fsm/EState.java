package com.t13max.ai.fsm;

/**
 * @Author t13max
 * @Date 13:49 2024/5/23
 */
public interface EState<T> extends State<T, StateParam<T>> {
    @Override
    default void enter(T entity, StateParam<T> param) {
    }

    @Override
    default void update(T entity, StateParam<T> param) {
    }

    @Override
    default void exit(T entity, StateParam<T> param) {
    }

    @Override
    default boolean onEvent(T entity, StateParam<T> param, StateEvent event) {
        return false;
    }
}
