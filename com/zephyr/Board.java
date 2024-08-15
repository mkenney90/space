package com.zephyr;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import com.zephyr.Particle.LaserParticle;
import com.zephyr.src.resources.StateMachine;

public class Board extends JPanel implements ActionListener {

    private Timer timer;
    private int score = 0;
    private SpaceShip spaceShip;
    private final int DELAY = 10;
    Controller controller;
    private List<Star> stars;
    private List<Rock> rocks;
    private List<Particle> particles;
    private int rockCooldown = 0;
    private int spaceShipParticleTimer = 0;
    private int lastXValue = 0;

    private final Color LASER_COLOR = new Color(66, 245, 108, 107);
    private final Color STAR_COLOR = new Color(255, 255, 150);
    private final int UI_FONT_SIZE = 20;
    private final int MAX_SCORE_LENGTH = 5;

    public StateMachine fsm;

    public Board(StateMachine fsm) {
        initBoard(fsm);
    }

    private void initBoard(StateMachine fsm) {
        this.fsm = fsm;
        setBackground(Color.black);
	    setFocusable(true);

        spaceShip = new SpaceShip(288, 300);
        controller = new Controller(spaceShip, fsm);
        addKeyListener(controller);

        SoundControl.playSound("startup.wav");

        stars = new ArrayList<>();
        rocks = new ArrayList<>();
        particles = new ArrayList<>();

        timer = new Timer(DELAY, this);
        timer.start();
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

        g2d.setPaint(new Color(175, 255, 255, 30));
        g2d.drawLine(300,0,300,600);

        List<Laser> lasers = spaceShip.getLasers();

        for (Laser l : lasers) {
            if (l.strength > 1) {
                g2d.setPaint(LASER_COLOR);
                g2d.drawRect(l.getXInt()-1, l.getYInt()-1,3, l.size + 2);
            }
            g2d.setPaint(Color.red);
            g2d.drawRect(l.getXInt(), l.getYInt(),1, l.size);
        }

        g2d.setPaint(STAR_COLOR);
        for (Star s: stars) {
            g2d.drawLine(s.getXInt(), s.getYInt(), s.getXInt(), s.getYInt()-1);
        }

        for (Rock r: rocks) {
            g2d.setPaint(Color.lightGray);
            g2d.fill(r.getBounds());
            g2d.setPaint(Color.gray);
            g2d.fill(r.getShadowBounds());
            if (r.getDamaged()) {
                g2d.setPaint(Color.darkGray);
                Shape[] dLines = r.getDamageCrackLine();
                for (int i=0;i<dLines.length;i++) {
                    g2d.draw(dLines[i]);
                }
            }
            // if (r.getStrength() > 1) {
            //     // draw outline on rocks with shield
            //     g2d.setPaint(Color.yellow);
            //     if (r.getStrength() > 2) {
            //         g2d.setPaint(Color.red);
            //     }
            //     g2d.draw(r.getBounds());
            // }
        }

        for (Particle p: particles) {
            g2d.setPaint(p.getColor());
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.fillOval((int) p.getX() - p.getSize() / 2, (int) p.getY() - p.getSize() / 2, p.getSize(), p.getSize());
        }

        g2d.rotate(Math.toRadians(spaceShip.getRotation()));
        g2d.drawImage(spaceShip.getImage(), spaceShip.getXInt(), spaceShip.getYInt(), null);

        // draw the score text on screen
        g2d.shear(-0.15, 0); // set shear effect
        g2d.setPaint(Color.black);
        g2d.fillRect(0, 0, 100, 30);
        g2d.setFont(new Font("Verdana", Font.BOLD, UI_FONT_SIZE));
        g2d.setColor(Color.white);
        g2d.drawString(String.format("%05d",score), 10, 20);
        g2d.setColor(Color.gray);
        int numZeroes = MAX_SCORE_LENGTH - Integer.toString(score).length();

        if (numZeroes > 0) {
            String scoreFormat = "%0" + numZeroes + "d";
            g2d.drawString(String.format(scoreFormat,0), 10, 20);
        }
        g2d.shear(0.15, 0); // undo shear effect

        if (fsm.currentState == GameState.PAUSED) {
            g2d.setPaint(Color.black);
            g2d.fillRect(200, 175, 225, 50);
            g2d.setFont(new Font("Verdana", Font.BOLD, UI_FONT_SIZE));
            g2d.setColor(Color.white);
            g2d.drawString("- PAUSE -", 245, 210);
        }
        
        // reset transforms
        g2d.setTransform(at);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        step();
    }
    
    private void step() {
        controller.handleInput();
        if (fsm.currentState == GameState.PLAY) {

            // generate star field
            if (Math.random() * 100 < 10) {
                stars.add(new Star((int) (Math.random() * 600), (int) (Math.random() * -40)));
            }

            // spawn rocks
            if (Math.random() * 100 < 2 && rocks.size() < 6 && rockCooldown == 0) {
                int newRx;
                do {
                    newRx = 25 + (int) (Math.random() * 550);
                } while (Math.abs(newRx - lastXValue) < 10);
                // all rocks have a minimum of 8 sides
                int points = (int) (Math.random() * 8) + 8;
                Rock newRock = new Rock(newRx, -10 + (int) Math.round(Math.random() * -40), points);
                rocks.add(newRock);
                lastXValue = newRx;
                rockCooldown = 60;
            }

            // generate ship thruster flame particles
            spaceShipParticleTimer ++;

            if (spaceShipParticleTimer % 2 == 0) {
                particles.add(new Particle(
                    spaceShip.getXInt() + spaceShip.getWidth()/2, 
                    spaceShip.getYInt() + 21, 
                    0.5, 
                    90, 
                    6, 
                    0.99, 
                    true, 
                    Color.yellow, 
                    1.0f, 
                    0)
                );
            }
            
            // general update functions
            List<Laser> lasers = spaceShip.getLasers();

            for (Laser l: lasers) {
                Rectangle laserBounds = l.getBounds();
                for (Rock r: rocks) {
                    Shape rockBounds = r.getBounds();

                    // check for laser collisions with asteroids
                    if (rockBounds.intersects(laserBounds)) {
                        r.takeDamage(l.strength);
                        if (r.getStrength() > 1) {
                            //SoundControl.playSound("shatter.wav");
                            score += 25;
                            // add velocity to rock from impact
                            double impactRadius = r.getDistance(l);
                            r.addSpeed('x', l.getX() < r.getX() ? 0.25 : -0.25);
                            r.addRotation((l.getX() < r.getX() ? 1 : -1) * impactRadius * 0.1);
                            r.addSpeed('y', -0.12);
                        } else {
                            //SoundControl.playSound("break.wav");
                            for (int i=0;i<8;i++) {
                                double randSize = 6 + Math.random() * 3;
                                float randAngle = (float) Math.random() * 30f;
                                int randOffset = (int) (Math.random() * 10);
                                particles.add(new Particle(r.getXInt() + randOffset, r.getYInt() + randOffset, 360 / 8 * i, randSize, randAngle));
                            }
                            score += 100;
                        }
                        l.setVisible(false);
                        r.takeDamage(spaceShip.getLaserStrength());
                        particles.add(new LaserParticle(l.getXInt(), l.getYInt(), 0));
                    
                    }
                }
                l.update();
            }
            lasers.removeIf(l -> (l.getY() < -10 || !l.isVisible()));

            stars.forEach(Star::move);
            stars.removeIf(s -> s.getY() > 400);

            // collisions between asteroids
            for (int i = 0; i < rocks.size(); i++) {
                Rock r = rocks.get(i);
                for (int j = i + 1; j < rocks.size(); j++) {
                    Rock o = rocks.get(j);
                    if (r.getDistance(o) <= r.getRadius() * 2) {
                        Area rb = r.getBounds();
                        Area ob = o.getBounds();
                        rb.intersect(ob);
                        if (!rb.isEmpty()) {
                            Rectangle cr = rb.getBounds();
                            int crx = cr.x + cr.width / 2;
                            int cry = cr.y + cr.height / 2;
                            for (int k = 0; k < 5; k++) {
                                particles.add(new Particle(crx, cry, 360 / 5 * k, Color.white, true));
                            }
                            r.handleCollision(o);
                        }
                    }
                }
                r.move();
            }
            rocks.removeIf(r -> (r.getY() > 400 || !r.isVisible()));

            particles.forEach(Particle::move);
            particles.removeIf(p -> p.getSize() < 0 || !p.isVisible());

            rockCooldown = Math.max(rockCooldown - 1, 0);

            spaceShip.move();
            spaceShip.tick();
        }
        repaint();
    }
}