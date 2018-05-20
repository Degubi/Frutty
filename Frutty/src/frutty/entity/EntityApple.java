package frutty.entity;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import frutty.Main;
import frutty.gui.GuiStats;
import frutty.map.Map;
import frutty.map.MapZone;

public class EntityApple extends Entity{
	public static final BufferedImage cherryTexture = Main.loadTexture("fruit", "cherry.png");
	public static final BufferedImage appleTexture = Main.loadTexture("fruit", "apple.png");
	
	private int sleepCounter = 0;
	
	public EntityApple(int x, int y) {
		super(x, y);
		
		serverPosY = y;
	}
	
	@Override
	public void render(Graphics graphics) {
		graphics.drawImage(appleTexture, posX, posY, null);
		
		super.render(graphics);
	}

	private static EntityEnemy getEnemyAtPos(int x, int y) {
		for(EntityEnemy enemy : Map.currentMap.enemies) {
			if(enemy.serverPosX == x && enemy.active && (enemy.serverPosY == y || enemy.serverPosY == y - 64)) {
				return enemy;
			}
		}
		return null;
	}
	
	@Override
	public void update(int ticks) {
		if(ticks % 8 == 0) {
			if(MapZone.isEmpty(posX, serverPosY + 64)) {
				if(sleepCounter == 0) {
					motionY = 64;
				}else{
					--sleepCounter;
				}
			}else{
				motionY = 0;
				sleepCounter = 5;
			}
			
			if(motionY != 0) {
				checkPlayer(false);
				
				EntityEnemy enemy = getEnemyAtPos(posX, serverPosY);
				if(enemy != null) {
					enemy.active = false;
					++GuiStats.enemyCount;
					Map.currentMap.score += 100;
				}
			}
			
			serverPosY += motionY;
		}
		posY += motionY / 8;
	}
}