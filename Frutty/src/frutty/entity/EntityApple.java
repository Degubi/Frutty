package frutty.entity;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import frutty.Main;
import frutty.map.Map;
import frutty.map.base.MapZone;

public final class EntityApple extends Entity{
	public static final BufferedImage appleTexture = Main.loadTexture("fruit", "apple.png");
	
	private int sleepCounter = 0;
	
	public EntityApple(int x, int y) {
		super(x, y);
		
		serverPosY = y;
	}
	
	@Override
	public void render(Graphics graphics) {
		graphics.drawImage(appleTexture, renderPosX, renderPosY, null);
		
		super.render(graphics);
	}
	
	@Override
	public void update(int ticks) {
		if(motionY != 0) {
			checkPlayers();
			
			for(EntityEnemy enemies : Map.enemies) {
				if(doesCollide(enemies.serverPosX, enemies.serverPosY)) {
					enemies.active = false;
				}
			}
		}
		
		if(ticks % 8 == 0) {
			if(MapZone.isEmptyAt(coordsToIndex(renderPosX, serverPosY + 64))) {
				if(sleepCounter == 0) {
					motionY = 64;
				}else{
					--sleepCounter;
				}
			}else{
				motionY = 0;
				sleepCounter = 5;
			}
			serverPosY += motionY;
		}
		renderPosY += motionY / 8;
	}
}