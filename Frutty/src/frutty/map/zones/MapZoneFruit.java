package frutty.map.zones;

import java.awt.Graphics;

import frutty.entity.EntityApple;
import frutty.map.Map;
import frutty.map.MapZone;
import frutty.stuff.EnumFruit;
import frutty.stuff.ITickable;

/**
 * Gy�m�lcs z�na class, ez kezeli a physics EntityApple l�trehoz�st is az update() f�ggv�nyben
 */
public class MapZoneFruit extends MapZone implements ITickable{
	public final EnumFruit fruitType;
	private int counter;  //Kell az�rt hogy so-so ugyanakkor essen le az alma
	
	/**
	 * 1 db plusz param�ter
	 * @param type Gy�m�lcs type
	 */
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
	
	/**
	 * Update f�ggv�ny, GuiIngame-ben h�v�dik be
	 */
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