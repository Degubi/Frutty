package frutty.entity;

import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;

import frutty.Main;
import frutty.gui.GuiIngame;
import frutty.gui.Settings;
import frutty.world.World;
import frutty.world.interfaces.MapZoneBase;

public abstract class Entity implements Serializable{
	private static final long serialVersionUID = 2876462867774051456L;
	
	public boolean active = true;
	public int renderPosX, renderPosY, serverPosX, serverPosY, motionX, motionY, moveRate;
	
	public Entity() {}
	public Entity(int x, int y) {
		renderPosX = x;
		renderPosY = y;
		serverPosX = x;
		serverPosY = y;
		moveRate = getServerUpdateRate() / getClientUpdateRate();
	}
	
	protected final EnumFacing findFreeFacing() {
		for(var randomFacing = EnumFacing.randomFacing(); ; randomFacing = EnumFacing.randomFacing()) {
			if(canGo(serverPosX + randomFacing.xOffset, serverPosY + randomFacing.yOffset)) {
				return randomFacing;
			}
			continue;
		}
	}
	
	protected final boolean canGo(int x, int y) {
		if(x < 0 || x > World.width || y < 0 || y > World.height) {
			return false;
		}
		MapZoneBase zone = World.getZoneAtIndex(coordsToIndex(x, y));
		return zone != null && zone.canNPCPass(x, y);
	}
	
	protected final boolean doesCollide(int x, int y) {
		return (serverPosX >= x && serverPosX + 64 <= x + 64) &&
			   (serverPosY >= y && serverPosY + 64 <= y + 64);
	}
	
	protected final void checkPlayers(boolean addScore) {
		for(var player : World.players) {
			if(!player.hidden && doesCollide(player.serverPosX, player.serverPosY)) {
				if(Settings.godEnabled || player.isInvicible()) {
					active = false;
					if(addScore) {
						World.score += 100;
					}
				}else{
					GuiIngame.showMessageAndClose("Game over!");
				}
			}
		}
	}
	
	protected final void checkEnemies() {
		for(var enemies : World.enemies) {
			if(doesCollide(enemies.serverPosX, enemies.serverPosY)) {
				enemies.active = false;
			}
		}
	}
	
	public static int coordsToIndex(int x, int y) {
		return x / 64 + (y / 64 * ((World.width + 64) / 64));
	}
	
	public final void handleRender(Graphics graphics) {
		render(graphics);
		
		if(Settings.debugCollisions) {
			graphics.setColor(Color.BLUE);
			graphics.drawRect(serverPosX, serverPosY, 64, 64);
			graphics.setColor(Color.RED);
			graphics.drawRect(renderPosX, renderPosY, 64, 64);
		}
	}
	
	public abstract void render(Graphics graphics);
	public abstract void updateClient();
	public abstract void updateServer();
	public abstract int getClientUpdateRate();
	public abstract int getServerUpdateRate();
	
	public final void update(int ticks) {
		if(ticks % getClientUpdateRate() == 0) {
			updateClient();
			renderPosX += motionX / moveRate;
			renderPosY += motionY / moveRate;
		}
		if(ticks % getServerUpdateRate() == 0) {
			updateServer();
			serverPosX += motionX;
			serverPosY += motionY;
		}
	}
	
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