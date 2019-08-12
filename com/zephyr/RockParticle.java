package com.zephyr;

public class RockParticle extends Primitive {

    private double speed;
    private int direction;
    protected double size = 5;
    protected double sizeDelta = 0.2;
    protected Boolean visible;

    public RockParticle(int x, int y, int direction) {
        super(x, y);
        this.x = x;
        this.y = y;
        this.direction = direction;
        speed = 2;
        visible = true;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSize() {
        return (int) size;
    }

    public void move() {
        switch (direction) {
            case 0:
                x += speed;
                y -= speed;
                break;
            case 1:
                x += speed;
                y += speed;
                break;
            case 2:
                x -= speed;
                y += speed;
                break;
            case 3:
                x -= speed;
                y -= speed;
                break;
        }
        size -= sizeDelta;
    }

}
