package frutty.world;

import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;
import java.util.Iterator;

import frutty.Main;
import frutty.gui.GuiHelper;
import frutty.gui.GuiIngame;
import frutty.gui.Settings;

public final class Particle implements Serializable{
	private static final long serialVersionUID = -9182849456014867036L;

	private static Color[] colors;
	
	public final int colorIndex;
	public int lifeTime, posX, posY;
	public final int motionY;
	
	public Particle(int x, int y, int colorIndex) {
		posX = x;
		posY = y;
		lifeTime = 25 + Main.rand.nextInt(20);
		motionY = 2 + Main.rand.nextInt(3);
		this.colorIndex = colorIndex;
	}
	
	public static void addParticles(int count, int x, int y, int color) {
		if(Settings.graphicsLevel == 2) {
			for(int k = 0; k < count; ++k) {
				World.particles.add(new Particle(x + Main.rand.nextInt(64), y + 64 + Main.rand.nextInt(32), color));
			}
		}
	}
	
	public static void precacheParticles() {
		colors = new Color[GuiIngame.textures.length];
		
		for(int k = 0; k < GuiIngame.textures.length; ++k) {
			colors[k] = new Color(GuiIngame.textures[k].getRGB(2, 2), true);
		}
	}
	
	public void render(Graphics graphics) {
		graphics.setColor(colors[colorIndex]);
		graphics.fillRect(posX, posY, 4, 4);
		
		graphics.setColor(GuiHelper.color_84Black);
		int till = posY / 240;
		for(int k = 0; k < till && k < 4; ++k) {
			graphics.fillRect(posX, posY, 4, 4);
		}
	}
	
	public void update(Iterator<Particle> iterator) {
		posY += motionY;
		if(--lifeTime == 0) {
			iterator.remove();
		}
	}
}