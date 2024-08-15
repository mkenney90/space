package com.zephyr;

import java.awt.Color;
import java.awt.geom.Point2D;

public class Particle {
    protected Point2D.Double pos;
    protected double speed;
    protected int direction;
    protected double size;
    protected double sizeDelta;
    protected boolean visible = true;
    protected Color color = Color.orange;
    protected int alpha;
    protected double alphaDelta;
    protected float xSpread = 0;
    protected double angle = 0;

    protected float wiggle;

    // main constructor with all params
    public Particle(int x, int y, double speed, int direction, double size, double sizeDelta, boolean visible, Color color, float xSpread, float angle) {
        pos = new Point2D.Double(x, y);
        this.speed = speed;
        this.direction = direction;
        this.size = size;
        this.sizeDelta = sizeDelta;
        this.visible = visible;
        this.color = color;
        this.alpha = 255;
        this.xSpread = xSpread;
        this.angle = angle;

        wiggle = (float) Math.random() * xSpread - (xSpread / 2);
    }
    
    public Particle(int x, int y, int direction, double size, float angle) {
        this(x, y, Util.randomRangeD(2, 5), direction, size, 0.25, true, Color.orange, 0f, angle);
    }
    
    public Particle(int x, int y, int direction, Color color, Boolean randomSize) {
        this(x, y, 3, direction, 5 + (randomSize ? Math.random() - 3 : 0), 0.12, true, color, 0, 0);
    }

    public Particle() {
        this(0, 0, 0, 0, 0, 0, false, null, 0, 0);
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
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    public void move() {
        pos.setLocation(
            pos.x + speed * Math.cos(Math.toRadians(direction + angle)) + wiggle / 10,
            pos.y + speed * Math.sin(Math.toRadians(direction + angle))
        );
        alpha -= alphaDelta;
        size -= sizeDelta;
    }

    static class LaserParticle extends Particle {

        protected int sizeMax;

        public LaserParticle(int x, int y, int direction) {
            super(x, y, 0, direction, 2, 5, true, Color.red, 0, 0);
            this.sizeMax = 25;
            this.alphaDelta = 25;
        }
        
        @Override
        public void move() {
            super.move();
            if (size > sizeMax) {
                visible = false;
            }
        }
    }
}