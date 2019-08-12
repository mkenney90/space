package com.zephyr;

import java.awt.*;

public class Laser extends Primitive {

    protected int size = 5;
    protected int strength = 1;
    protected int speed = 5;
    protected int x1, x2, y1, y2;

    public Laser(int orig_x, int orig_y, int size, int strength) {
        super(orig_x, orig_y);
        this.size = size;
        this.strength = strength;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 1, size);
    }

    public void update() {

        this.y -= this.speed;
    }
}