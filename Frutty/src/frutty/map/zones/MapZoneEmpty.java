package frutty.map.zones;

import java.awt.Color;
import java.awt.Graphics;

import frutty.map.base.MapZone;

public final class MapZoneEmpty extends MapZone{
	public MapZoneEmpty() {
		super(1, false);
	}

	@Override
	public void draw(int x, int y, int textureIndex, Graphics graphics) {
		graphics.setColor(Color.BLACK);
		graphics.fillRect(x, y, 64, 64);
	}

	@Override
	public boolean isBreakable(int x, int y) {
		return true;
	}
}