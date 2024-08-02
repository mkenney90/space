package com.zephyr;

import java.awt.Color;
import java.awt.geom.Point2D;

public class Particle {
    private Point2D.Double pos;
    private double speed;
    private int direction;
    private double size;
    private double sizeDelta;
    private boolean visible;
    private Color color = Color.orange;
    private float xSpread = 0;
    private double angle = 0;

    protected float wiggle;

    public Particle(int x, int y, double speed, int direction, double size, double sizeDelta, boolean visible, Color color, float xSpread, float angle) {
        pos = new Point2D.Double(x, y);
        this.speed = speed;
        this.direction = direction;
        this.size = size;
        this.sizeDelta = sizeDelta;
        this.visible = visible;
        this.color = color;
        this.xSpread = xSpread;
        this.angle = angle;

        wiggle = (float) Math.random() * xSpread - (xSpread / 2);
    }
    
    public Particle(int x, int y, int direction, double size, double angle) {
        pos = new Point2D.Double(x, y);
        this.speed = 3;
        this.direction = direction;
        this.size = size;
        this.sizeDelta = 0.25;
        this.angle = angle;
    }
    
    public Particle(int x, int y, int direction, Color color, Boolean randomSize) {
        pos = new Point2D.Double(x, y);
        this.speed = 3;
        this.direction = direction;
        this.sizeDelta = 0.12;
        this.color = color;
        this.size = 5 + (randomSize ? Math.random() - 3 : 0);
    }

    public double getX() {
        return pos.x;
    }

    public double getY() {
        return pos.y;
    }

    public boolean isVisible() {
        return visible;
    }
    
    public int getSize() {
        return (int) size;
    }

    public Color getColor() {
        return color;
    }
    
    public void move() {
        pos.setLocation(
            pos.x + speed * Math.cos(Math.toRadians(direction + angle)) + wiggle / 10,
            pos.y + speed * Math.sin(Math.toRadians(direction + angle))
        );
        size -= sizeDelta;
    }
}
