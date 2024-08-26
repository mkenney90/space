package com.zephyr;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.util.HashMap;

import com.zephyr.SpaceShip.ShipState;
import com.zephyr.states.PauseState;
import com.zephyr.states.PlayState;
import com.zephyr.states.StateManager;

public class Controller implements KeyListener {
    private HashMap<Integer, String> controlMap = new HashMap<>();
    private HashMap<String, Integer> keyStates = new HashMap<>();
    private HashMap<String, Integer> prevKeyStates = new HashMap<>();
    private SpaceShip player;
    private StateManager stateManager;

    public Controller(SpaceShip player, StateManager stateManager) {
        this.player = player;
        this.stateManager = stateManager;

        // set default game controls
        controlMap.put(37, "LEFT");
        controlMap.put(39, "RIGHT");
        controlMap.put(38, "UP");
        controlMap.put(40, "DOWN");
        controlMap.put(32, "FIRE");
        controlMap.put(80, "PAUSE");
        controlMap.put(66, "BOSS_DEBUG");
        controlMap.put(77, "DEATH_DEBUG");

        for (String k : controlMap.values()) {
            keyStates.put(k, 0);
            prevKeyStates.put(k, 0);
        }
    }

    public void keyPressed(KeyEvent e) {
        System.out.println(e.getKeyCode());
        String keyIndex = controlMap.get(e.getKeyCode());
        keyStates.put(keyIndex, keyStates.get(keyIndex) + 1);
    }

    public void keyReleased(KeyEvent e) {
        String keyIndex = controlMap.get(e.getKeyCode());
        keyStates.put(keyIndex, 0);
    }

    public void keyTyped(KeyEvent e) {

    }

    public Boolean isPressed(String key) {
        return keyStates.getOrDefault(key, 0) > 0;
    }

    public Boolean isPressedOnce(String key) {
        return keyStates.get(key) == 1 && prevKeyStates.get(key) == 0;
    }

    public void handleInput() {
        if (isPressed("LEFT")) {
            player.setAccelX(-0.2f);
            player.setDirection("left");
        } else if (isPressed("RIGHT")) {
            player.setAccelX(0.2f);
            player.setDirection("right");
        } else {
            player.setAccelX(0);
            player.setDirection("neutral");
        }

        if (isPressed("UP")) {
            player.setAccelY(-0.2f);
        } else if (isPressed("DOWN")) {
            player.setAccelY(0.2f);
        } else {
            player.setAccelY(0);
        }

        if (isPressed("FIRE")) {
            player.fireLaser();
        }

        // if (isPressed(KeyEvent.VK_Z)) {
        // player.setLaserStrength(2);
        // }

        if (isPressedOnce("PAUSE")) {
            if (stateManager.getCurrentState() instanceof PlayState) {
                stateManager.changeState(new PauseState(stateManager.getBoard()));
            } else {
                stateManager.changeState(new PlayState(stateManager.getBoard()));
            }
        }

        if (isPressed("BOSS_DEBUG")) {
            stateManager.getBoard().setGamePhase("boss");
        }
        if (isPressed("DEATH_DEBUG")) {
            stateManager.getBoard().getSpaceShip().setState(ShipState.DYING);
        }

        prevKeyStates = new HashMap<>(keyStates);
    }

    public HashMap<String, Integer> getKeyMap() {
        return keyStates;
    }
}
