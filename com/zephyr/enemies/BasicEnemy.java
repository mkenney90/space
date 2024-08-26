package com.zephyr.enemies;

import java.awt.Color;

public class BasicEnemy extends BaseEnemy {
    public BasicEnemy(int x, int y) {
        super(x, y, 20, 20, 2, 100, Color.red, "\\..\\src\\resources\\images\\basic_enemy.png");
    }

    @Override
    public void update() {
        y += speed;
        bounds.setLocation(x, y);
    }
}
