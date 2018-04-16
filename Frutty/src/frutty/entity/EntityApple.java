package frutty.entity;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import frutty.gui.GuiIngame;
import frutty.gui.GuiStats;
import frutty.map.Map;
import frutty.map.MapZone;

/**
 * Physics alma class fájlja
 */
public class EntityApple extends Entity{
	public static final BufferedImage cherryTexture = loadTexture("cherry.png");
	public static final BufferedImage appleTexture = loadTexture("apple.png");
	
	private int sleepCounter = 0;
	
	public EntityApple(int x, int y) {
		super(x, y);
	}
	
	public void render(Graphics graphics) {
		graphics.drawImage(appleTexture, posX, posY, null);
	}
	
	/**
	 * Zuhanás logika, ellenség esetén pont adás, játékos esetén game over
	 */
	public void update(int ticks) {
		if(MapZone.isEmpty(posX, posY + 64)) {
			if(sleepCounter == 0) {
				posY += 64;
				
				for(EntityPlayer players : Map.currentMap.getPlayers()) {
					if(posY == players.posY && posX == players.posX) {
						GuiIngame.showMessageAndClose("Game over!");
					}
				}
				
				EntityEnemy enemy = Map.getEnemyAtPos(posX, posY);
				if(enemy != null) {
					enemy.active = false;
					++GuiStats.enemyCount;
					Map.currentMap.score += 100;
				}
			}else{
				--sleepCounter;
			}
		}else{
			sleepCounter = 5;
		}
	}
}