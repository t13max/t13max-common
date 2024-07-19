package com.t13max.ai.fsm;

/**
 * @Author t13max
 * @Date 13:49 2024/5/23
 */
public interface State<T, P> {

    void enter(T entity, P param);

    void update(T entity, P param);

    void exit(T entity, P param);

    boolean onEvent(T entity, P param, StateEvent event);

    default boolean stateSwitch(State<T, P> state) {
        return true;
    }
}
