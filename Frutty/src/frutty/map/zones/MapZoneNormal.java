package frutty.map.zones;

import java.awt.Graphics;

import frutty.gui.GuiStats;
import frutty.map.Map;
import frutty.map.MapZone;

public class MapZoneNormal extends MapZone{
	public MapZoneNormal(int xPos, int yPos, int zoneIndex) {
		super(xPos, yPos, zoneIndex);
	}
	
	@Override
	public void onBreak() {
		Map.setZoneEmptyAt(zoneIndex);
		MapZone up = Map.getZoneAtPos(posX, posY - 64);
		++GuiStats.zoneCount;
		if(up != null && up instanceof MapZoneFruit) {
			((MapZoneFruit)up).notified = true;
		}
	}
	
	@Override
	public void draw(Graphics graphics) {
		graphics.drawImage(Map.currentMap.texture, posX, posY, 64, 64, null);
		renderDepth(graphics);
	}

	@Override
	public boolean isPassable() {
		return true;
	}
}