package com.zephyr;

import java.awt.EventQueue;
import javax.swing.JFrame;

import com.zephyr.states.StateManager;

public class Main extends JFrame {
    public static final int SCREEN_WIDTH = 1200;
    public static final int SCREEN_HEIGHT = 768;
    Board gameBoard;

    public Main() {
        initGameWindow();
    }
    
    private void initGameWindow() {
        Board gameBoard = new Board(SCREEN_WIDTH, SCREEN_HEIGHT);
        setTitle("Generic Space Shooter 2k19");
        setContentPane(gameBoard);
        pack();
        
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            Main ex = new Main();
            ex.setVisible(true);
        });
    }
}