package frutty.map;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Iterator;

import frutty.Main;
import frutty.gui.GuiHelper;
import frutty.gui.GuiSettings.Settings;

public final class Particle {
	private static final Color[] colors = {new Color(202, 203, 87), new Color(127, 127, 127), new Color(150, 108, 74), new Color(178, 99, 78)};
	
	public static int colorIndex = 0;
	public int lifeTime, posX, posY;
	public final int motionY;
	
	public Particle(int x, int y) {
		posX = x;
		posY = y;
		lifeTime = 5 + Main.rand.nextInt(5);
		motionY = 1 + Main.rand.nextInt(3);
	}
	
	public static void addParticles(int count, int x, int y) {
		if(Settings.graphicsLevel == 2) {
			for(int k = 0; k < count; ++k) {
				Map.currentMap.particles.add(new Particle(x + Main.rand.nextInt(64), y + 64 + Main.rand.nextInt(32)));
			}
		}
	}
	
	public void render(Graphics graphics) {
		graphics.setColor(colors[colorIndex]);
		graphics.fillRect(posX, posY += motionY, 4, 4);
		
		graphics.setColor(GuiHelper.color_84Black);
		int till = posY / 240;
		for(int k = 0; k < till && k < 4; ++k) {
			graphics.fillRect(posX, posY, 4, 4);
		}
	}
	
	public void update(Iterator<Particle> iterator) {
		if(--lifeTime == 0) {
			iterator.remove();
		}
	}
}