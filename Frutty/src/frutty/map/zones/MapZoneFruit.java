package frutty.map.zones;

import java.awt.Graphics;

import frutty.entity.EntityApple;
import frutty.entity.EntityPlayer;
import frutty.gui.GuiIngame;
import frutty.gui.GuiStats;
import frutty.map.Map;
import frutty.map.MapZone;

public final class MapZoneFruit extends MapZone{
	public final EnumFruit fruitType;
	public boolean notified;
	private int counter;
	private final int textureIndex;
	
	public MapZoneFruit(int xPos, int yPos, EnumFruit type, int zoneIndex, int textIndex) {
		super(xPos, yPos, zoneIndex);
		fruitType = type;
		textureIndex = textIndex;
	}

	@Override
	public void onBreak(EntityPlayer player) {
		super.onBreak(player);
		
		Map.currentMap.score += 50;
		if(--Map.currentMap.pickCount == 0) {
			GuiIngame.showMessageAndClose("You won!");
			GuiStats.compareScores();
		}
	}
	
	@Override
	public void draw(Graphics graphics) {
		graphics.drawImage(GuiIngame.textures[textureIndex], posX, posY, 64, 64, null);
		
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
	public int getParticleIndex() {
		return textureIndex;
	}
	
	@Override
	public boolean isBreakable() {
		return fruitType == EnumFruit.CHERRY;
	}
	
	public static enum EnumFruit{
		APPLE,
		CHERRY;
	}
}