package frutty.registry;

import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import frutty.map.MapZone;
import frutty.registry.internal.InternalRegistry;
import frutty.registry.internal.Lazy;

public final class FruttyRegistry {
	private FruttyRegistry() {}
	
	/**
	 * @param ID Zone ID
	 * @param zone The zone object
	 * @param editorTextureName Name of the texture used in editor, put it in the dev folder
	 */
	public static void registerZone(MapZone zone, String editorTextureName) {
		if(zone.zoneID < 20) {
			System.err.println("Can't register zone with ID lower than 20");
		}else {
			Integer sad = zone.zoneID;
			if(!InternalRegistry.zoneRegistry.containsKey(sad)) {
				InternalRegistry.zoneRegistry.put(sad, zone);
				InternalRegistry.editorButtonIcons[zone.zoneID] = new Lazy<>(() -> new ImageIcon("./textures/dev/" + editorTextureName + ".png"));
			}else{
				System.err.println("Zone already registered with ID: " + zone.zoneID);
			}
		}
	}
	
	public static BufferedImage loadTexture(String prefix, String name) {
		return InternalRegistry.loadTexture(prefix, name);
	}
}