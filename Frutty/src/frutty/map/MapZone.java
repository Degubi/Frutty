package frutty.map;

import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;

import frutty.map.zones.MapZoneEmpty;

public abstract class MapZone implements Serializable{
	private static final long serialVersionUID = 392316063689927131L;
	private static final Color blak = new Color(0, 0, 0, 96);
	
	public final int posX, posY, zoneIndex;
	
	public MapZone(int xPos, int yPos, int index) {
		posX = xPos;
		posY = yPos;
		zoneIndex = index;
	}
	
	protected final void renderDepth(Graphics graphics) {
		graphics.setColor(blak);
		
		switch(posY / 100) {   //Tableswitch, o1-es teljesítmény érdekében
			case 0: break;
			case 1: case 2: graphics.fillRect(posX, posY, 64, 64); break;
			case 3: case 4: graphics.fillRect(posX, posY, 64, 64);
							graphics.fillRect(posX, posY, 64, 64); break;
			case 5: case 6: graphics.fillRect(posX, posY, 64, 64);
							graphics.fillRect(posX, posY, 64, 64);
							graphics.fillRect(posX, posY, 64, 64); break;
			default: 		graphics.fillRect(posX, posY, 64, 64);
							graphics.fillRect(posX, posY, 64, 64);
							graphics.fillRect(posX, posY, 64, 64);
							graphics.fillRect(posX, posY, 64, 64);
		}
	}
	
	public static boolean isEmpty(int x, int y) {
		MapZone zone = Map.getZoneAtPos(x, y);
		return zone != null && zone instanceof MapZoneEmpty;
	}
	
	public void onBreak() {}
	public abstract boolean isPassable();
	public abstract void draw(Graphics graphics);
}