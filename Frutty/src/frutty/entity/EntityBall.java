package frutty.entity;

import java.awt.Color;
import java.awt.Graphics;

import frutty.Main;
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
		graphics.fillOval(posX + 16, posY + 16, 16, 16);
	}

	public void activate(int x, int y, EnumFacing facing) {
		if(!active) {
			active = true;
			posX = x;
			posY = y;
			
			setFacing(facing);
			
			posX += motionX;
			posY += motionY;
		}
	}
	
	
	private void setFacing(EnumFacing facing) {
		motionX = facing.xOffset;
		motionY = facing.yOffset;
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
			
			for(EntityPlayer player : Map.currentMap.players)
				if(posY == player.posY && posX == player.posX) {
					active = false;
				}
			
			if(!MapZone.isEmpty(posX + motionX, posY + motionY)) {
				for(int rotat = motionY == 64 ? 1 : 3; ; rotat = Main.rand.nextInt(4)) {
					if(rotat == 0 && MapZone.isEmpty(posX + 64, posY)) {
						setFacing(EnumFacing.RIGHT);
						break;
					}else if(rotat == 1 && MapZone.isEmpty(posX - 64, posY)) {
						setFacing(EnumFacing.LEFT);
						break;
					}else if(rotat == 2 && MapZone.isEmpty(posX, posY + 64)) {
						setFacing(EnumFacing.DOWN);
						break;
					}else if(rotat == 3 && MapZone.isEmpty(posX, posY - 64)){
						setFacing(EnumFacing.UP);
						break;
					}
				}
			}
			posX += motionX;
			posY += motionY;
		}
	}
}