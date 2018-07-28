package frutty.sound;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public final class CachedSoundClip {
	private final Clip soundClip = createClip();
	
	public CachedSoundClip(String filePath) {
		try(AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("./sounds/" + filePath))){
			soundClip.open(audioInputStream);
			((FloatControl) soundClip.getControl(FloatControl.Type.MASTER_GAIN)).setValue(-20);
			
		} catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
			e.printStackTrace();
		}
	}
	
	private static Clip createClip() {
		try {
			return AudioSystem.getClip();
		} catch (LineUnavailableException e) {
			return null;
		}
	}
	
	public void start() {
		soundClip.setMicrosecondPosition(0);
		soundClip.start();
	}
	
	public void stop() {
		soundClip.stop();
		soundClip.close();
	}
}