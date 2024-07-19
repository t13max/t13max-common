package com.t13max.ai.fsm;

/**
 * @Author t13max
 * @Date 13:50 2024/5/23
 */
public class StateParam<T> {

    private State<T, StateParam<T>> currentSubState;

    private State<T, StateParam<T>> lastSubState;

    void changeSubState(T entity, State<T, StateParam<T>> state) {
        if (this.currentSubState == state)
            return;

        this.lastSubState = this.currentSubState;
        if (this.currentSubState != null)
            this.currentSubState.exit(entity, this);
        state.enter(entity, this);
        this.currentSubState = state;
    }

    State<T, StateParam<T>> getCurrentSubState() {
        return currentSubState;
    }

    void setCurrentSubState(State<T, StateParam<T>> currentSubState) {
        this.currentSubState = currentSubState;
    }

    State<T, StateParam<T>> getLastSubState() {
        return lastSubState;
    }

    void setLastSubState(State<T, StateParam<T>> lastSubState) {
        this.lastSubState = lastSubState;
    }
}
