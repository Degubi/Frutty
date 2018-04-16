package frutty.map.zones;

import java.awt.Color;
import java.awt.Graphics;

import frutty.map.MapZone;

/**
 * Üres zóna class, semmi speciálisat nem csinál, nincs textúra csak 1 fekete rectangle-t rajzol
 */
public class MapZoneEmpty extends MapZone{
	public MapZoneEmpty(int xPos, int yPos, int zoneIndex) {
		super(xPos, yPos, zoneIndex);
	}

	@Override
	public void draw(Graphics graphics) {
		graphics.setColor(Color.BLACK);
		graphics.fillRect(posX, posY, 64, 64);
	}
}