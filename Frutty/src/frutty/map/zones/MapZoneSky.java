package frutty.map.zones;

import java.awt.Graphics;

import frutty.gui.GuiIngame;
import frutty.map.MapZone;

public final class MapZoneSky extends MapZone{
	public MapZoneSky(int xPos, int yPos, int index) {
		super(xPos, yPos, index);
	}

	@Override
	public void draw(Graphics graphics) {
		graphics.drawImage(GuiIngame.skyTexture, posX, posY, posX + 64, posY + 64, posX, posY, posX + 64, posY + 64, null);
	}
	
	@Override
	public boolean isBreakable() {
		return false;
	}
}