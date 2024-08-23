package com.zephyr.states;

import java.awt.*;

import com.zephyr.Board;

public class PauseState implements BaseState {
    private Board board;

    public PauseState(Board board) {
        this.board = board;
    }

    @Override
    public void enterState() {

    }

    @Override
    public void updateState() {

    }

    public void render(Graphics g) {
        // Draw pause screen overlay
        Graphics2D g2d = (Graphics2D) g;
        g2d.setPaint(Color.black);
        g2d.fillRect(200, 175, 225, 50);
        g2d.setFont(new Font("Verdana", Font.BOLD, 20));
        g2d.setColor(Color.white);
        g2d.drawString("- PAUSE -", 245, 210);
    }
}
