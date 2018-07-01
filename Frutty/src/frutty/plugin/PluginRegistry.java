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
		if(isZoneAlreadyRegistered(zoneID)) {
			throw new IllegalArgumentException("Zone already registered with ID: " + zoneID);
		}
		
		if(Main.zoneIndex == Main.zoneStorage.length) {
			Object[] newArray = new Object[Main.zoneStorage.length + 10];
			System.arraycopy(Main.zoneStorage, 0, newArray, 0, Main.zoneStorage.length);
			Main.zoneStorage = newArray;
		}
		Main.zoneStorage[Main.zoneIndex++] = zoneID;
		Main.zoneStorage[Main.zoneIndex++] = zone;
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
		for(int k = 0; k < Main.zoneIndex; k += 2) {
			if(Main.zoneStorage[k].equals(name)) {
				return true;
			}
		}
		return false;
	}
}