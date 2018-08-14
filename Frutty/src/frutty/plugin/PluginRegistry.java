package frutty.plugin;

import frutty.FruttyMain;
import frutty.world.base.MapZoneBase;

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
		
		FruttyMain.zoneRegistry.add(zone);
	}
	
	private static boolean isZoneAlreadyRegistered(String name) {
		for(MapZoneBase zones : FruttyMain.zoneRegistry) {
			if(zones.zoneName.equals(name)) {
				return true;
			}
		}
		return false;
	}
}