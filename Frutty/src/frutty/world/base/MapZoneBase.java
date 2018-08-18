package frutty.world.base;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.Serializable;
import java.util.List;

import javax.swing.ImageIcon;

import frutty.FruttyMain;
import frutty.entity.EntityPlayer;
import frutty.gui.GuiSettings.Settings;
import frutty.gui.GuiStats;
import frutty.plugin.event.world.ZoneAddedEvent;
import frutty.plugin.internal.EventHandle;
import frutty.sound.CachedSoundClip;
import frutty.tools.GuiHelper;
import frutty.tools.Lazy;
import frutty.tools.Material;
import frutty.world.Particle;
import frutty.world.World;
import frutty.world.zones.MapZoneApple;
import frutty.world.zones.MapZoneBush;
import frutty.world.zones.MapZoneCherry;
import frutty.world.zones.MapZoneChest;
import frutty.world.zones.MapZoneEmpty;
import frutty.world.zones.MapZoneNormal;
import frutty.world.zones.MapZonePlayer;
import frutty.world.zones.MapZonePortal;
import frutty.world.zones.MapZoneSky;
import frutty.world.zones.MapZoneSpawner;
import frutty.world.zones.MapZoneWater;

@SuppressWarnings("unused")
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
	
	public abstract void draw(int x, int y, Material material, Graphics2D graphics);
	protected abstract ImageIcon getEditorIcon();
	
	public boolean doesHidePlayer(int x, int y) {return false;}
	public boolean isBreakable(int x, int y) {return canPlayerPass(x, y);}
	public boolean canPlayerPass(int x, int y) {return true;}
	public boolean canNPCPass(int x, int y) {return false;}
	protected void onZoneAdded(boolean isCoop, int x, int y) {}
	
	public void onZoneEntered(int x, int y, int zoneIndex, Material material, EntityPlayer player) {
		player.hidden = doesHidePlayer(x, y);

		if(isBreakable(x, y)) {
			World.setZoneEmptyAt(zoneIndex);
			breakSound.start();
			GuiStats.zoneCount++;
			
			int checkIndex = zoneIndex - (World.width / 64) - 1;
			MapZoneBase up = World.getZoneAtIndex(checkIndex);
			if(up != null && up instanceof IZoneEntityProvider) {
				World.zoneEntities[checkIndex].onNotified();
			}
			
			Particle.spawnFallingParticles(2 + FruttyMain.rand.nextInt(10), x, y, material);
		}
	}
	
	
	/********************************************************INTERNALS***********************************************************/
	
	
	public final void render(int x, int y, Material material, Graphics2D graphics) {
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
		ImageIcon icon = getEditorIcon();
		return (icon.getIconWidth() == 64 && icon.getIconHeight() == 64) ? icon : new ImageIcon(icon.getImage().getScaledInstance(64, 64, Image.SCALE_DEFAULT));
	}
	
	public static boolean isEmpty(int x, int y) {
		MapZoneBase zone = World.getZoneAtPos(x, y);
		return zone != null && zone == emptyZone;
	}
	
	public static boolean isEmptyAt(int index) {
		MapZoneBase zone = World.getZoneAtIndex(index);
		return zone != null && zone == emptyZone;
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
	
	private static final List<MapZoneBase> zoneRegistry = FruttyMain.toList(normalZone, emptyZone, appleZone, player1Zone, player2Zone, cherryZone, spawnerZone, chestZone, waterZone, skyZone, bushZone, portalZone);
	
	/**
	 * Register zone
	 * @param zoneID String ID for the zone (format: "pluginID:zoneName")
	 * @param zone The zone to register
	 */
	public static void registerZone(MapZoneBase zone) {
		if(!zone.zoneName.contains(":")) {
			throw new IllegalArgumentException("Tried to register zone without plugin ID: " + zone.zoneName);
		}
		
		if(isZoneAlreadyRegistered(zone.zoneName)) {
			throw new IllegalArgumentException("Zone already registered with ID: " + zone.zoneName);
		}
		
		zoneRegistry.add(zone);
	}
	
	public static MapZoneBase getZoneFromName(String name) {
		for(MapZoneBase zone : zoneRegistry) {
			if(zone.zoneName.equals(name)) {
				return zone;
			}
		}
		return null;
	}
	
	public static String[] zoneNames() {
		return zoneRegistry.stream().map(zone -> zone.zoneName).toArray(String[]::new);
	}
	
	private static boolean isZoneAlreadyRegistered(String name) {
		for(MapZoneBase zones : zoneRegistry) {
			if(zones.zoneName.equals(name)) {
				return true;
			}
		}
		return false;
	}
}