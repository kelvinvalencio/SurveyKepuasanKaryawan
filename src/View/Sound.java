/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import java.io.File;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 *
 * @author Sevian
 */
public class Sound {
    public static boolean SWITCH = true;
    public static synchronized void play(final InputStream stream) 
    {
        Runnable music = new Runnable() { 
            public void run() {
                try {
                    Clip clip = AudioSystem.getClip();
                    AudioInputStream inputStream = AudioSystem.getAudioInputStream(stream);
                    clip.open(inputStream);
                    clip.start();
                } catch (Exception e) {
                    System.out.println("play sound error: " + e.getMessage() + " for " + stream);
                }
            }
        };
        //use .wav             
        Thread thread = new Thread(music);
        if(SWITCH){
            thread.start();
            SWITCH = false;
        }
        else{
            thread.stop();
            SWITCH = true;
        }
    }
}
