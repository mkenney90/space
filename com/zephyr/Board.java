package com.zephyr;

import javafx.scene.shape.Circle;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;


public class Board extends JPanel implements ActionListener {

    private Timer timer;
    private SpaceShip spaceShip;
    private final int DELAY = 10;
    Controller cont;
    private List<Star> stars;
    private List<Rock> rocks;
    private List<RockParticle> rockparts;
    private int rockCooldown = 0;
    private int lastXValue = 0;
    private int rx = 0;

    public Board() {

        initBoard();
    }

    private void initBoard() {

        setBackground(Color.black);
	    setFocusable(true);

        spaceShip = new SpaceShip(292, 300);
        cont = new Controller(spaceShip);
        addKeyListener(cont);

        stars = new ArrayList<>();
        rocks = new ArrayList<>();
        rockparts = new ArrayList<>();

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

        g2d.rotate(0);
        g2d.scale(2.0, 2.0);
        AffineTransform at = g2d.getTransform();

        g2d.setPaint(new Color(175, 255, 255, 30));
        g2d.drawLine(300,0,300,600);

        List<Laser> lasers = spaceShip.getLasers();

        for (Laser l : lasers) {
            if (l.strength > 1) {
                g2d.setPaint(new Color(66, 245, 108, 107));
                g2d.drawRect(l.getX()-1, l.getY()-1,3, l.size + 2);
            }
            g2d.setPaint(Color.RED);
            g2d.drawRect(l.getX(), l.getY(),1, l.size);
        }

        g2d.setPaint(new Color(255, 255, 150));
        for (Star s: stars) {
            g2d.drawLine(s.getX(), s.getY(), s.getX(), s.getY());
        }

        for (Rock r: rocks) {
            if (r.strength > 1) {
                g2d.setPaint(Color.ORANGE);
                g2d.setStroke(new BasicStroke(3));
                Rectangle r1 = new Rectangle(r.getX(), r.getY(), r.getWidth(), r.getHeight());
                AffineTransform ar = AffineTransform.getRotateInstance(Math.toRadians(r.getRotation()), r.getX() + r.getWidth() / 2, r.getY() + r.getHeight() / 2);
                g2d.draw(ar.createTransformedShape(r1));
            }
            g2d.translate(r.getX() + r.getWidth() / 2, r.getY() + r.getHeight() / 2);
            g2d.rotate(Math.toRadians(r.getRotation()));
            g2d.setPaint(Color.GRAY);
            g2d.fillRect(-r.getWidth()/2, -r.getHeight()/2, r.getWidth(), r.getHeight());
            g2d.setTransform(at);
        }

        for (RockParticle rp: rockparts) {
            g2d.setPaint(Color.ORANGE);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.fillOval(rp.getX(), rp.getY(), rp.getSize(), rp.getSize());
            rp.move();
        }

        g2d.translate(spaceShip.getX() + spaceShip.getWidth() / 2, spaceShip.getY() + spaceShip.getHeight() / 2);
        g2d.rotate(Math.toRadians(spaceShip.getRotation()));
        g2d.drawImage(spaceShip.getImage(), -spaceShip.getWidth() / 2,
                -spaceShip.getHeight() / 2, null);
        g2d.setTransform(at);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        
        step();
    }
    
    private void step() {

        if (Math.random() * 100 < 10) {
            stars.add(new Star((int) Math.round(Math.random() * 600), (int) Math.round(Math.random() * - 40)));
        }
        if (Math.random() * 100 < 2 && rocks.size() < 6 && rockCooldown == 0) {
            while (Math.abs(rx - lastXValue) < 10) {
                rx = 25 + (int) Math.round(Math.random() * 550);
            }
            rocks.add(new Rock(rx, -10 + (int) Math.round(Math.random() * - 40 )));
            lastXValue = rx;
            rockCooldown = 60;
        }

        List<Laser> lasers = spaceShip.getLasers();

        for (Laser l: lasers) {

            Rectangle r1 = l.getBounds();
            for (Rock r: rocks) {

                Shape r2 = r.getBounds();
                if (r2.intersects(r1)) {
                    l.setVisible(false);
                    r.strength -= 1;
                    for (int i=0;i<4;i++) {
                        rockparts.add(new RockParticle(r.getX() + r.getWidth() / 2, r.getY() + r.getHeight() / 2, i));
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

        for (Rock r: rocks) {
            r.move();
        }
        rocks.removeIf(r -> (r.getY() > 400 || !r.isVisible()));

        rockCooldown = Math.max(rockCooldown - 1, 0);

        rockparts.removeIf(rp -> rp.getSize() < 0);

        spaceShip.move();
        spaceShip.tick();
        cont.handleInput();
        repaint();
    }

}