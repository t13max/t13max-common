package com.t13max.ai.fsm;

import java.util.HashMap;
import java.util.Map;

/**
 * 分层有限状态机
 *
 * @Author t13max
 * @Date 13:50 2024/5/23
 */
public class StateMachineHierarchic<T, S extends State<T, StateParam<T>>> extends StateMachineSimple<T, S> {

    private Map<EState<T>, S> topStates = new HashMap<>();

    StateMachineHierarchic() {
        super();
    }

    @Override
    public void addState(EState<T> eState, S state) {
        topStates.put(eState, state);
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    public void changeState(S state) {
        if (state instanceof EState)
            changeState((EState<T>) state);
        else
            throw new IllegalArgumentException("error state type");
    }

    private void changeState(EState<T> eState) {
        S state = topStates.get(eState);
        if (state != null) {
            this.lastState = this.currentState;
            if (this.currentState != null)
                this.currentState.exit(owner, stateParam);
            this.currentState = state;
            this.currentState.enter(owner, stateParam);
        }
    }

    @Override
    public void initialState(T owner, S initState) {
        if (topStates.containsValue(initState)) {
            super.initialState(owner, initState);
        } else {
            throw new IllegalArgumentException("There is no state in the state machine");
        }
    }

    @Override
    public void initialState(T owner, S initState, S globalState) {
        super.initialState(owner, initState, globalState);
    }

    @Override
    public S getCurrentState() {
        return super.getCurrentState();
    }

    @Override
    public boolean revertToLastState() {
        if (this.lastState == null) {
            return false;
        }
        if (this.lastState == currentState)
            return currentState.onEvent(owner, stateParam, StateEvent.REVERT_STATE);

        changeState(lastState);

        return true;
    }

    private boolean isInState(EState<T> eState) {
        return topStates.containsKey(eState);
    }
}
