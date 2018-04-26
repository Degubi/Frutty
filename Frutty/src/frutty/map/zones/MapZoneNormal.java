package frutty.map.zones;

import java.awt.Graphics;

import frutty.map.Map;
import frutty.map.MapZone;

public class MapZoneNormal extends MapZone{
	public MapZoneNormal(int xPos, int yPos, int zoneIndex) {
		super(xPos, yPos, zoneIndex);
	}
	
	@Override
	public void draw(Graphics graphics) {
		if(posY < 100) {
			graphics.drawImage(Map.currentMap.textures[0], posX, posY, 64, 64, null);
		}else if(posY > 100 && posY < 300) {
			graphics.drawImage(Map.currentMap.textures[1], posX, posY, 64, 64, null);
		}else if(posY > 300 && posY < 400) {
			graphics.drawImage(Map.currentMap.textures[2], posX, posY, 64, 64, null);
		}else if(posY > 400 && posY < 600) {
			graphics.drawImage(Map.currentMap.textures[3], posX, posY, 64, 64, null);
		}else{
			graphics.drawImage(Map.currentMap.textures[4], posX, posY, 64, 64, null);
		}
	}
}