package frutty.plugin;

import java.awt.image.BufferedImage;

import frutty.Main;
import frutty.map.interfaces.MapZoneBase;

/**Methods for registering zones, loading textures, etc*/
public final class PluginRegistry {
	private PluginRegistry() {}
	
	/**
	 * Register zone
	 * @param zoneID String ID for the zone (format: "pluginID:zoneName")
	 * @param zone The zone to register
	 */
	public static void registerZone(String zoneID, MapZoneBase zone) {
		if(!zoneID.contains(":")) {
			throw new IllegalArgumentException("Tried to register zone without plugin ID: " + zoneID);
		}
		if(Main.zoneRegistry.containsKey(zoneID)) {
			throw new IllegalArgumentException("Zone already registered with ID: " + zoneID);
		}
		Main.zoneRegistry.put(zoneID, zone);
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
}