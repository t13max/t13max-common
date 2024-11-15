package com.t13max.ai.fsm;

/**
 * @Author t13max
 * @Date 13:49 2024/5/23
 */
public interface IState<T> {

    void enter(T entity);

    void update(T entity);

    void exit(T entity);

    boolean onEvent(T entity, IStateEvent event);

    default boolean stateSwitch(IState<T> state) {
        return true;
    }
}
