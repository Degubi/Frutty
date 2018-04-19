package frutty.entity;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.util.Random;

import javax.imageio.ImageIO;

import frutty.gui.GuiIngame;

public abstract class Entity implements Serializable{
	private static final long serialVersionUID = 2876462867774051456L;
	protected static final Random rand = GuiIngame.rand;
	
	public boolean active = true;
	public int posX, posY, motionX, motionY;
	
	public Entity() {}
	public Entity(int x, int y) {
		posX = x;
		posY = y;
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