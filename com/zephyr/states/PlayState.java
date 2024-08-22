package com.zephyr.states;

import java.awt.*;
import java.util.List;

import com.zephyr.Board;
import com.zephyr.Laser;
import com.zephyr.Particle;
import com.zephyr.Rock;
import com.zephyr.Star;
import com.zephyr.Util;

public class PlayState extends BaseState {
    private StateManager state;
    private Board board;

    public PlayState(StateManager state, Board board) {
        this.state = state;
        this.board = state.board;
    }

    public void update() {
        board.getController().handleInput();
            // generate star field
            if (Math.random() * 100 < 10) {
                board.getStars().add(new Star((int) (Math.random() * 600), (int) (Math.random() * -40)));
            }

            // spawn rocks
            if (Math.random() * 100 < 2 && board.getRocks().size() < 6 && board.getRockCooldown() == 0) {
                int newRx;
                do {
                    newRx = 25 + (int) (Math.random() * 550);
                } while (Math.abs(newRx - board.getLastXValue()) < 10);
                // all rocks have a minimum of 8 sides
                int points = Util.randomRange(8, 16);
                Rock newRock = new Rock(newRx, -35, points);
                board.getRocks().add(newRock);
                board.setLastXValue(newRx);
                board.setRockCooldown(60);
            }

            // generate ship thruster flame particles
            board.incrementSpaceShipParticleTimer();

            if (board.getSpaceShipParticleTimer() % 2 == 0) {
                board.getParticles().add(new Particle(
                        board.getSpaceShip().getXInt() + board.getSpaceShip().getWidth() / 2,
                        board.getSpaceShip().getYInt() + 21,
                        0.5,
                        90,
                        6,
                        -0.99,
                        true,
                        Util.flameColor(),
                        1.0f,
                        0));
            }

            // general update functions
            List<Laser> lasers = board.getSpaceShip().getLasers();

            for (Laser l : lasers) {
                Rectangle laserBounds = l.getBounds();
                for (Rock r : rocks) {
                    Shape rockBounds = r.getBounds();

                    // check for laser collisions with asteroids
                    if (rockBounds.intersects(laserBounds)) {
                        r.takeDamage(l.strength);
                        if (r.getStrength() > 1) {
                            // SoundControl.playSound("shatter.wav");
                            score += 25;
                            // add velocity to rock from impact
                            double impactRadius = r.getDistance(l);
                            r.addSpeed('x', l.getX() < r.getX() ? 0.25 : -0.25);
                            r.addRotation((l.getX() < r.getX() ? 1 : -1) * impactRadius * 0.1);
                            r.addSpeed('y', -0.12);
                        } else {
                            // SoundControl.playSound("break.wav");
                            for (int i = 0; i < 8; i++) {
                                double randSize = 6 + Math.random() * 3;
                                float randAngle = (float) Math.random() * 30f;
                                int randOffset = (int) (Math.random() * 10);
                                board.getParticles().add(new Particle(r.getXInt() + randOffset, r.getYInt() + randOffset,
                                        360 / 8 * i, randSize, randAngle));
                            }
                            score += 100;
                        }
                        l.setVisible(false);
                        r.takeDamage(board.getSpaceShip().getLaserStrength());
                        board.getParticles().add(new LaserParticle(l.getXInt(), l.getYInt(), 0));

                    }
                }
                l.update();
            }
            lasers.removeIf(l -> (l.getY() < -10 || !l.isVisible()));

            stars.forEach(Star::move);
            stars.removeIf(s -> s.getY() > 400);

            // collisions between asteroids
            for (int i = 0; i < board.getRocks().size(); i++) {
                Rock r = board.getRocks().get(i);
                for (int j = i + 1; j < board.getRocks().size(); j++) {
                    Rock o = board.getRocks().get(j);
                    if (r.getDistance(o) <= r.getRadius() * 2) {
                        Area rb = r.getBounds();
                        Area ob = o.getBounds();
                        rb.intersect(ob);
                        if (!rb.isEmpty()) {
                            Rectangle cr = rb.getBounds();
                            int crx = cr.x + cr.width / 2;
                            int cry = cr.y + cr.height / 2;
                            for (int k = 0; k < 5; k++) {
                                board.getParticles().add(new Particle(crx, cry, 360 / 5 * k, Color.white, true));
                            }
                            r.handleCollision(o);
                        }
                    }
                }
                r.move();
            }
            board.getRocks().removeIf(r -> (r.getY() > 425 || !r.isVisible()));

            board.getParticles().forEach(Particle::move);
            board.getParticles().removeIf(p -> p.getSize() < 0 || !p.isVisible());

            rockCooldown = Math.max(rockCooldown - 1, 0);

            board.getSpaceShip().move();
            board.getSpaceShip().tick();
        }
        repaint();
}
