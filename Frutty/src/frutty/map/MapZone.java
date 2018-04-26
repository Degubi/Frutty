package frutty.map;

import java.awt.Graphics;
import java.io.Serializable;

import frutty.gui.GuiStats;
import frutty.map.zones.MapZoneEmpty;
import frutty.map.zones.MapZoneFruit;

public abstract class MapZone implements Serializable{
	private static final long serialVersionUID = 392316063689927131L;
	
	public final int posX, posY, zoneIndex;
	
	public MapZone(int xPos, int yPos, int index) {
		posX = xPos;
		posY = yPos;
		zoneIndex = index;
	}
	
	public static boolean isEmpty(int x, int y) {
		MapZone zone = Map.getZoneAtPos(x, y);
		return zone != null && zone instanceof MapZoneEmpty;
	}
	
	public void onBreak() {
		Map.setZoneEmptyAt(zoneIndex);
		MapZone up = Map.getZoneAtPos(posX, posY - 64);
		++GuiStats.zoneCount;
		if(up != null && up instanceof MapZoneFruit) {
			((MapZoneFruit)up).notified = true;
		}
	}

	public abstract void draw(Graphics graphics);
}