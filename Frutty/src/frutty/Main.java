package frutty;

import java.io.File;
import java.util.Random;

import frutty.gui.GuiMenu;
import frutty.gui.GuiSettings.Settings;
import frutty.gui.GuiStats;
import frutty.map.zones.MapZoneChest;
import frutty.map.zones.MapZoneEmpty;
import frutty.map.zones.MapZoneFruit;
import frutty.map.zones.MapZoneFruit.EnumFruit;
import frutty.map.zones.MapZoneNormal;
import frutty.map.zones.MapZoneSky;
import frutty.map.zones.MapZoneSpawner;
import frutty.map.zones.MapZoneWater;
import frutty.registry.internal.InternalRegistry;
import frutty.registry.internal.PluginLoader;

public final class Main {
	public static final Random rand = new Random();
	
	public static final MapZoneNormal normalZone = new MapZoneNormal();
	public static final MapZoneEmpty emptyZone = new MapZoneEmpty();
	public static final MapZoneFruit appleZone = new MapZoneFruit(EnumFruit.APPLE);
	public static final MapZoneFruit cherryZone = new MapZoneFruit(EnumFruit.CHERRY);
	public static final MapZoneSpawner spawnerZone = new MapZoneSpawner();
	public static final MapZoneChest chestZone = new MapZoneChest();
	public static final MapZoneWater waterZone = new MapZoneWater();
	public static final MapZoneSky skyZone = new MapZoneSky();
	
	public static void main(String[] args){
		InternalRegistry.registerZone(normalZone, "normal");
		InternalRegistry.registerZone(emptyZone, "empty");
		InternalRegistry.registerZone(appleZone, "apple");
		InternalRegistry.registerZone(cherryZone, "cherry");
		InternalRegistry.registerZone(spawnerZone, "spawner");
		InternalRegistry.registerZone(chestZone, "chest");
		InternalRegistry.registerZone(waterZone, "water");
		InternalRegistry.registerZone(skyZone, "sky");
		
		PluginLoader.loadPlugins();
		
		GuiMenu.showMenu();
		Settings.loadSettings();
		GuiStats.loadStats();
		new File("./saves/").mkdir();
	}
}