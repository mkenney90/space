package com.zephyr.states;

import java.awt.Graphics;

import com.zephyr.Board;

public interface BaseState {
    void enterState();

    void updateState();

    void render(Graphics g);

}
