/**
 * The main class for launching the game
 */

package com.zephyr;

import java.awt.EventQueue;
import javax.swing.JFrame;

public class PlayGame extends JFrame {
    public static final int SCREEN_WIDTH = 1200;
    public static final int SCREEN_HEIGHT = 768;
    Board board;

    public PlayGame() {
        board = new Board(SCREEN_WIDTH, SCREEN_HEIGHT);
        setTitle("Generic Space Shooter 2k19");
        setContentPane(board);
        pack();
        
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            PlayGame pg = new PlayGame();
            pg.setVisible(true);
        });
    }
}