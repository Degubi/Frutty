package frutty.map.zones;

import java.awt.Graphics;

import frutty.gui.GuiIngame;
import frutty.map.base.MapZone;

public final class MapZoneSky extends MapZone{
	public MapZoneSky() {
		super(9, false);
	}

	@Override
	public void draw(int x, int y, int textureIndex, Graphics graphics) {
		graphics.drawImage(GuiIngame.skyTexture, x, y, x + 64, y + 64, x, y, x + 64, y + 64, null);
	}
	
	@Override
	public boolean isBreakable(int x, int y) {
		return false;
	}
}