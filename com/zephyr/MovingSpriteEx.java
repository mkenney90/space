package com.zephyr;

import java.awt.EventQueue;
import javax.swing.JFrame;

public class MovingSpriteEx extends JFrame {

    public MovingSpriteEx() {
        
        initUI();
    }
    
    private void initUI() {

        add(new Board());

        setTitle("Generic Space Shooter 2k19");
        setSize(1200, 800);
        
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {
            MovingSpriteEx ex = new MovingSpriteEx();
            ex.setVisible(true);
        });
    }
}