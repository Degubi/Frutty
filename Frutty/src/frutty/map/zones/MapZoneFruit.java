package frutty.map.zones;

import java.awt.Graphics;

import frutty.entity.EntityApple;
import frutty.gui.GuiIngame;
import frutty.gui.GuiStats;
import frutty.map.Map;
import frutty.map.MapZone;
import frutty.stuff.EnumFruit;

public final class MapZoneFruit extends MapZone{
	public final EnumFruit fruitType;
	public boolean notified;
	private int counter;
	
	public MapZoneFruit(int xPos, int yPos, EnumFruit type, int zoneIndex) {
		super(xPos, yPos, zoneIndex);
		fruitType = type;
	}

	@Override
	public void onBreak() {
		Map.setZoneEmptyAt(zoneIndex);
		MapZone up = Map.getZoneAtPos(posX, posY - 64);
		++GuiStats.zoneCount;
		if(up != null && up instanceof MapZoneFruit) {
			((MapZoneFruit)up).notified = true;
		}
		
		Map.currentMap.score += 50;
		if(--Map.currentMap.pickCount == 0) {
			GuiIngame.showMessageAndClose("You won!");
			GuiStats.compareScores();
		}
	}
	
	@Override
	public void draw(Graphics graphics) {
		graphics.drawImage(GuiIngame.texture, posX, posY, 64, 64, null);
		
		if(fruitType == EnumFruit.APPLE) {
			graphics.drawImage(EntityApple.appleTexture, posX, posY, null);
		}else if(fruitType == EnumFruit.CHERRY) {
			graphics.drawImage(EntityApple.cherryTexture, posX, posY, null);
		}
		super.draw(graphics);
	}
	
	public void update() {
		if(notified){
			if(counter > 1) {
				Map.setZoneEmptyAt(zoneIndex);
				Map.currentMap.entities.add(new EntityApple(posX, posY));
			}else{
				++counter;
			}
		}
	}

	@Override
	public boolean isPassable() {
		return fruitType == EnumFruit.CHERRY;
	}
}