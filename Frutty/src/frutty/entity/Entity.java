package frutty.entity;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.util.Random;

import javax.imageio.ImageIO;

import frutty.gui.GuiIngame;

/**
 * Fõ entity class, Minden entity ebbõl a classból indul ki, abstract függvényei a GuiIngame class-ban
 * hívódnak be (render && update)
 * Serializable class, mivel mentéskor minden egyes entity-t kiírunk 1 fájlba Java Serializációval
 */
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
	
	/**
	 * Textúra betöltésre szolgáló függvény, szép mert elintézi az exception kezelést.
	 * @param path Textúra neve
	 * @return A BufferedImage-t ha van az úton textúra, egyébként null
	 */
	protected static BufferedImage loadTexture(String path) {
		try {
			return ImageIO.read(GuiIngame.class.getResource("/textures/" + path));
		} catch (IOException e) {
			System.err.println("Can't find texture: " + path + ", returning null. Have fun :)");
			return null;
		}
	}
	
	/**
	 * Ezt a függvény overrideolja az összes entity, textúra rajzolás/egyéb dolgok rajzolása
	 * @param graphics Graphics object
	 */
	public abstract void render(Graphics graphics);
	/**
	 * Ezt a függvényt overrideolja az összes entity, update logikák történnek itt
	 * @param ticks A map tick számlálója
	 */
	public abstract void update(int ticks);
}