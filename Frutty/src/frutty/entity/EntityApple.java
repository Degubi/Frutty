package frutty.entity;

import java.awt.Graphics;

import frutty.sound.StreamedSoundClip;
import frutty.world.zones.MapZoneApple;

public final class EntityApple extends EntityFalling{
	private final StreamedSoundClip fallSound = new StreamedSoundClip("fall.wav");
	
	public EntityApple(int x, int y) {
		super(x, y);
		
		fallSound.start();
	}
	
	@Override
	public void onFallStopped() {
		fallSound.stop();
	}

	@Override
	public void render(Graphics graphics) {
		graphics.drawImage(MapZoneApple.appleTexture, renderPosX, renderPosY, null);
	}

	@Override
	public void updateClient() {
		if(motionY != 0) {
			checkPlayers(false);
			checkEnemies();
		}
	}
}