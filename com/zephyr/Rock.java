package com.zephyr;

import org.w3c.dom.css.Rect;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class Rock extends Primitive {

    private double speed;
    private int width;
    private int height;
    private int rotation;
    protected int strength = (int) Math.max(Math.random() * 5 - 2, 1);
    private double rotationDelta;
    protected Boolean visible;

    public Rock(int x, int y) {
        super(x, y);
        this.x = x;
        this.y = y;
        this.strength = strength;
        speed = (Math.random() * .1) + 1.3;
        width = (int) (Math.random() * 5) + 10;
        height = (int) (Math.random() * 5) + 10;
        rotation = (int) (Math.random() * 360);
        rotationDelta = -8 + (Math.random() * 16);
        visible = true;
        System.out.println(strength);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getRotation() {
        return rotation;
    }

    public Shape getBounds() {
        Rectangle r1 = new Rectangle(x, y, width, height);
        AffineTransform at = AffineTransform.getRotateInstance(Math.toRadians(rotation), x + width / 2, y + height / 2);
        return at.createTransformedShape(r1);
    }

    public void move() {
        rotation += rotationDelta;
        y = (int) (y + speed);
        setVisible(strength > 0);
    }

}

