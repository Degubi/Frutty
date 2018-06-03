package frutty.plugin;

import java.awt.image.BufferedImage;

import frutty.Main;
import frutty.map.base.MapZone;

public interface IFruttyPlugin {
	public void pluginMain();
	
	/**
	 * @param ID Zone ID
	 * @param zone The zone object
	 * @param editorTextureName Name of the texture used in editor, put it in the dev folder
	 */
	public static void registerZone(MapZone zone) {
		if(zone.zoneID < 21) {
			throw new IllegalArgumentException("Can't register zone with ID lower than 20");
		}
		@SuppressWarnings("boxing")
		Integer sad = zone.zoneID;
		
		if(Main.zoneRegistry.containsKey(sad)) {
			throw new IllegalArgumentException("Zone already registered with ID: " + zone.zoneID);
		}
		Main.zoneRegistry.put(sad, zone);
	}
	
	public static BufferedImage loadTexture(String prefix, String name) {
		return Main.loadTexture(prefix, name);
	}
}