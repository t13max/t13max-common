package com.t13max.ai.fsm;

/**
 * 简单实现
 *
 * @Author t13max
 * @Date 13:50 2024/5/23
 */
public class SimpleStateMachine<T, S extends IState<T>> extends DefaultStateMachine<T, S> {

    public SimpleStateMachine() {
        super();
    }

    public SimpleStateMachine(T owner, S initState) {
        super(owner, initState);
    }

    public SimpleStateMachine(T owner, S initState, S globalState) {
        super(owner, initState, globalState);
    }
}
