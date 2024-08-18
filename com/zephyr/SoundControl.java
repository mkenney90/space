package com.zephyr;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

public class SoundControl {
    public SoundControl() {

    }

    public static void playSound(String filename) {
        String path = "com\\zephyr\\src\\resources\\sounds\\" + filename;

        try {
            File soundFile = new File(path);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
