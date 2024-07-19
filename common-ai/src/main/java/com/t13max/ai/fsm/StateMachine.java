package com.t13max.ai.fsm;

import com.t13max.ai.Blackboard;

/**
 * 状态机
 *
 * @Author t13max
 * @Date 16:35 2024/7/19
 */
public interface StateMachine<T, S> {

    void startup();

    void stop();

    void update();

    void changeState(S state);

    boolean revertToLastState();

    S getCurrentState();

    void initialState(T owner, S initState);

    void initialState(T owner, S initState, S globalState);

    T getOwner();

    default void addState(EState<T> eState, S state) {
    }

    boolean handleEvent(StateEvent event);

    Blackboard getBlackBoard();
}
