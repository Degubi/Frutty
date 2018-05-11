package frutty.entity;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.ImageIO;

import frutty.gui.GuiIngame;
import frutty.gui.GuiSettings.Settings;
import frutty.map.Map;
import frutty.map.MapZone;
import frutty.stuff.EnumFacing;

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
				if(!Settings.godEnabled) {
					GuiIngame.showMessageAndClose("Game over!");
				}else{
					active = false;
					Map.currentMap.score += 100;
				}
			}
		}
	}
	
	protected static BufferedImage loadTexture(String path) {
		try{
			return ImageIO.read(GuiIngame.class.getResource("/textures/" + path));
		}catch(IOException e){
			System.err.println("Can't find texture: " + path + ", returning null. Have fun :)");
			return null;
		}
	}
	
	public void render(Graphics graphics) {
		graphics.setColor(Color.WHITE);
		graphics.drawRect(serverPosX, serverPosY, 64, 64);
	}
	
	public abstract void update(int ticks);
}