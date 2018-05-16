package frutty.map.zones;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import frutty.map.MapZone;

public final class MapZoneWater extends MapZone {
	private static final BufferedImage[] textures = getWaterTextures();
	private static boolean decrease = false;
	private static int textureIndex = 0;
	
	public MapZoneWater(int xPos, int yPos, int index) {
		super(xPos, yPos, index);
	}

	private static BufferedImage[] getWaterTextures() {
		BufferedImage main = loadTexture("special/water.png");
		BufferedImage[] water = new BufferedImage[32];
		
		for(int k = 0; k < 32; ++k) {
			water[k] = main.getSubimage(0, k * 16, 16, 16);
		}
		return water;
	}
	
	@Override
	public void draw(Graphics graphics) {
		graphics.drawImage(textures[textureIndex], posX, posY, 64, 64, null);
		super.draw(graphics);
	}
	
	public static void updateWaterUV() {
		if(decrease) {
			--textureIndex;
		}else{
			++textureIndex;
		}
		
		if(textureIndex == 0) {
			decrease = false;
		}
		if(textureIndex == 31) {
			decrease = true;
		}
	}
	
	@Override
	public boolean renderBeforePlayer() {
		return false;
	}
	
	@Override
	public boolean isBreakable() {
		return true;
	}
}