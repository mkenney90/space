package com.zephyr;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

public class SoundControl {
    public SoundControl() {

    }

    public static void playSound(String filename) {
        try {
            Clip clip = AudioSystem.getClip();
            File soundFile = new File("C:\\Users\\Mike\\Documents\\dev\\space\\com\\zephyr\\src\\resources\\sounds\\" + filename);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            clip.open(audioIn);
            clip.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
