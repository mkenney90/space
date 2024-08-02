package com.zephyr;

public class Primitive {
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected boolean visible;

    public Primitive(int x, int y) {
        this.x = x;
        this.y = y;
        visible = true;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }
}