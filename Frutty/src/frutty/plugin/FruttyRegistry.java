package frutty.plugin;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.ImageIcon;

import frutty.Main;
import frutty.map.base.MapZone;

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
			@SuppressWarnings("boxing")
			Integer sad = zone.zoneID;
			
			if(!Main.zoneRegistry.containsKey(sad)) {
				Main.zoneRegistry.put(sad, zone);
				String path = "./textures/dev/" + editorTextureName + ".png";
				Main.editorButtonIcons[zone.zoneID] = new Lazy<>(() -> new ImageIcon(path));
				
				if(!new File(path).exists()) {
					System.err.println("Unable to load texture: " + path);
				}
			}else{
				System.err.println("Zone already registered with ID: " + zone.zoneID);
			}
		}
	}
	
	public static BufferedImage loadTexture(String prefix, String name) {
		return Main.loadTexture(prefix, name);
	}
}