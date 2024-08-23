package com.zephyr;

import java.awt.*;

public class Laser extends Sprite {
    protected int size = 5;
    protected int strength = 1;
    protected int speed = 5;

    public Laser(int orig_x, int orig_y, int size, int strength) {
        super(orig_x, orig_y);
        this.size = size;
        this.strength = strength;
    }

    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, 1, size);
    }

    public int getStrength() {
        return strength;
    }

    public void update() {
        y -= speed;
    }
}