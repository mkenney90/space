package com.zephyr;

import javax.swing.*;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;


public class SpaceShip extends Sprite {
    private int dx;
    private int dy;
    private int x = 288;
    private int y = 300;
    private int laserDelay = 20;
    private int laserTimer = 0;
    private int laserStrength = 1;
    private double rotation = 0;
    private Image image;
    private List<Laser> lasers;

    public SpaceShip(int x, int y) {
        super(x, y);
        initShip();
    }

    private void initShip() {
        ImageIcon ii = new ImageIcon("com/zephyr/src/resources/spaceship.png");
        image = ii.getImage();
        lasers = new ArrayList<>();

        width = image.getWidth(null);
        height = image.getHeight(null);
    }

    public void tick() {
        laserTimer = Math.max(laserTimer - 1, 0);
    }

    public void move() {
        x = Math.max(Math.min(x + dx, 562),10);
        y += dy;
    }

    public void fireLaser() {
        if (laserTimer > 0) return;
        lasers.add(new Laser(x + getWidth()/2, y + 4, 6, laserStrength));
        laserTimer = laserDelay;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setDX(int dx) {
        this.dx = dx;
    }

    public void setDY(int dy) {
        this.dy = dy;
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