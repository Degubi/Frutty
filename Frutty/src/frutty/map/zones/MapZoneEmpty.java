package frutty.map.zones;

import java.awt.Graphics;

import frutty.map.MapZone;

public final class MapZoneEmpty extends MapZone{
	public MapZoneEmpty(int xPos, int yPos, int zoneIndex) {
		super(xPos, yPos, zoneIndex);
	}

	@Override
	public void draw(Graphics graphics) {}

	@Override
	public boolean isBreakable() {
		return true;
	}
}