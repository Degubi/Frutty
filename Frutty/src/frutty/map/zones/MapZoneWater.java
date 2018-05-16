package frutty.map.zones;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import frutty.map.MapZone;

public final class MapZoneWater extends MapZone {
	private static final BufferedImage texture = loadTexture("special/water.png");
	private static boolean decrease = false;
	private static int textureY = 0;
	
	public MapZoneWater(int xPos, int yPos, int index) {
		super(xPos, yPos, index);
	}
	
	@Override
	public void draw(Graphics graphics) {
		graphics.setColor(Color.BLACK);
		graphics.fillRect(posX, posY, 64, 64);
	}
	
	public void drawAfter(Graphics graphics) {
		graphics.drawImage(texture, posX, posY, posX + 64, posY + 64, 0, textureY, 16, textureY + 16, null);
		super.draw(graphics);
	}
	
	public static void updateWaterUV() {
		if(decrease) {
			textureY -= 16;
		}else{
			textureY += 16;
		}
		
		if(textureY == 0) {
			decrease = false;
		}
		if(textureY == 448) {
			decrease = true;
		}
	}
	
	@Override
	public boolean isBreakable() {
		return true;
	}
}