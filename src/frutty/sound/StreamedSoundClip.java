package frutty.sound;

import frutty.*;
import frutty.gui.GuiSettings.*;
import java.io.*;
import javax.sound.sampled.*;
import javax.sound.sampled.DataLine.*;

@SuppressWarnings("resource")
public final class StreamedSoundClip {
    private final File soundFile;
    private volatile boolean shouldPlay = true;

    public StreamedSoundClip(String fileName) {
        soundFile = new File(Main.executionDir + "sounds/" + fileName);
    }

    public void start() {
        if(Settings.enableSound) {
            new Thread(() -> {
                try(var audioInputStream = AudioSystem.getAudioInputStream(soundFile)){
                    var audioFormat = audioInputStream.getFormat();

                    var soundLine = (SourceDataLine) AudioSystem.getLine(new Info(SourceDataLine.class, audioFormat));

                    soundLine.open(audioFormat);
                    ((FloatControl) soundLine.getControl(FloatControl.Type.MASTER_GAIN)).setValue(-20);
                    soundLine.start();

                    var readBytes = 0;
                    var soundData = new byte[16 * 1024];

                    while(shouldPlay && readBytes > -1) {
                        readBytes = audioInputStream.read(soundData, 0, soundData.length);
                        if (readBytes >= 0) {
                            soundLine.write(soundData, 0, readBytes);
                        }
                     }

                    soundLine.close();
                } catch (IOException | LineUnavailableException | UnsupportedAudioFileException ex) {
                    ex.printStackTrace();
                }
            }).start();
        }
    }

    public void stop() {
        shouldPlay = false;
    }
}