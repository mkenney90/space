package com.zephyr;

public final class Util {
    public static int randomRange(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
