package frutty.entity;

import frutty.*;
import frutty.gui.GuiSettings.*;
import frutty.world.*;
import java.awt.*;
import java.io.*;

public abstract class Entity implements Serializable{
	private static final long serialVersionUID = 2876462867774051456L;
	
	public int renderPosX, renderPosY, serverPosX, serverPosY, motionX, motionY;
	public final int moveRate;
	
	public Entity() {
		moveRate = 0;
	}
	public Entity(int x, int y) {
		renderPosX = x;
		renderPosY = y;
		serverPosX = x;
		serverPosY = y;
		moveRate = getServerUpdateRate() / getClientUpdateRate();
	}
	
	public abstract void render(Graphics graphics);
	public abstract void updateClient();
	public abstract void updateServer();
	public abstract int getClientUpdateRate();
	public abstract int getServerUpdateRate();
	public void onKilled(@SuppressWarnings("unused") Entity killer) {}
	
	/**************************************************INTERNALS**************************************************/
	
	
	protected final EnumFacing findFreeFacing() {
		var serverPosXLocal = serverPosX;
		var serverPosYLocal = serverPosY;
		
		for(var randomFacing = EnumFacing.randomFacing(); ; randomFacing = EnumFacing.randomFacing()) {
			if(World.isPositionFree(serverPosXLocal + randomFacing.xOffset, serverPosYLocal + randomFacing.yOffset)) {
				return randomFacing;
			}
			continue;
		}
	}
	
	protected final boolean doesCollide(Entity other) {
		var posX = renderPosX;
		var posY = renderPosY;
		var otherPosX = other.renderPosX;
		var otherPosY = other.renderPosY;
		
		return posX < otherPosX + 64 && 
			   posX + 64 > otherPosX &&
			   posY < otherPosY + 64 && 
			   posY + 64 > otherPosY;
	}
	
	protected final void checkPlayers() {
		var godEnabled = Settings.enableGod;
		
		for(var player : World.players) {
			if(!player.hidden && doesCollide(player)) {
				if(godEnabled || player.isInvicible()) {
					onKilled(player);
				}else{
					if(this instanceof EntityFalling) {
						((EntityFalling)this).onFallStopped();
					}
					player.onKilled(this);
				}
			}
		}
	}
	
	public final void renderDebug(Graphics graphics) {
		render(graphics);
		
		graphics.setColor(Color.BLUE);
		graphics.drawRect(serverPosX, serverPosY, 64, 64);
		graphics.setColor(Color.RED);
		graphics.drawRect(renderPosX, renderPosY, 64, 64);
	}
	
	public final void updateInternal(int ticks) {
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