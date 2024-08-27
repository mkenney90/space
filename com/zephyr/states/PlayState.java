package com.zephyr.states;

import java.awt.*;
import java.awt.geom.Area;
import java.util.List;

import com.zephyr.Board;
import com.zephyr.Laser;
import com.zephyr.Particle;
import com.zephyr.PlayGame;
import com.zephyr.Particle.LaserParticle;
import com.zephyr.SpaceShip.ShipState;
import com.zephyr.enemies.BaseEnemy;
import com.zephyr.enemies.BasicEnemy;
import com.zephyr.Rock;
import com.zephyr.SpaceShip;
import com.zephyr.Star;
import com.zephyr.Util;

public class PlayState implements BaseState {
    private final int[] GAME_BOUNDS = {PlayGame.SCREEN_WIDTH+50, PlayGame.SCREEN_HEIGHT+50};
    Rectangle gameBoundsRect = new Rectangle(-25, -25, GAME_BOUNDS[0], GAME_BOUNDS[1]);
    private Board board;
    
    public PlayState(Board board) {
        this.board = board;
    }
    public void enterState() {
        System.out.println("PlayState init");
    }
    public void updateState() {
        List<Star> stars = board.getStars();
        List<Rock> rocks = board.getRocks();
        List<Particle> particles = board.getParticles();
        List<Laser> lasers = board.getSpaceShip().getLasers();
        List<BaseEnemy> enemies = board.getEnemies();
        SpaceShip spaceShip = board.getSpaceShip();
        int rockCooldown = board.getRockCooldown();
        int spaceShipParticleTimer = board.getSpaceShipParticleTimer();
        int lastXValue = board.getLastXValue();
        int score = board.getScore();
        String gamePhase = board.getGamePhase();
        
        handleStars(stars);
        handleRocks(rocks, spaceShip, score, rockCooldown, lastXValue, particles, enemies, gamePhase);
        handleParticles(particles, spaceShip, score);
        handleSpaceShip(spaceShip, spaceShipParticleTimer, particles);
        handleLasers(lasers, rocks, particles, spaceShip);
        handleEnemies(enemies);
        
        // Update state variables
        board.setRockCooldown(Math.max(rockCooldown - 1, 0));
        board.incrementSpaceShipParticleTimer();
    }
    private void handleStars(List<Star> stars) {
        if (Math.random() * 100 < 10) {
            stars.add(new Star((int) (Math.random() * 600), (int) (Math.random() * -40)));
        }

        stars.forEach(Star::move);
        stars.removeIf(s -> s.getY() > 400);
    }

    private void handleRocks(List<Rock> rocks, SpaceShip spaceShip, int score, int rockCooldown, int lastXValue, List<Particle> particles, List<BaseEnemy> enemies, String gamePhase) {
        // spawn rocks
        if (!gamePhase.equals("boss")) {
            if (Math.random() * 100 < 5 && rocks.size() < 6 + board.getGameLevel() && rockCooldown < 1) {
                int newRx;
                do {
                    newRx = 25 + (int) (Math.random() * 550);
                } while (Math.abs(newRx - lastXValue) < 75);
                // all rocks have a minimum of 8 sides
                int points = Util.randomRange(8, 16);
                Rock newRock = new Rock(newRx, Util.randomRange(0, 20) - 50, points);
                rocks.add(newRock);
                lastXValue = newRx;
                rockCooldown = 60;
            }
        } else {
            if (enemies.size() < 1 && Util.randomRange(0, 500) < 50) {
                enemies.add(new BasicEnemy(-30, 100));
                System.out.println("enemy spawned");
            }
        }

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
        rocks.removeIf(r -> (r.getY() > 425 || !r.isVisible()));
    }

    private void handleParticles(List<Particle> particles, SpaceShip spaceShip, int score) {
        particles.forEach(Particle::move);
        particles.removeIf(p -> p.getSize() < 0 || !p.isVisible());
    }

    private void handleSpaceShip(SpaceShip spaceShip, int spaceShipParticleTimer, List<Particle> particles) {
        // generate ship thruster flame particles
        spaceShipParticleTimer++;

        if (spaceShip.getState() == ShipState.NORMAL) {
            if (spaceShipParticleTimer % 2 == 0) {
                particles.add(new Particle(
                    spaceShip.getXInt() + spaceShip.getWidth() / 2,
                    spaceShip.getYInt() + 21,
                    0.5,
                    90,
                    6,
                    -1.0,
                    true,
                    Util.flameColor(),
                    1.0f,
                    0));
            }
        }
        if (spaceShip.getState() == ShipState.DYING) {
            particles.add(new Particle(
                spaceShip.getXInt() + Util.randomRange(0, spaceShip.getWidth()),
                spaceShip.getYInt() + Util.randomRange(0, spaceShip.getHeight()),
                1,
                Util.randomRange(0, 360),
                7,
                -0.99,
                true,
                Util.flameColor(),
                1.0f,
                0));
        }
        if (spaceShip.getState() == ShipState.EXPLODE) {
            for (int i=0;i<45;i++) {
                particles.add(new Particle(
                    spaceShip.getXInt() + Util.randomRange(0, spaceShip.getWidth()),
                    spaceShip.getYInt() + Util.randomRange(0, spaceShip.getHeight()),
                    5,
                    Util.randomRange(0, 360),
                    Util.randomRange(7,9),
                    -0.15,
                    true,
                    Util.flameColor(),
                    1.0f,
                    0));
            }
            spaceShip.setState(ShipState.DEAD);
        }

        spaceShip.move();
        spaceShip.update();
    }

    private void handleLasers(List<Laser> lasers, List<Rock> rocks, List<Particle> particles, SpaceShip spaceShip) {
        // Update laser positions and handle collisions
        for (Laser l : lasers) {
            Rectangle laserBounds = l.getBounds();
            for (Rock r : rocks) {
                Shape rockBounds = r.getBounds();

                // Check for laser collisions with rocks
                if (rockBounds.intersects(laserBounds)) {
                    r.takeDamage(l.getStrength());
                    if (r.getStrength() > 1) {
                        // Update score and rock properties
                        board.addScore(25);
                        double impactRadius = r.getDistance(l);
                        r.addSpeed('x', l.getX() < r.getX() ? 0.25 : -0.25);
                        r.addSpeed('y', -0.12);
                        // add a spin force to the rock
                        r.addRotation((l.getX() < r.getX() ? 1 : -1) * impactRadius * 0.20); 
                    } else {
                        // Add particles for rock breakage
                        for (int i = 0; i < 8; i++) {
                            double randSize = 6 + Math.random() * 3;
                            float randAngle = (float) Math.random() * 30f;
                            int randOffset = (int) (Math.random() * 10);
                            particles.add(new Particle(r.getXInt() + randOffset, r.getYInt() + randOffset,
                                    360 / 8 * i, randSize, randAngle));
                        }
                        board.addScore(100);
                    }
                    // Destroy the laser
                    l.setVisible(false); 
                    r.takeDamage(spaceShip.getLaserLevel());
                    particles.add(new LaserParticle(l.getXInt(), l.getYInt(), 0));
                }
            }
            l.update();
        }
        // Remove lasers that are no longer visible or have gone off-screen
        lasers.removeIf(l -> (!l.getBounds().intersects(gameBoundsRect) || !l.isVisible()));
    }

    private void handleEnemies(List<BaseEnemy> enemies) {
        for (BaseEnemy e: enemies) {
            e.move();
            e.update();
        }
    }

    public void render(Graphics g) {

    }
}
