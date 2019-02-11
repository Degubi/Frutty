package frutty.entity;

import frutty.sound.*;
import frutty.world.*;
import frutty.world.zones.*;
import java.awt.*;

public final class EntityApple extends EntityFalling{
	private final StreamedSoundClip fallSound = new StreamedSoundClip("fall.wav");
	private static final Color BROWN = new Color(205, 133, 63);
	
	public EntityApple(int x, int y) {
		super(x, y);
		fallSound.start();
	}
	
	@Override
	public void onFallStopped() {
		fallSound.stop();
		Particle.spawnRandomParticles(10, serverPosX, serverPosY, BROWN);
	}

	@Override
	public void render(Graphics graphics) {
		graphics.drawImage(MapZoneApple.appleTexture, renderPosX, renderPosY, null);
	}
	
	@Override
	public void onKilled(Entity killer) {
		fallSound.stop();
	}
	
	@Override
	public void updateClient() {
		if(motionY != 0) {
			checkPlayers();
			
			for(var enemy : World.enemies) {
				if(doesCollide(enemy.serverPosX, enemy.serverPosY)) {
					enemy.onKilled(this);
				}
			}
		}
	}
}