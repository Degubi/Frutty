package frutty.world;

import frutty.*;
import frutty.gui.GuiSettings.*;
import frutty.tools.*;
import java.awt.*;
import java.io.*;
import java.util.*;

public final class Particle implements Serializable{
	private static final long serialVersionUID = -9182849456014867036L;

	public final Color color;
	public int lifeTime, posX, posY;
	public final int motionX, motionY;
	
	private Particle(int x, int y, boolean fall, Color color) {
		posX = x;
		posY = y;
		
		if(fall) {
			motionX = 0;
			motionY = 2 + FruttyMain.rand.nextInt(3);
		}else{
			motionX = -2 + FruttyMain.rand.nextInt(5);
			motionY = -2 + FruttyMain.rand.nextInt(2);
		}
		
		lifeTime = 25 + FruttyMain.rand.nextInt(20);
		this.color = color;
	}
	
	public static void spawnFallingParticles(int count, int x, int y, Material material) {
		if(Settings.graphicsLevel == 2) {
			var rand = FruttyMain.rand;
			var color = material.particleColor;
			var particles = World.particles;
			
			for(int k = 0; k < count; ++k) {
				particles.add(new Particle(x + rand.nextInt(64), y + 64 + rand.nextInt(32), true, color));
			}
		}
	}
	
	public static void spawnRandomParticles(int count, int x, int y, Color color) {
		if(Settings.graphicsLevel == 2) {
			var particles = World.particles;
			
			for(int k = 0; k < count; ++k) {
				particles.add(new Particle(x + 32, y + 60, false, color));
			}
		}
	}
	
	public void render(Graphics graphics) {
		var posXLocal = posX;
		var posYLocal = posY;

		graphics.setColor(color);
		graphics.fillRect(posXLocal, posYLocal, 4, 4);
		graphics.setColor(GuiHelper.color_84Black);
		
		for(int till = posYLocal / 240, k = 0; k < till && k < 4; ++k) {
			graphics.fillRect(posXLocal, posYLocal, 4, 4);
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