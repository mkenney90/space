package com.zephyr;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;

import com.zephyr.SpaceShip.ShipState;
import com.zephyr.enemies.BaseEnemy;
import com.zephyr.states.StateManager;

public class Board extends JPanel implements Runnable {
    private int score = 0;
    private int gameLevel = 1;
    private SpaceShip spaceShip;
    private int ships = 3;
    private Image shipIcon;
    private final int DELAY = 13;
    Controller controller;
    private List<Star> stars;
    private List<Rock> rocks;
    private List<Particle> particles;
    private List<BaseEnemy> enemies;
    private int rockCooldown = 0;
    private int spaceShipParticleTimer = 0;
    private int lastXValue = 0;
    private String gamePhase = "normal";

    private final Color LASER_COLOR_PRIMARY = Color.red;
    private final Color LASER_COLOR_SECONDARY = new Color(66, 245, 108, 107);
    private final Color STAR_COLOR = new Color(255, 255, 150);
    private final Color ROCK_COLOR = Color.lightGray;
    private final Color SHADOW_COLOR = Color.gray;
    private final int UI_FONT_SIZE = 20;
    private final int MAX_SCORE_LENGTH = 5;

    public StateManager stateManager;

    public Board(int width, int height) {
        super();
        initBoard(width, height);
        Thread thread = new Thread(this);
        thread.start();
    }

    public void run() {
        while (true) {
            try {
                Thread.sleep(DELAY);
            } catch (Exception e) {
                System.out.println(e);
            }
            step();
        }
    }

    private void initBoard(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        setFocusable(true);
        setBackground(Color.black);

        spaceShip = new SpaceShip(288, 300);
        try {
            shipIcon = ImageIO.read(getClass().getResourceAsStream("\\src\\resources\\images\\spaceship.png"));
        } catch (Exception e) {
            System.out.println("Error loading icon file");
        }
        stateManager = new StateManager(this);
        controller = new Controller(spaceShip, stateManager);
        addKeyListener(controller);

        SoundControl.playSound("startup.wav");

        stars = new ArrayList<>();
        rocks = new ArrayList<>();
        particles = new ArrayList<>();
        enemies = new ArrayList<>();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
        Toolkit.getDefaultToolkit().sync();
    }

    private void doDrawing(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform at = g2d.getTransform(); // get default transform

        g2d.scale(2.0, 2.0);

        g2d.setColor(new Color(175, 255, 255, 30));
        g2d.drawLine(300, 0, 300, 340);

        List<Laser> lasers = spaceShip.getLasers();

        for (Laser l : lasers) {
            if (l.strength > 1) {
                g2d.setColor(LASER_COLOR_SECONDARY);
                g2d.drawRect(l.getXInt() - 1, l.getYInt() - 1, 3, l.size + 2);
            }
            g2d.setColor(LASER_COLOR_PRIMARY);
            g2d.drawRect(l.getXInt(), l.getYInt(), 1, l.size);
        }

        g2d.setColor(STAR_COLOR);
        for (Star s : stars) {
            g2d.drawLine(s.getXInt(), s.getYInt(), s.getXInt(), s.getYInt() - 1);
        }

        for (Rock r : rocks) {
            g2d.setColor(ROCK_COLOR);
            g2d.fill(r.getBounds());
            g2d.setColor(SHADOW_COLOR);
            g2d.fill(r.getShadowBounds());
            if (r.getDamaged()) {
                g2d.setColor(Color.darkGray);
                Shape[] dLines = r.getDamageCrackLines();
                for (int i = 0; i < dLines.length; i++) {
                    g2d.draw(dLines[i]);
                }
            }
            // if (r.getStrength() > 1) {
            // // draw outline on rocks with shield
            // g2d.setColor(Color.yellow);
            // if (r.getStrength() > 2) {
            // g2d.setColor(Color.red);
            // }
            // g2d.draw(r.getBounds());
            // }
        }

        for (Particle p : particles) {
            g2d.setColor(p.getColor());
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.fillOval((int) p.getX() - p.getSize() / 2, (int) p.getY() - p.getSize() / 2, p.getSize(), p.getSize());
        }

        for (BaseEnemy e: enemies) {
            e.render(g);
        }

        if (spaceShip.visible) {
            g2d.rotate(Math.toRadians(spaceShip.getRotation()), spaceShip.getX()+spaceShip.getWidth()/2, spaceShip.getY()+spaceShip.getHeight()/2);
            if (spaceShip.getState() == ShipState.NORMAL || spaceShip.getIFramesTimer() % 15 <= 8) {
                g2d.drawImage(spaceShip.getImage(), spaceShip.getXInt(), spaceShip.getYInt(), null);
            }
            g2d.rotate(-Math.toRadians(spaceShip.getRotation()), spaceShip.getX()+spaceShip.getWidth()/2, spaceShip.getY()+spaceShip.getHeight()/2);
        }
        // g2d.drawRect(spaceShip.getXInt(), spaceShip.getYInt(), spaceShip.getWidth(),
        // spaceShip.getHeight());

        // draw the score text on screen
        g2d.shear(-0.15, 0); // set shear effect
        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, 600, 30);
        g2d.setFont(new Font("Verdana", Font.BOLD, UI_FONT_SIZE));
        g2d.setColor(Color.white);
        g2d.drawString(String.format("%05d", score), 10, 20);
        g2d.setColor(Color.gray);
        int numZeroes = MAX_SCORE_LENGTH - Integer.toString(score).length();

        if (numZeroes > 0) {
            String scoreFormat = "%0" + numZeroes + "d";
            g2d.drawString(String.format(scoreFormat, 0), 10, 20);
        }
        g2d.scale(0.5,0.5);
        for (int i=0;i<ships;i++) {
            g2d.drawImage(spaceShip.getImage(), 24 + (i * 32), 45, null);
            // g2d.setColor(Color.red);
            // g2d.fillOval(15 + (i * 12), 25, 8, 8);
        }
        g2d.shear(0.15, 0); // undo shear effect

        stateManager.getCurrentState().render(g);

        // reset transforms
        g2d.setTransform(at);
    }

    private void step() {
        controller.handleInput();
        stateManager.update();
        gameLevel = score / 500;
        repaint();
    }

    public Controller getController() {
        return controller;
    }
    public List<Star> getStars() {
        return stars;
    }

    public List<Rock> getRocks() {
        return rocks;
    }

    public List<Particle> getParticles() {
        return particles;
    }
    
    public List<BaseEnemy> getEnemies() {
        return enemies;
    }

    public SpaceShip getSpaceShip() {
        return spaceShip;
    }

    public int getRockCooldown() {
        return rockCooldown;
    }

    public void setRockCooldown(int rockCooldown) {
        this.rockCooldown = rockCooldown;
    }

    public int getSpaceShipParticleTimer() {
        return spaceShipParticleTimer;
    }

    public void incrementSpaceShipParticleTimer() {
        this.spaceShipParticleTimer++;
    }

    public void setLastXValue(int lastXValue) {
        this.lastXValue = lastXValue;
    }

    public int getLastXValue() {
        return lastXValue;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int newScore) {
        this.score = newScore;
    }

    public int getGameLevel() {
        return gameLevel;
    }

    public void addScore(int points) {
        this.score += points;
    }

    public String getGamePhase() {
        return gamePhase;
    }

    public void setGamePhase(String newPhase) {
        this.gamePhase = newPhase;
    }

}