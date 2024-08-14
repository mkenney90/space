package com.zephyr;

import java.awt.EventQueue;
import javax.swing.JFrame;

import com.zephyr.src.resources.StateMachine;

public class Main extends JFrame {
    public static final int SCREEN_WIDTH = 1200;
    public static final int SCREEN_HEIGHT = 800;
    public StateMachine fsm;

    public Main() {
        fsm = new StateMachine();
        initUI();
    }
    
    private void initUI() {
        add(new Board(fsm));

        setTitle("Generic Space Shooter 2k19");
        setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        
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