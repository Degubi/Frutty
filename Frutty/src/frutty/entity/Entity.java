package frutty.entity;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.ImageIO;

import frutty.gui.GuiIngame;
import frutty.map.MapZone;
import frutty.stuff.EnumFacing;

public abstract class Entity implements Serializable{
	private static final long serialVersionUID = 2876462867774051456L;
	
	public boolean active = true;
	public int posX, posY, motionX, motionY;
	
	public Entity() {}
	public Entity(int x, int y) {
		posX = x;
		posY = y;
	}
	
	protected EnumFacing findFreeFacing() {
		for(EnumFacing randomFacing = EnumFacing.randomFacing(); ; randomFacing = EnumFacing.randomFacing()) {
			if(MapZone.isEmpty(posX + randomFacing.xOffset, posY + randomFacing.yOffset)) {
				return randomFacing;
			}
			continue;
		}
	}
	
	protected static BufferedImage loadTexture(String path) {
		try {
			return ImageIO.read(GuiIngame.class.getResource("/textures/" + path));
		} catch (IOException e) {
			System.err.println("Can't find texture: " + path + ", returning null. Have fun :)");
			return null;
		}
	}
	
	public abstract void render(Graphics graphics);
	public abstract void update(int ticks);
}