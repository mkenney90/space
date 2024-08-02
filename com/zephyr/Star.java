package com.zephyr;

public class Star extends Sprite {

    private double speed;
    protected Boolean visible;

    public Star(int x, int y) {
        super(x, y);
        this.x = x;
        this.y = y;
        speed = (Math.random() * 1.1) + 3;
        visible = true;
    }

    public void move() {
        y = (int) (y + speed);
    }

}
