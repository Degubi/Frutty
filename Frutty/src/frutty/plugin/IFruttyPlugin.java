package frutty.plugin;

import java.awt.image.BufferedImage;

import frutty.Main;
import frutty.map.MapZone;
import frutty.stuff.Version;

public interface IFruttyPlugin {
	void initPlugin();
	String getPluginID();
	Version getPluginVersion();
	default String getPluginDescription() {return null;}
	default String getUpdateURL() {return null;}
	default String getVersionURL() {return null;}
	
	/**
	 * @param ID Zone ID
	 * @param zone The zone object
	 * @param editorTextureName Name of the texture used in editor, put it in the dev folder
	 */
	default void registerZone(String ID, MapZone zone) {
		var finalID = getPluginID() + ":" + ID;
		if(Main.zoneRegistry.containsKey(finalID)) {
			throw new IllegalArgumentException("Zone already registered with ID: " + finalID);
		}
		Main.zoneRegistry.put(finalID, zone);
	}
	
	static BufferedImage loadTexture(String prefix, String name) {
		return Main.loadTexture(prefix, name);
	}
}