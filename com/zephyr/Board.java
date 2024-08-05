package com.zephyr;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

public class Board extends JPanel implements ActionListener {
    private Timer timer;
    private int score = 0;
    private SpaceShip spaceShip;
    private final int DELAY = 10;
    Controller controller;
    private List<Star> stars;
    private List<Rock> rocks;
    private List<Particle> particles;
    private Rock lastRock;
    private int rockCooldown = 0;
    private int spaceShipParticleTimer = 0;
    private int lastXValue = 0;
    private int rx = 0;

    public Board() {
        initBoard();
    }

    private void initBoard() {
        setBackground(Color.black);
	    setFocusable(true);

        spaceShip = new SpaceShip(288, 300);
        controller = new Controller(spaceShip);
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

        g2d.setColor(Color.white);
        g2d.setFont(new Font("Fixedsys", Font.BOLD, 30));
        g2d.drawString("" + score, 10, 30);


        g2d.rotate(0);
        g2d.scale(2.0, 2.0);
        AffineTransform at = g2d.getTransform(); // set default transform

        g2d.setPaint(new Color(175, 255, 255, 50));
        g2d.drawLine(300,0,300,600);

        List<Laser> lasers = spaceShip.getLasers();

        for (Laser l : lasers) {
            if (l.strength > 1) {
                g2d.setPaint(new Color(66, 245, 108, 107));
                g2d.drawRect(l.getXInt()-1, l.getYInt()-1,3, l.size + 2);
            }
            g2d.setPaint(Color.red);
            g2d.drawRect(l.getXInt(), l.getYInt(),1, l.size);
        }

        g2d.setPaint(new Color(255, 255, 150));
        for (Star s: stars) {
            g2d.drawLine(s.getXInt(), s.getYInt(), s.getXInt(), s.getYInt()-1);
        }

        for (Rock r: rocks) {
            g2d.setPaint(Color.lightGray);
            g2d.fill(r.getBounds());
            g2d.setPaint(Color.gray);
            g2d.fill(r.getShadowBounds());
            if (r.getDamaged()) {
                g2d.setPaint(Color.black);
                Shape[] dLines = r.getDamageCrackLine();
                for (int i=0;i<dLines.length;i++) {
                    g2d.draw(dLines[i]);
                }
            }
            // g2d.fillOval(r.getXInt(), r.getYInt(), 2, 2);
            if (r.getStrength() > 1) {
                // draw outline on rocks with shield
                g2d.setPaint(Color.yellow);
                if (r.getStrength() > 2) {
                    g2d.setPaint(Color.red);
                }
                g2d.draw(r.getBounds());
            }
        }

        for (Particle p: particles) {
            g2d.setPaint(p.getColor());
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.fillOval((int) p.getX() - p.getSize() / 2, (int) p.getY() - p.getSize() / 2, p.getSize(), p.getSize());
        }

        g2d.rotate(Math.toRadians(spaceShip.getRotation()));
        g2d.drawImage(spaceShip.getImage(), spaceShip.getXInt(), spaceShip.getYInt(), null);

        // reset transforms
        g2d.setTransform(at);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        step();
    }
    
    private void step() {
        // generate star field
        if (Math.random() * 100 < 10) {
            stars.add(new Star((int) Math.round(Math.random() * 600), (int) Math.round(Math.random() * - 40)));
        }
        // spawn rocks
        if (Math.random() * 100 < 2 && rocks.size() < 6 && rockCooldown == 0) {
            while (Math.abs(rx - lastXValue) < 10) {
                rx = 25 + (int) Math.round(Math.random() * 550);
            }
            // all rocks have a minimum of 6 sides
            int points = (int) (Math.random() * 8) + 6;
            lastRock = (!rocks.isEmpty() ? rocks.getLast() : null);
            Rock newRock = new Rock(rx, -10 + (int) Math.round(Math.random() * - 40 ), points);
            rocks.add(newRock);
            if (rocks.size() > 1) {
                //System.out.println(newRock.getDistance(lastRock));
            }
            lastXValue = rx;
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
                0));
        }
        
        // general update functions
        List<Laser> lasers = spaceShip.getLasers();

        for (Laser l: lasers) {
            Rectangle r1 = l.getBounds();
            for (Rock r: rocks) {
                Shape r2 = r.getBounds();

                // check for laser collisions with asteroids
                if (r2.intersects(r1)) {
                    r.takeDamage(l.strength);
                    if (r.getStrength() > 1) {
                        //SoundControl.playSound("shatter.wav");
                        score += 25;
                        // add velocity to rock from impact
                        double impactRadius = r.getDistance(l);
                        if (l.getX() < r.getX()) {
                            r.addSpeed('x', 0.25);
                            r.addRotation(impactRadius * 0.1);
                        } else {
                            r.addSpeed('x', -0.25);
                            r.addRotation(-impactRadius * 0.1);
                        }
                        r.addSpeed('y', -0.12);
                    } else {
                        //SoundControl.playSound("break.wav");
                        score += 100;
                    }
                    l.setVisible(false);
                    r.takeDamage(spaceShip.getLaserStrength());
                    for (int i=0;i<8;i++) {
                        double randSize = 6 + Math.random() * 3;
                        double randAngle = Math.random() * 30;
                        int randOffset = (int) (Math.random() * 10);
                        particles.add(new Particle(r.getXInt() + randOffset, r.getYInt() + randOffset, 360 / 8 * i, randSize, randAngle));
                    }
                }
            }
            l.update();
        }
        lasers.removeIf(l -> (l.getY() < -10 || !l.isVisible()));

        for (Star s: stars) {
            s.move();
        }
        stars.removeIf(s -> s.getY() > 400);

        // collisions between asteroids
        for (Rock r: rocks) {
            for (Rock o: rocks) {
                if (r == o || r.getDistance(o) > r.getRadius() * 2) {
                    continue;
                }
                Area rb = r.getBounds();
                Area ob = o.getBounds();
                
                rb.intersect(ob);
                if (!rb.isEmpty()) {
                    Rectangle cr = rb.getBounds();
                    int crx = cr.x + cr.width / 2;
                    int cry = cr.y + cr.height / 2;
                    for (int i=0;i<5;i++) {
                        particles.add(new Particle(crx, cry, 360 / 5 * i, Color.white, true));
                    }
                    r.handleCollision(o);
                }
            }
            r.move();
        }
        rocks.removeIf(r -> (r.getY() > 400 || !r.isVisible()));

        for (Particle p: particles) {
            p.move();
        }
        particles.removeIf(p -> p.getSize() < 0);

        rockCooldown = Math.max(rockCooldown - 1, 0);

        spaceShip.move();
        spaceShip.tick();
        controller.handleInput();
        repaint();
    }

}