package frutty.plugin;

import java.awt.image.BufferedImage;

import frutty.Main;
import frutty.world.interfaces.MapZoneBase;

/**Methods for registering zones, loading textures, etc*/
public final class PluginRegistry {
	private PluginRegistry() {}
	
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
		
		Main.zoneRegistry.add(zone);
	}
	
	/**
	 * Load texture
	 * @param prefix Folder of the texture
	 * @param name Name of the texture including format
	 * @return The texture object
	 */
	public static BufferedImage loadTexture(String prefix, String name) {
		return Main.loadTexture(prefix, name);
	}
	
	private static boolean isZoneAlreadyRegistered(String name) {
		for(MapZoneBase zones : Main.zoneRegistry) {
			if(zones.zoneName.equals(name)) {
				return true;
			}
		}
		return false;
	}
}