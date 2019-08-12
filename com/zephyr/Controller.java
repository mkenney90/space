package com.zephyr;

import javafx.scene.input.KeyCode;

import javax.swing.JLabel;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.util.HashMap;

public class Controller implements KeyListener {

    private JLabel keyManager;
    private HashMap<Integer, Boolean> keys;
    private SpaceShip player;

    public Controller(SpaceShip player) {

        this.player = player;
        System.out.println("controller initiated");
        keys = new HashMap<>();
    }

    public void keyPressed(KeyEvent e) {
        keys.put(e.getKeyCode(), true);
    }
    public void keyReleased(KeyEvent e) {
        keys.put(e.getKeyCode(), false);
    }
    public void keyTyped(KeyEvent e) {
        
    }
    public boolean isPressed(int key) {
        return keys.getOrDefault(key, false);
    }
    public void handleInput() {
        if (isPressed(KeyEvent.VK_LEFT)) {
            player.setDX(-2);
        } else if (isPressed(KeyEvent.VK_RIGHT)) {
            player.setDX((2));
        } else {
            player.setDX(0);
        }

        if (isPressed(KeyEvent.VK_UP)) {
            player.setDY(-2);
        } else if (isPressed(KeyEvent.VK_DOWN)) {
            player.setDY(2);
        } else {
            player.setDY(0);
        }

        if (isPressed(KeyEvent.VK_SPACE)) {
            player.fireLaser();
        }

        if (isPressed(KeyEvent.VK_Z)) {
            player.setLaserStrength(2);
        }
    }

    public HashMap<Integer, Boolean> getKeyMap() {
        return keys;
    }
}
