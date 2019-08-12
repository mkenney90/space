package com.zephyr;

public class Star extends Primitive {

    private double speed;
    protected Boolean visible;

    public Star(int x, int y) {
        super(x, y);
        this.x = x;
        this.y = y;
        speed = (Math.random() * 1.1) + 3;
        visible = true;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void move() {
        y = (int) (y + speed);
    }

}
