package com.zephyr;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class Rock extends Primitive {

    private double speed;
    private int width;
    private int height;
    private int xPoly[] = {-12, 0, 12, 7, -7};
    private int yPoly[] = {3, 12, 3, -12, -12};
    private Polygon poly;
    private int rotation;
    protected int strength;
    private double rotationDelta;
    protected Boolean visible;

    public Rock(int x, int y) {
        super(x, y);
        this.x = x;
        this.y = y;
        this.strength = (int) Math.max(Math.random() * 5 - 2, 1);
        poly = new Polygon(xPoly, yPoly, xPoly.length);
        speed = (Math.random() * .1) + 1.3;
        width = (int) (Math.random() * 5) + 10;
        height = (int) (Math.random() * 5) + 10;
        rotation = (int) (Math.random() * 360);
        rotationDelta = -8 + (Math.random() * 16);
        visible = true;
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

    public Polygon getPoly() {
        return poly;
    }

    public int getRotation() {
        return rotation;
    }

    public Shape getBounds() {
        //TODO: Fix bounding box code

        //Rectangle r1 = new Rectangle(x, y, width, height);

        Polygon p1 = poly;

        AffineTransform at = AffineTransform.getRotateInstance(Math.toRadians(rotation), x, y);
        return at.createTransformedShape(p1);
    }

    public void move() {
        rotation += rotationDelta;
        y = (int) (y + speed);
        setVisible(strength > 0);
    }

}

