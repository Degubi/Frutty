package frutty.sound;

import frutty.gui.GuiSettings.*;
import java.io.*;
import javax.sound.sampled.*;
import javax.sound.sampled.DataLine.*;

public final class StreamedSoundClip{
	private final File soundFile;
	private boolean shouldPlay = true;
	
	public StreamedSoundClip(String fileName) {
		soundFile = new File("./sounds/" + fileName);
	}
	
	public void start() {
		if(Settings.enableSound) {
			new Thread(() -> {
				try(AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile)){
					AudioFormat audioFormat = audioInputStream.getFormat();
					@SuppressWarnings("resource")
					SourceDataLine soundLine = (SourceDataLine) AudioSystem.getLine(new Info(SourceDataLine.class, audioFormat));
					
					soundLine.open(audioFormat);
					((FloatControl) soundLine.getControl(FloatControl.Type.MASTER_GAIN)).setValue(-20);
					soundLine.start();
					
					int readBytes = 0;
					byte[] soundData = new byte[16 * 1024];
					
					while(shouldPlay && readBytes > -1) {
						readBytes = audioInputStream.read(soundData, 0, soundData.length);
						if (readBytes >= 0) {
							soundLine.write(soundData, 0, readBytes);
						}
			         }
					
					soundLine.close();
				} catch (UnsupportedAudioFileException ex) {
					ex.printStackTrace();
				} catch (IOException ex) {
					ex.printStackTrace();
				} catch (LineUnavailableException ex) {
					ex.printStackTrace();
				}
			}).start();
		}
	}
	
	public void stop() {
		shouldPlay = false;
	}
}