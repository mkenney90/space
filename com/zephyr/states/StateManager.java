package com.zephyr.states;

import com.zephyr.Board;

public class StateManager {

    public BaseState currentState;
    Board board;

    public StateManager(Board board) {
        this.board = board;
        currentState.enterState(this, board);
    }

    public void changeState(BaseState newState) {
        currentState = newState;
        newState.enterState(this, board);
    }

    public void update() {
        currentState.updateState(this, board);
    }
}
