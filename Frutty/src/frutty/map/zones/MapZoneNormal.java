package frutty.map.zones;

import java.awt.Graphics;

import frutty.gui.GuiIngame;
import frutty.map.MapZone;

public final class MapZoneNormal extends MapZone{
	private final int textureIndex;
	
	public MapZoneNormal(int xPos, int yPos, int zoneIndex, int textIndex) {
		super(xPos, yPos, zoneIndex);
		textureIndex = textIndex;
	}

	@Override
	public void draw(Graphics graphics) {
		graphics.drawImage(GuiIngame.textures[textureIndex], posX, posY, 64, 64, null);
		super.draw(graphics);
	}

	@Override
	public int getParticleIndex() {
		return textureIndex;
	}
	
	@Override
	public boolean isBreakable() {
		return true;
	}
}