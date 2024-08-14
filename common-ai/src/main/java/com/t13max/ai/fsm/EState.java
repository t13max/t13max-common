package com.t13max.ai.fsm;

/**
 * @Author t13max
 * @Date 13:49 2024/5/23
 */
public interface EState<T> extends IState<T> {

    @Override
    default void enter(T entity) {
    }

    @Override
    default void update(T entity) {
    }

    @Override
    default void exit(T entity) {
    }

    @Override
    default boolean onEvent(T entity, IStateEvent event) {
        return false;
    }
}
