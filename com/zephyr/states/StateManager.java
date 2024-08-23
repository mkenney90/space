package com.zephyr.states;

import com.zephyr.Board;

public class StateManager {
    public BaseState currentState;
    Board board;

    public StateManager(Board board) {
        this.board = board;
        this.currentState = new PlayState(board);
        currentState.enterState();
    }

    public void changeState(BaseState newState) {
        this.currentState = newState;
        newState.enterState();
    }

    public void update() {
        if (currentState != null) {
            currentState.updateState();
        }
    }

    public BaseState getCurrentState() {
        return currentState;
    }

    public Board getBoard() {
        return board;
    }
}
