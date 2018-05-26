package frutty.entity;

import java.awt.Color;
import java.awt.Graphics;

import frutty.gui.GuiStats;
import frutty.map.Map;
import frutty.map.base.MapZone;

public class EntityBall extends Entity{
	public EntityBall() {
		super(0, 0);
		active = false;
	}

	@Override
	public void render(Graphics graphics) {
		graphics.setColor(Color.WHITE);
		graphics.fillOval(renderPosX + 24, renderPosY + 24, 16, 16);
		super.render(graphics);
	}

	public void activate(int x, int y, EnumFacing facing) {
		if(!active) {
			active = true;
			renderPosX = x;
			renderPosY = y;
			serverPosX = x;
			serverPosY = y;
			
			motionX = facing.xOffset;
			motionY = facing.yOffset;
			
			renderPosX += motionX;
			renderPosY += motionY;
		}
	}
	
	@Override
	public void update(int ticks) {
		if(ticks % 15 == 0) {
			EntityEnemy enemy = Map.getEnemyPredictedAtPos(renderPosX, renderPosY, this);
			if(enemy != null) {
				enemy.active = false;
				active = false;
				++GuiStats.enemyCount;
				Map.currentMap.score += 100;
			}
			
			for(EntityPlayer player : Map.currentMap.players) {
				if(renderPosY == player.renderPosY && renderPosX == player.renderPosX) {
					active = false;
				}
			}
			
			if(!MapZone.isEmpty(renderPosX + motionX, renderPosY + motionY)) {
				EnumFacing facing = findFreeFacing();
				motionX = facing.xOffset;
				motionY = facing.yOffset;
			}
			
			renderPosX += motionX;
			renderPosY += motionY;
			serverPosX = renderPosX;
			serverPosY = renderPosY;
		}
	}
}