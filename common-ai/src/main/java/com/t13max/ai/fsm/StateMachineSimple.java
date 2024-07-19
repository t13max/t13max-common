package com.t13max.ai.fsm;

/**
 * 默认实现有限状态机
 *
 * @Author t13max
 * @Date 13:50 2024/5/23
 */
public class StateMachineSimple<T, S extends State<T, StateParam<T>>> extends StateMachineDefault<T, S, StateParam<T>> {
    public StateMachineSimple() {
        super();
        stateParam = new StateParam<>();
    }

    public StateMachineSimple(T owner, S initState) {
        super(owner, initState);
        stateParam = new StateParam<>();
    }

    public StateMachineSimple(T owner, S initState, S globalState) {
        super(owner, initState, globalState);
        stateParam = new StateParam<>();
    }
}
