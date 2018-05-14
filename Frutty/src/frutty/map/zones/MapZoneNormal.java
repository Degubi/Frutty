package frutty.map.zones;

import java.awt.Graphics;

import frutty.gui.GuiIngame;
import frutty.map.MapZone;

public final class MapZoneNormal extends MapZone{
	public MapZoneNormal(int xPos, int yPos, int zoneIndex) {
		super(xPos, yPos, zoneIndex);
	}

	@Override
	public void draw(Graphics graphics) {
		graphics.drawImage(GuiIngame.texture, posX, posY, 64, 64, null);
		super.draw(graphics);
	}

	@Override
	public boolean isBreakable() {
		return true;
	}
}