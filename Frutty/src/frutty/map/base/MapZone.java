package frutty.map.base;

import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;

import frutty.Main;
import frutty.entity.EntityPlayer;
import frutty.entity.zone.EntityAppleZone;
import frutty.entity.zone.EntityZone;
import frutty.gui.GuiHelper;
import frutty.gui.GuiSettings.Settings;
import frutty.gui.GuiStats;
import frutty.map.Map;
import frutty.map.Particle;

@SuppressWarnings("unused")
public abstract class MapZone implements Serializable{
	private static final long serialVersionUID = 392316063689927131L;
	
	public final int zoneID;
	public final boolean hasShadowRender;
	
	public MapZone(int ID, boolean hasDarkening) {
		zoneID = ID;
		hasShadowRender = hasDarkening;
	}
	
	public abstract void draw(int x, int y, int textureIndex, Graphics graphics);
	
	public boolean hasZoneEntity() {return false;}
	public boolean isBreakable(int x, int y) {
		return true;
	}
	
	public void onZoneAdded(boolean isBackground, int x, int y) {}
	
	public EntityZone getZoneEntity(int x, int y, int zoneIndex) {
		return null;
	}
	
	/**Call super.onBreak if you want a normal breakable zone
	 * @param player Player obj used in subclasses */
	public void onBreak(int x, int y, int zoneIndex, int textureIndex, EntityPlayer player) {
		if(isBreakable(x, y) && this != Main.emptyZone && this != Main.waterZone) {
			Map.setZoneEmptyAt(zoneIndex);
			++GuiStats.zoneCount;
			
			int checkIndex = zoneIndex - (Map.width / 64) - 1;
			MapZone up = Map.getZoneAtIndex(checkIndex);
			if(up != null && up == Main.appleZone) {
				((EntityAppleZone)Map.zoneEntities[checkIndex]).notified = true;
			}
			Particle.addParticles(2 + Main.rand.nextInt(10), x, y, textureIndex);
		}
	}
	
	public final void render(int x, int y, int textureIndex, Graphics graphics) {
		draw(x, y, textureIndex, graphics);
		
		if(hasShadowRender && Settings.graphicsLevel > 0) {
			graphics.setColor(GuiHelper.color_84Black);
			
			int till = y / 120;
			for(int k = 0; k < till && k < 4; ++k) {
				graphics.fillRect(x, y, 64, 64);
			}
		}
		
		if(Settings.renderDebug) {
			graphics.setColor(Color.WHITE);
			graphics.drawRect(x, y, 64, 64);
		}
	}
	
	public static boolean isEmpty(int x, int y) {
		MapZone zone = Map.getZoneAtPos(x, y);
		return zone != null && zone == Main.emptyZone;
	}
	
	public static boolean isEmptyAt(int index) {
		MapZone zone = Map.getZoneAtIndex(index);
		return zone != null && zone == Main.emptyZone;
	}
}