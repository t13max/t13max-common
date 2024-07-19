package com.t13max.ai.fsm;

/**
 * @Author t13max
 * @Date 13:49 2024/5/23
 */
public class StateMachineBuilder<T, S extends State<T, StateParam<T>>> {
    private StateMachine<T, S> stateMachine;

    private static final int TYPE_HFSM = 1;

    private StateMachineBuilder(int type) {
        if (type == TYPE_HFSM)
            this.stateMachine = new StateMachineHierarchic<T, S>();
    }

    public static <T, S extends State<T, StateParam<T>>> StateMachineBuilder<T, S> HFSMBuilder() {
        return new StateMachineBuilder<>(TYPE_HFSM);
    }

    public StateMachineBuilder<T, S> addState(EState<T> eState, S state) {
        stateMachine.addState(eState, state);

        return this;
    }

    public StateMachine<T, S> build(T owner, S initState) {
        stateMachine.initialState(owner, initState);

        return stateMachine;
    }

    public StateMachine<T, S> build(T owner, S initState, S globalState) {
        stateMachine.initialState(owner, initState, globalState);

        return stateMachine;
    }
}
