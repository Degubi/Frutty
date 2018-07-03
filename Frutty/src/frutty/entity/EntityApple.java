package frutty.entity;

import java.awt.Graphics;

import frutty.map.zones.MapZoneFruit;

public final class EntityApple extends EntityFalling{
	public EntityApple(int x, int y) {
		super(x, y);
	}
	
	static {
		System.out.println("LOAD");
	}
	
	@Override
	public void render(Graphics graphics) {
		graphics.drawImage(MapZoneFruit.appleTexture, renderPosX, renderPosY, null);
	}

	@Override
	public void updateClient() {
		if(motionY != 0) {
			checkPlayers(false);
			checkEnemies();
		}
	}
}