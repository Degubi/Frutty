package frutty.sound;

import frutty.tools.*;
import java.io.*;
import javax.sound.sampled.*;

public final class CachedSoundClip {
    private final Clip soundClip = createClip();

    public CachedSoundClip(String filePath) {
        if(Settings.enableSound) {
            try(var audioInputStream = AudioSystem.getAudioInputStream(new File(GamePaths.SOUNDS_DIR + filePath))){
                soundClip.open(audioInputStream);
                ((FloatControl) soundClip.getControl(FloatControl.Type.MASTER_GAIN)).setValue(-20);
            } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
                e.printStackTrace();
            }
        }
    }

    private static Clip createClip() {
        if(Settings.enableSound) {
            try {
                return AudioSystem.getClip();
            } catch (LineUnavailableException e) {}
        }
        return null;
    }

    public void start() {
        if(Settings.enableSound) {
            soundClip.setMicrosecondPosition(0);
            soundClip.start();
        }
    }

    public void stop() {
        if(Settings.enableSound) {
            soundClip.stop();
            soundClip.close();
        }
    }
}