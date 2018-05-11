package frutty.entity;

import java.awt.Color;
import java.awt.Graphics;

import frutty.gui.GuiStats;
import frutty.map.Map;
import frutty.map.MapZone;
import frutty.stuff.EnumFacing;

public class EntityBall extends Entity{
	public EntityBall() {
		super(0, 0);
		active = false;
	}

	@Override
	public void render(Graphics graphics) {
		graphics.setColor(Color.WHITE);
		graphics.fillOval(posX + 24, posY + 24, 16, 16);
		super.render(graphics);
	}

	public void activate(int x, int y, EnumFacing facing) {
		if(!active) {
			active = true;
			posX = x;
			posY = y;
			serverPosX = x;
			serverPosY = y;
			
			motionX = facing.xOffset;
			motionY = facing.yOffset;
			
			posX += motionX;
			posY += motionY;
			
		}
	}
	
	@Override
	public void update(int ticks) {
		if(ticks % 15 == 0) {
			EntityEnemy enemy = Map.getEnemyPredictedAtPos(posX, posY, this);
			if(enemy != null) {
				enemy.active = false;
				active = false;
				++GuiStats.enemyCount;
				Map.currentMap.score += 100;
			}
			
			for(EntityPlayer player : Map.currentMap.players) {
				if(posY == player.posY && posX == player.posX) {
					active = false;
				}
			}
			
			if(!MapZone.isEmpty(posX + motionX, posY + motionY)) {
				EnumFacing facing = findFreeFacing();
				motionX = facing.xOffset;
				motionY = facing.yOffset;
			}
			
			posX += motionX;
			posY += motionY;
			serverPosX = posX;
			serverPosY = posY;
		}
	}
}