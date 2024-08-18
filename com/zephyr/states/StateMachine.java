package com.zephyr.states;

import com.zephyr.GameState;

public class StateMachine {

    public GameState currentState;

    public StateMachine() {
        this.currentState = GameState.PLAY;
    }

    public void setState(GameState newState) {
        currentState = newState;
    }
}
