package frutty.entity;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import frutty.Main;

public final class EntityApple extends EntityFalling{
	public static final BufferedImage appleTexture = Main.loadTexture("fruit", "apple.png");
	
	public EntityApple(int x, int y) {
		super(x, y);
	}
	
	@Override
	public void render(Graphics graphics) {
		graphics.drawImage(appleTexture, renderPosX, renderPosY, null);
	}

	@Override
	public void updateClient() {
		if(motionY != 0) {
			checkPlayers();
			checkEnemies();
		}
	}
}