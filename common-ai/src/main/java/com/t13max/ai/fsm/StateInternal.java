package com.t13max.ai.fsm;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @Author t13max
 * @Date 13:49 2024/5/23
 */
public abstract class StateInternal<T> implements State<T, StateParam<T>> {

    private StateInternal<T> parent;

    private Map<EState<T>, State<T, StateParam<T>>> children = new HashMap<>();


    protected boolean globalUpdate(T entity, StateParam<T> param) {
        return true;
    }

    @Override
    public void update(T entity, StateParam<T> param) {
        if (children.isEmpty())
            return;

        if (!globalUpdate(entity, param))
            return;

        if (children.size() > 0 && param.getCurrentSubState() == null) {
            children.values().stream()
                    .findFirst()
                    .ifPresent(param::setCurrentSubState);
        }

        if (param.getCurrentSubState() != null)
            param.getCurrentSubState().update(entity, param);
    }

    @Override
    public void exit(T entity, StateParam<T> param) {
        param.setLastSubState(null);
    }

    @Override
    public boolean onEvent(T entity, StateParam<T> param, StateEvent event) {
        if (event == StateEvent.REVERT_STATE) {
            if (param.getLastSubState() == null) {
                return false;
            }
            if (param.getCurrentSubState() != null)
                param.getCurrentSubState().exit(entity, param);
            State<T, StateParam<T>> temp = param.getCurrentSubState();
            param.setCurrentSubState(param.getLastSubState());
            param.setLastSubState(temp);
            if (param.getCurrentSubState() != null)
                param.getCurrentSubState().enter(entity, param);

            return true;
        }

        return false;
    }

    public void changeInternalState(T entity, StateParam<T> param, EState<T> eState) {
        State<T, StateParam<T>> state = children.get(eState);
        if (state != null) {
            param.changeSubState(entity, state);
        }
    }

    public void changeSubState(T entity, StateParam<T> param, EState<T> eState) {
        getParentState().ifPresent(p -> p.changeInternalState(entity, param, eState));
    }

    protected void addChild(EState<T> eState, StateInternal<T> state) {
        children.put(eState, state);
        state.setParent(this);
    }

    protected Optional<StateInternal<T>> getParentState() {
        return Optional.ofNullable(parent);
    }

    private void setParent(StateInternal<T> state) {
        this.parent = state;

    }
}
