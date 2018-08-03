package frutty.world;

import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;
import java.util.Iterator;

import frutty.Main;
import frutty.gui.GuiIngame;
import frutty.gui.GuiSettings.Settings;
import frutty.tools.GuiHelper;

public final class Particle implements Serializable{
	private static final long serialVersionUID = -9182849456014867036L;

	private static Color[] colors;
	
	public final Color color;
	public int lifeTime, posX, posY;
	public final int motionX, motionY;
	
	private Particle(int x, int y, int colorIndex) {
		posX = x;
		posY = y;
		lifeTime = 25 + Main.rand.nextInt(20);
		motionX = 0;
		motionY = 2 + Main.rand.nextInt(3);
		color = colors[colorIndex];
	}
	
	private Particle(int x, int y, Color color) {
		posX = x;
		posY = y;
		lifeTime = 25 + Main.rand.nextInt(20);
		motionX = -2 + Main.rand.nextInt(5);
		motionY = -2 + Main.rand.nextInt(2);
		this.color = color;
	}
	
	public static void spawnFallingParticles(int count, int x, int y, int color) {
		if(Settings.graphicsLevel == 2) {
			for(int k = 0; k < count; ++k) {
				World.particles.add(new Particle(x + Main.rand.nextInt(64), y + 64 + Main.rand.nextInt(32), color));
			}
		}
	}
	
	public static void spawnRandomParticles(int count, int x, int y, Color color) {
		if(Settings.graphicsLevel == 2) {
			for(int k = 0; k < count; ++k) {
				World.particles.add(new Particle(x + 32, y + 60, color));
			}
		}
	}
	
	public static void precacheParticles() {
		colors = new Color[GuiIngame.textures.length];
		
		for(int k = 0; k < GuiIngame.textures.length; ++k) {
			colors[k] = new Color(GuiIngame.textures[k].getRGB(2, 2));
		}
	}
	
	public void render(Graphics graphics) {
		graphics.setColor(color);
		graphics.fillRect(posX, posY, 4, 4);
		
		graphics.setColor(GuiHelper.color_84Black);
		int till = posY / 240;
		for(int k = 0; k < till && k < 4; ++k) {
			graphics.fillRect(posX, posY, 4, 4);
		}
	}
	
	public void update(Iterator<Particle> iterator) {
		posY += motionY;
		posX += motionX;
		if(--lifeTime == 0) {
			iterator.remove();
		}
	}
}