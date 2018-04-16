package frutty.entity;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.util.Random;

import javax.imageio.ImageIO;

import frutty.gui.GuiIngame;

/**
 * F� entity class, Minden entity ebb�l a classb�l indul ki, abstract f�ggv�nyei a GuiIngame class-ban
 * h�v�dnak be (render && update)
 * Serializable class, mivel ment�skor minden egyes entity-t ki�runk 1 f�jlba Java Serializ�ci�val
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
	 * Text�ra bet�lt�sre szolg�l� f�ggv�ny, sz�p mert elint�zi az exception kezel�st.
	 * @param path Text�ra neve
	 * @return A BufferedImage-t ha van az �ton text�ra, egy�bk�nt null
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
	 * Ezt a f�ggv�ny overrideolja az �sszes entity, text�ra rajzol�s/egy�b dolgok rajzol�sa
	 * @param graphics Graphics object
	 */
	public abstract void render(Graphics graphics);
	/**
	 * Ezt a f�ggv�nyt overrideolja az �sszes entity, update logik�k t�rt�nnek itt
	 * @param ticks A map tick sz�ml�l�ja
	 */
	public abstract void update(int ticks);
}