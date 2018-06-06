package frutty;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import frutty.gui.GuiMenu;
import frutty.gui.GuiStats;
import frutty.gui.Settings;
import frutty.gui.editor.GuiEditor;
import frutty.gui.editor.GuiEditor.ZoneButton;
import frutty.gui.editor.GuiToolSelector;
import frutty.gui.editor.GuiToolSelector.GuiTextureSelector;
import frutty.map.base.MapZone;
import frutty.map.interfaces.ITexturable;
import frutty.map.zones.MapZoneChest;
import frutty.map.zones.MapZoneEmpty;
import frutty.map.zones.MapZoneFruit;
import frutty.map.zones.MapZoneFruit.EnumFruit;
import frutty.map.zones.MapZoneNormal;
import frutty.map.zones.MapZoneSky;
import frutty.map.zones.MapZoneSpawner;
import frutty.map.zones.MapZoneWater;
import frutty.plugin.IFruttyPlugin;

public final class Main {
	public static final HashMap<String, MapZone> zoneRegistry = new HashMap<>();
	public static final ArrayList<Plugin> pluginList = new ArrayList<>(2);
	
	public static final Random rand = new Random();
	public static final String VERSION = "1.0.0";

	public static final MapZoneNormal normalZone = new MapZoneNormal();
	public static final MapZoneEmpty emptyZone = new MapZoneEmpty();
	public static final MapZoneFruit appleZone = new MapZoneFruit(EnumFruit.APPLE);
	public static final MapZoneFruit cherryZone = new MapZoneFruit(EnumFruit.CHERRY);
	public static final MapZoneSpawner spawnerZone = new MapZoneSpawner();
	public static final MapZoneChest chestZone = new MapZoneChest();
	public static final MapZoneWater waterZone = new MapZoneWater();
	public static final MapZoneSky skyZone = new MapZoneSky();
	
	public static void main(String[] args){
		zoneRegistry.put("normalZone", normalZone);
		zoneRegistry.put("emptyZone", emptyZone);
		zoneRegistry.put("appleZone", appleZone);
		zoneRegistry.put("cherryZone", cherryZone);
		zoneRegistry.put("spawnerZone", spawnerZone);
		zoneRegistry.put("chestZone", chestZone);
		zoneRegistry.put("waterZone", waterZone);
		zoneRegistry.put("skyZone", skyZone);
		
		GuiMenu.showMenu(true);
		Settings.loadSettings();
		GuiStats.loadStats();
		new File("./saves/").mkdir();
		
		pluginList.add(new Plugin("Frutty", "A Neri naon büdös", null, VERSION, null));
		pluginList.add(Plugin.pluginLoaderPlugin);
		
		loadPlugins();
	}
	
	public static String getZoneName(MapZone zone) {
		var entries = zoneRegistry.entrySet();
		
		for(Entry<String, MapZone> entry : entries) {
			if(entry.getValue() == zone) {
				return entry.getKey();
			}
		}
		return null;
	}
	
	public static boolean hasTextureInfo(String ID) {
		return zoneRegistry.get(ID) instanceof ITexturable;
	}
	
	public static ImageIcon[] getEditorTextureVariants(String ID) {
		return ((ITexturable)zoneRegistry.get(ID)).getEditorTextureVars();
	}
	
	public static MapZone handleMapReading(String ID) {
		if(ID.equals("player1Zone") || ID.equals("player2Zone")) {
			return Main.emptyZone;
		}
		return zoneRegistry.get(ID);
	}
	
	public static BufferedImage loadTexture(String prefix, String name) {
		try{
			return ImageIO.read(new File("./textures/" + prefix + "/" + name));
		}catch(IOException e){
			System.err.println("Can't find texture: " + prefix + "/" + name + ", returning null. Have fun :)");
			return null;
		}
	}
	
	public static void handleEditorReading(GuiEditor editor, String zoneID, ObjectInputStream input, int x, int y, String[] textures) throws IOException {
		if(zoneID.equals("player1Zone")) {
			ZoneButton button = new ZoneButton(GuiToolSelector.player1Texture, editor);
			button.setBounds(x * 64, y * 64, 64, 64);
			button.zoneID = zoneID;
			editor.zoneButtons.add(button);
			editor.add(button);
		}else if(zoneID.equals("player2Zone")) {
			ZoneButton button = new ZoneButton(GuiToolSelector.player2Texture, editor);
			button.setBounds(x * 64, y * 64, 64, 64);
			button.zoneID = zoneID;
			editor.zoneButtons.add(button);
			editor.add(button);
		}else{
			MapZone zone = zoneRegistry.get(zoneID);
			
			ZoneButton button = new ZoneButton(zone.editorTexture.get(), editor);
			button.setBounds(x * 64, y * 64, 64, 64);
			button.zoneID = zoneID;
			if(zone instanceof ITexturable){
				int textureData = input.readByte();
				button.zoneTexture = textures[textureData];
				button.setIcon(((ITexturable)zone).getEditorTextureVars()[GuiTextureSelector.indexOf(textures[textureData] + ".png")]);
			}
			editor.zoneButtons.add(button);
			editor.add(button);
		}
	}
	
	public static void loadPlugins() {
		new File("./plugins/").mkdir();
		
		File[] all = new File("./plugins/").listFiles((dir, name) -> name.endsWith(".jar"));
		if(all.length > 0) {
			
			String[] mainClassNames = new String[all.length];
			URL[] classLoaderNames = new URL[all.length];
			
			for(int k = 0; k < all.length; ++k) {
				try {
					classLoaderNames[k] = all[k].toURI().toURL();
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				}
				
				try(JarFile jar = new JarFile(all[k])){
					Manifest mani = jar.getManifest();
					if(mani == null) {
						System.err.println("Can't find manifest file from plugin: " + all[k]);
					}else{
						String pluginClass = mani.getMainAttributes().getValue("Plugin-Class");
						if(pluginClass == null) {
							System.err.println("Can't find \"Plugin-Class\" attribute from plugin: " + all[k]);
						}else {
							mainClassNames[k] = pluginClass;
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			try(URLClassLoader urlClass = new URLClassLoader(classLoaderNames)){
				for(int k = 0; k < mainClassNames.length; ++k) {
					if(mainClassNames[k] == null) {
						System.err.println("Can't load main class from plugin: " + all[k]);
					}else{
						var loaded = urlClass.loadClass(mainClassNames[k]);
						if(hasInterface(loaded, IFruttyPlugin.class)) {
							Object instance = loaded.getDeclaredConstructor().newInstance();
							loaded.getMethod("initPlugin").invoke(instance);
							pluginList.add(new Plugin((String) loaded.getMethod("getPluginID").invoke(instance), 
												   (String) loaded.getMethod("getPluginDescription").invoke(instance), 
												   (String) loaded.getMethod("getUpdateURL").invoke(instance), 
												   (String) loaded.getMethod("getPluginVersion").invoke(instance),
												   (String) loaded.getMethod("getVersionURL").invoke(instance)));
						}
					}
				}
			} catch (IOException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static boolean hasInterface(Class<?> theClass, Class<?> interfaceClass) {
		for(var faces : theClass.getInterfaces()) {
			if(interfaceClass.isAssignableFrom(faces)){
				return true;
			}
		}
		return false;
	}
	
	public static final class Plugin {
		public static final Plugin pluginLoaderPlugin = new Plugin("Plugin Loader", "Base plugin loading module for Frutty", "https://www.google.com", "1.0.0", null);
		
		public final String description;
		public final String ID, updateURL, version, versionURL;
			
		public Plugin(String name, String desc, String url, String ver, String verURL) {
			description = desc;
			ID = name;
			updateURL = url;
			version = ver;
			versionURL = verURL;
		}
			
		@Override
		public String toString() {
			return ID;
		}
			
		public String getInfo() {
			return "Name: " + ID + 
					"<br>Version: " + version + 
					"<br>URL: " + (updateURL == null ? "" : ("<a href=" + updateURL + ">" + updateURL + "</a>")) + 
					"<br>Description: " + description;
		}
	}
}