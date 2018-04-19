package frutty.map.zones;

import java.awt.Graphics;

import frutty.entity.EntityApple;
import frutty.map.Map;
import frutty.map.MapZone;
import frutty.stuff.EnumFruit;
import frutty.stuff.ITickable;

public class MapZoneFruit extends MapZone implements ITickable{
	public final EnumFruit fruitType;
	private int counter;  //Kell azért hogy so-so ugyanakkor essen le az alma
	
	public MapZoneFruit(int xPos, int yPos, EnumFruit type, int zoneIndex) {
		super(xPos, yPos, zoneIndex);
		fruitType = type;
	}

	@Override
	public void draw(Graphics graphics) {
		if(posY < 100) {
			graphics.drawImage(Map.currentMap.textures[0], posX, posY, null);
		}else if(posY > 100 && posY < 300) {
			graphics.drawImage(Map.currentMap.textures[1], posX, posY, null);
		}else if(posY > 300 && posY < 400) {
			graphics.drawImage(Map.currentMap.textures[2], posX, posY, null);
		}else if(posY > 400 && posY < 600) {
			graphics.drawImage(Map.currentMap.textures[3], posX, posY, null);
		}else{
			graphics.drawImage(Map.currentMap.textures[4], posX, posY, null);
		}
		
		if(fruitType == EnumFruit.APPLE) {
			graphics.drawImage(EntityApple.appleTexture, posX, posY, null);
		}else if(fruitType == EnumFruit.CHERRY) {
			graphics.drawImage(EntityApple.cherryTexture, posX, posY, null);
		}
	}
	
	@Override
	public void update() {
		if(notified){
			if(counter > 1) {
				Map.setZoneEmptyAt(zoneIndex);
				Map.currentMap.entities.add(new EntityApple(posX, posY));
			}else {
				++counter;
			}
		}
	}
}