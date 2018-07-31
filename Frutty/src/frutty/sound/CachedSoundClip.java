package frutty.sound;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import frutty.gui.GuiSettings.Settings;

public final class CachedSoundClip {
	private final Clip soundClip = createClip();
	
	public CachedSoundClip(String filePath) {
		if(Settings.enableSound) {
			try(AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("./sounds/" + filePath))){
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
			} catch (LineUnavailableException e) {
				return null;
			}
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