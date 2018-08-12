package frutty;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import frutty.gui.GuiIngame;
import frutty.gui.GuiMenu;
import frutty.plugin.internal.Plugin;
import frutty.tools.IOHelper;
import frutty.world.base.MapZoneBase;
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

public final class Main {
	public static final Random rand = new Random();
	public static final boolean hasPlugins = IOHelper.fileCount("plugins") > 0;
	
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
	
	public static final List<MapZoneBase> zoneRegistry = toList(normalZone, emptyZone, appleZone, player1Zone, player2Zone, cherryZone, spawnerZone, chestZone, waterZone, skyZone, bushZone, portalZone);

	private Main() {}
	
	public static void main(String[] args){
		Plugin.handlePluginInit();
		GuiMenu.createMainFrame(true);
		IOHelper.createDirectory("saves");
	}

	public static MapZoneBase getZoneFromName(String name) {
		for(MapZoneBase zone : zoneRegistry) {
			if(zone.zoneName.equals(name)) {
				return zone;
			}
		}
		return null;
	}
	
	public static void loadTextures(String[] textureNames) {
		GuiIngame.textures = new BufferedImage[textureNames.length];
		for(int k = 0; k < textureNames.length; ++k) {
			GuiIngame.textures[k] = IOHelper.loadTexture("map", textureNames[k] + ".png");
		}
	}
	
	public static void loadSkyTexture(String textureName) {
		GuiIngame.skyTexture = !textureName.equals("null") ? IOHelper.loadTexture("map/skybox", textureName + ".png") : null;
	}
	
	@SafeVarargs
	public static <T> ArrayList<T> toList(T... objs){
		ArrayList<T> list = new ArrayList<>(objs.length);
		
		for(T el : objs) {
			list.add(el);
		}
		return list;
	}
}