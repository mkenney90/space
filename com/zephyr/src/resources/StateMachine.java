package com.zephyr.src.resources;

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
