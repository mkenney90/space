package com.zephyr;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpaceShip extends Sprite {
    public enum ShipState {
        NORMAL,
        SHIELD,
        DYING,
        EXPLODE,
        DEAD
    }
    private ShipState state;
    private int deathTimer = 0;

    private float dx = 0;
    private float dy = 0;
    private float ix = 0;
    private float iy = 0;
    private float accelX = 0;
    private float accelY = 0;

    private float maxDX = 2.5f;
    private float maxDY = 2.25f;

    private String direction = "neutral";
    private int laserDelay = 20;
    private int laserTimer = 0;
    private int laserStrength = 1;
    private double rotation = 0;
    private double rotationDelta = 0;
    private Image image;
    private List<Laser> lasers;
    private Map<String, BufferedImage> sprites = new HashMap<>();

    public SpaceShip(float x, float y) {
        super(x, y);
        initShip();
    }

    private void loadImages() {
        try {
            sprites.put("neutral",
                    ImageIO.read(getClass().getResourceAsStream("\\src\\resources\\images\\spaceship.png")));
            sprites.put("left",
                    ImageIO.read(getClass().getResourceAsStream("\\src\\resources\\images\\spaceship_left.png")));
            sprites.put("right",
                    ImageIO.read(getClass().getResourceAsStream("\\src\\resources\\images\\spaceship_right.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initShip() {
        loadImages();

        image = sprites.get(direction);
        lasers = new ArrayList<>();
        width = image.getWidth(null);
        height = image.getHeight(null);
        state = ShipState.NORMAL;
    }

    public void update() {
        if (state == ShipState.DYING) {
            explode();
            return;
        }

        if (Math.abs(ix) > Math.abs(accelX)) {
            ix *= 0.95;
        }
        if (Math.abs(ix) < 0.005) {
            ix = 0;
        }
        ix += accelX;
        dx = ix;

        if (Math.abs(dx) > maxDX) {
            dx = maxDX * Math.signum(dx);
        }

        if (Math.abs(iy) > Math.abs(accelY)) {
            iy *= 0.92;
        }
        if (Math.abs(iy) < 0.005) {
            iy = 0;
        }
        iy += accelY;
        dy = iy;

        if (Math.abs(dy) > maxDY) {
            dy = maxDY * Math.signum(dy);
        }

        if (rotationDelta < 0) {
            rotation--;
            rotationDelta++;
        }
        if (rotationDelta > 0) {
            rotation++;
            rotationDelta--;
        }

        laserTimer = Math.max(laserTimer - 1, 0);
        image = sprites.get(direction);
    }

    public void move() {
        x = Math.max(Math.min(x + dx, 572), 4);
        y = Math.max(Math.min(y + dy, 356), 4);
    }

    public void fireLaser() {
        if (laserTimer > 0)
            return;
        lasers.add(new Laser((int) x + getWidth() / 2, (int) y + 4, 6, laserStrength));
        SoundControl.playSound("laser1.wav");
        laserTimer = laserDelay;
    }

    public void explode() {
        if (deathTimer < 50) {
            applyJitter();
            deathTimer++;
        } else {
            state = ShipState.EXPLODE;
            visible = false;
        }
    }

    public void applyJitter() {
        setDX(Util.randomRange(-2,2));
        setDY(Util.randomRange(-2,2));
        rotationDelta = Util.randomRange(-90, 90);
        System.out.println(getRotation());
    }

    public void setDX(int dx) {
        this.dx = dx;
    }

    public void setDY(int dy) {
        this.dy = dy;
    }

    public void setIX(float ix) {
        this.ix = ix;
    }

    public void setIY(float iy) {
        this.iy = iy;
    }

    public void setAccelX(float accelX) {
        this.accelX = accelX;
    }

    public void setAccelY(float accelY) {
        this.accelY = accelY;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Image getImage() {
        return image;
    }

    public ShipState getState() {
        return state;
    }

    public void setState(ShipState newState) {
        if (newState instanceof ShipState) {
            state = newState;
        }
    }

    public List<Laser> getLasers() {
        return lasers;
    }

    public int getLaserStrength() {
        return laserStrength;
    }

    public void setLaserStrength(int str) {
        this.laserStrength = str;
    }

    public double getRotation() {
        return rotation;
    }

}