package frutty.world.base;

import frutty.*;
import frutty.entity.*;
import frutty.gui.*;
import frutty.gui.GuiSettings.*;
import frutty.plugin.event.world.*;
import frutty.sound.*;
import frutty.tools.*;
import frutty.world.*;
import frutty.world.zones.*;
import java.awt.*;
import java.io.*;
import java.util.List;
import javax.swing.*;

@SuppressWarnings({"unused", "static-method"})
public abstract class MapZoneBase implements Serializable{
	private static final long serialVersionUID = 392316063689927131L;
	public transient final Lazy<ImageIcon> editorTexture = new Lazy<>(this::getEditorIconInternal);
	
	protected static final CachedSoundClip breakSound = new CachedSoundClip("zonebreak.wav");
	public final String zoneName;
	public final boolean hasShadowRender, hasParticleSpawns;
	
	public MapZoneBase(String name, boolean hasDarkening, boolean enableParticles) {
		zoneName = name;
		hasShadowRender = hasDarkening;
		hasParticleSpawns = enableParticles;
	}
	
	public MapZoneBase(String name) {
		this(name, true, true);
	}
	
	public abstract void draw(int x, int y, Material material, Graphics graphics);
	protected abstract ImageIcon getEditorIcon();
	
    public boolean doesHidePlayer(int x, int y) {return false;}
	public boolean isBreakable(int x, int y) {return canPlayerPass(x, y);}
	public boolean canPlayerPass(int x, int y) {return true;}
	public boolean canNPCPass(int x, int y) {return false;}
	protected void onZoneAdded(boolean isCoop, int x, int y) {}
	
	public void onZoneEntered(int x, int y, Material material, EntityPlayer player) {
		player.hidden = doesHidePlayer(x, y);

		if(isBreakable(x, y)) {
			World.setZoneEmptyAt(x, y);
			breakSound.start();
			GuiStats.zoneCount++;
			
			var aboveZoneIndex = World.coordsToIndex(x, y - 64);
			var zoneAbove = World.getZoneAtIndex(aboveZoneIndex);
			
			if(zoneAbove != null && zoneAbove instanceof IZoneEntityProvider) {
				World.zoneEntities[aboveZoneIndex].onNotified();
			}
			
			Particle.spawnFallingParticles(2 + Main.rand.nextInt(10), x, y, material);
		}
	}
	
	/********************************************************INTERNALS***********************************************************/
	
	
	public final void drawInternal(int x, int y, Material material, Graphics graphics) {
		draw(x, y, material, graphics);
		
		if(hasShadowRender && Settings.graphicsLevel > 0) {
			graphics.setColor(GuiHelper.color_84Black);
			
			int till = y / 120;
			for(int k = 0; k < till && k < 4; ++k) {
				graphics.fillRect(x, y, 64, 64);
			}
		}
		
		if(Settings.renderDebugLevel > 1) {
			graphics.setColor(Color.WHITE);
			graphics.drawRect(x, y, 64, 64);
		}
	}
	
	public final void onZoneAddedInternal(boolean isCoop, int x, int y) {
		if(!EventHandle.zoneAddedEvents.isEmpty()) {
			ZoneAddedEvent event = new ZoneAddedEvent(this, x, y);
			EventHandle.handleEvent(event, EventHandle.zoneAddedEvents);
			
			if(!event.canceled) {
				onZoneAdded(isCoop, x, y);
			}
		}else{
			onZoneAdded(isCoop, x, y);
		}
	}
	
	private final ImageIcon getEditorIconInternal() {
		var icon = getEditorIcon();
		return (icon.getIconWidth() == 64 && icon.getIconHeight() == 64) ? icon : new ImageIcon(icon.getImage().getScaledInstance(64, 64, Image.SCALE_DEFAULT));
	}
	
	public static final MapZoneNormal normalZone = new MapZoneNormal();
	public static final MapZoneEmpty emptyZone = new MapZoneEmpty();
	public static final MapZonePlayer player1Zone = new MapZonePlayer(1);
	public static final MapZonePlayer player2Zone = new MapZonePlayer(2);
	public static final MapZoneApple appleZone = new MapZoneApple();
	public static final MapZoneCherry cherryZone = new MapZoneCherry();
	public static final MapZoneSpawner spawnerZone = new MapZoneSpawner();
	public static final MapZoneChest chestZone = new MapZoneChest();
	public static final MapZoneWater waterZone = new MapZoneWater();
	public static final MapZoneSky skyZone = new MapZoneSky();
	public static final MapZoneBush bushZone = new MapZoneBush();
	public static final MapZonePortal portalZone = new MapZonePortal();
	
	private static final List<MapZoneBase> zoneRegistry = GeneralFunctions.toMutableList(normalZone, emptyZone, appleZone, player1Zone, player2Zone, cherryZone, spawnerZone, chestZone, waterZone, skyZone, bushZone, portalZone);
	
	/**
	 * Register zone
	 * @param zoneID String ID for the zone (format: "pluginID:zoneName")
	 * @param zone The zone to register
	 */
	public static void registerZone(MapZoneBase zone) {
		if(!zone.zoneName.contains(":")) {
			throw new IllegalArgumentException("Tried to register zone without plugin ID: " + zone.zoneName);
		}
		
		if(zoneRegistry.stream().anyMatch(zones -> zones.zoneName.equals(zone.zoneName))) {
			throw new IllegalArgumentException("Zone already registered with ID: " + zone.zoneName);
		}
		
		zoneRegistry.add(zone);
	}
	
	public static MapZoneBase getZoneFromName(String name) {
		for(var zone : zoneRegistry) {
			if(zone.zoneName.equals(name)) {
				return zone;
			}
		}
		return null;
	}
	
	public static String[] zoneNames() {
		return zoneRegistry.stream().map(zone -> zone.zoneName).toArray(String[]::new);
	}
}