package frutty.map.zones;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import frutty.entity.EntityPlayer;
import frutty.entity.effects.EntityEffectInvisible;
import frutty.gui.GuiIngame;
import frutty.gui.GuiStats;
import frutty.map.Map;
import frutty.map.MapZone;

public final class MapZoneChest extends MapZone{
	private static final BufferedImage chest = loadTexture("chest.png");
	
	public MapZoneChest(int xPos, int yPos, int index) {
		super(xPos, yPos, index);
	}

	@Override
	public void draw(Graphics graphics) {
		graphics.drawImage(GuiIngame.texture, posX, posY, 64, 64, null);
		graphics.drawImage(chest, posX, posY, 64, 64, null);
		super.draw(graphics);
	}
	
	@Override
	public void onBreak(EntityPlayer player) {
		player.entityEffects.add(new EntityEffectInvisible());
		Map.setZoneEmptyAt(zoneIndex);
		MapZone up = Map.getZoneAtPos(posX, posY - 64);
		++GuiStats.zoneCount;
		if(up != null && up instanceof MapZoneFruit) {
			((MapZoneFruit)up).notified = true;
		}
	}
	
	@Override
	public boolean isBreakable() {
		return true;
	}
}