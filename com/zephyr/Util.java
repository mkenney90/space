package com.zephyr;

import java.awt.Color;


public final class Util {
    public static int randomRange(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
    public static double randomRangeD(double min, int max) {
        return (Math.random() * (max - min)) + min;
    }
    public static Color randomColor(int red, int green, int blue, int alpha, boolean randomRed, boolean randomGreen, boolean randomBlue, boolean randomAlpha) {
        int _r = red, _g = green, _b = blue, _a = alpha;
        
        if (randomRed) {
            _r = red + randomRange(-25, 25);
        }        
        if (randomGreen) {
            _g = green + randomRange(-25, 25);
        }        
        if (randomBlue) {
            _b = blue + randomRange(-25, 25);
        }        
        if (randomAlpha) {
            _a = alpha + randomRange(-25, 25);
        }

        return new Color(_r, _g, _b, _a);
    }
    public static Color flameColor() {
        int intensity = randomRange(0, 2);
        int _r = 255, _g = 175 + intensity * 40, _b = intensity * 120, _a = 200;

        return new Color(_r, _g, _b, _a);
    }
}
