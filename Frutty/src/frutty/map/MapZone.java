package frutty.map;

import java.awt.Graphics;
import java.io.Serializable;

import frutty.Main;
import frutty.entity.EntityPlayer;
import frutty.gui.GuiHelper;
import frutty.gui.GuiSettings.Settings;
import frutty.gui.GuiStats;
import frutty.map.zones.MapZoneEmpty;
import frutty.map.zones.MapZoneFruit;
import frutty.map.zones.MapZoneWater;

public abstract class MapZone implements Serializable{
	private static final long serialVersionUID = 392316063689927131L;
	
	public final int posX, posY, zoneIndex;
	
	public MapZone(int xPos, int yPos, int index) {
		posX = xPos;
		posY = yPos;
		zoneIndex = index;
	}
	
	/** Super.draw(graphics)- et a végére ha kell depth render*/
	public void draw(Graphics graphics) {
		if(Settings.graphicsLevel > 0) {
			graphics.setColor(GuiHelper.color_84Black);
			
			int till = posY / 120;
			for(int k = 0; k < till && k < 4; ++k) {
				graphics.fillRect(posX, posY, 64, 64);
			}
		}
	}
	
	public static boolean isEmpty(int x, int y) {
		MapZone zone = Map.getZoneAtPos(x, y);
		return zone != null && zone instanceof MapZoneEmpty;
	}
	
	/**Call super.onBreak if you want a normal breakable zone
	 * @param player Player obj used in subclasses */
	public void onBreak(EntityPlayer player) {
		if(isBreakable() && this instanceof MapZoneEmpty == false && this instanceof MapZoneWater == false) {
			Map.setZoneEmptyAt(zoneIndex);
			++GuiStats.zoneCount;
			
			MapZone up = Map.getZoneAtIndex(zoneIndex - (Map.currentMap.width / 64) - 1);
			if(up != null && up instanceof MapZoneFruit) {
				((MapZoneFruit)up).notified = true;
			}
			Particle.addParticles(2 + Main.rand.nextInt(10), posX, posY, getParticleIndex());
		}
	}
	
	@SuppressWarnings("static-method")
	public int getParticleIndex() {
		return 0;
	}
	public abstract boolean isBreakable();
}