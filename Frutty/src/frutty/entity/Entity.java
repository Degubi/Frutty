package frutty.entity;

import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;

import frutty.Main;
import frutty.gui.GuiIngame;
import frutty.gui.Settings;
import frutty.map.Map;
import frutty.map.base.MapZone;

public abstract class Entity implements Serializable{
	private static final long serialVersionUID = 2876462867774051456L;
	
	public boolean active = true;
	public int renderPosX, renderPosY, serverPosX, serverPosY, motionX, motionY;
	
	public Entity() {}
	public Entity(int x, int y) {
		renderPosX = x;
		renderPosY = y;
		serverPosX = x;
		serverPosY = y;
	}
	
	protected EnumFacing findFreeFacing() {
		for(EnumFacing randomFacing = EnumFacing.randomFacing(); ; randomFacing = EnumFacing.randomFacing()) {
			if(MapZone.isEmpty(serverPosX + randomFacing.xOffset, serverPosY + randomFacing.yOffset)) {
				return randomFacing;
			}
			continue;
		}
	}
	
	protected boolean doesCollide(int x, int y) {
		return (serverPosX >= x && serverPosX + 64 <= x + 64) &&
			   (serverPosY >= y && serverPosY + 64 <= y + 64);
	}
	
	protected void checkPlayers() {
		for(EntityPlayer player : Map.players) {
			if(doesCollide(player.serverPosX, player.serverPosY)) {
				if(Settings.godEnabled || player.isInvicible()) {
					active = false;
					Map.score += 100;
				}else{
					GuiIngame.showMessageAndClose("Game over!");
				}
			}
		}
	}
	
	public static int coordsToIndex(int x, int y) {
		return x / 64 + (y / 64 * ((Map.width + 64) / 64));
	}
	
	public void render(Graphics graphics) {
		if(Settings.debugCollisions) {
			graphics.setColor(Color.BLUE);
			graphics.drawRect(serverPosX, serverPosY, 64, 64);
			graphics.setColor(Color.RED);
			graphics.drawRect(renderPosX, renderPosY, 64, 64);
		}
	}
	
	public abstract void update(int ticks);
	
	public static enum EnumFacing {
		UP(0, -64, 2),
		DOWN(0, 64, 1),
		LEFT(-64, 0, 3),
		RIGHT(64, 0, 0);
		
		public final int xOffset, yOffset, textureIndex;
		private EnumFacing(int x, int y, int index) {
			xOffset = x;
			yOffset = y;
			textureIndex = index;
		}

		public static EnumFacing randomFacing() {
			switch(Main.rand.nextInt(4)) {
				case 0: return UP;
				case 1: return DOWN;
				case 2: return LEFT;
				default: return RIGHT;
			}
		}
	}
}