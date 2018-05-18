package frutty.entity;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.ImageIO;

import frutty.Main;
import frutty.gui.GuiIngame;
import frutty.gui.GuiSettings.Settings;
import frutty.map.Map;
import frutty.map.MapZone;

//Coord to index: posX / 64 + (posY / 64 * ((Map.currentMap.width + 64) / 64))
public abstract class Entity implements Serializable{
	private static final long serialVersionUID = 2876462867774051456L;
	
	public boolean active = true;
	public int posX, posY, serverPosX, serverPosY, motionX, motionY;
	
	public Entity() {}
	public Entity(int x, int y) {
		posX = x;
		posY = y;
		serverPosX = x;
		serverPosY = y;
	}
	
	protected EnumFacing findFreeFacing() {
		for(EnumFacing randomFacing = EnumFacing.randomFacing(); ; randomFacing = EnumFacing.randomFacing()) {
			if(MapZone.isEmpty(posX + randomFacing.xOffset, posY + randomFacing.yOffset)) {
				return randomFacing;
			}
			continue;
		}
	}
	
	protected void checkPlayer(boolean checkInterp) {
		for(EntityPlayer player : Map.currentMap.players) {
			if((serverPosX == player.serverPosX && serverPosY == player.serverPosY) || checkInterp 
					&& ((serverPosY + motionY == player.serverPosY && serverPosX + motionX == player.serverPosX))) {
				if(Settings.godEnabled || player.isInvicible()) {
					active = false;
					Map.currentMap.score += 100;
				}else{
					GuiIngame.showMessageAndClose("Game over!");
				}
			}
		}
	}
	
	protected static BufferedImage loadTexture(String path) {
		try{
			return ImageIO.read(new File("./textures/" + path));
		}catch(IOException e){
			System.err.println("Can't find texture: " + path + ", returning null. Have fun :)");
			return null;
		}
	}
	
	public void render(Graphics graphics) {
		if(Settings.debugCollisions) {
			graphics.setColor(Color.WHITE);
			graphics.drawRect(serverPosX, serverPosY, 64, 64);
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