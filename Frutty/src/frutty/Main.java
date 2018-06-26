package frutty;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import frutty.gui.GuiMenu;
import frutty.gui.GuiStats;
import frutty.gui.Settings;
import frutty.map.MapZoneBase;
import frutty.map.interfaces.ITexturable;
import frutty.map.zones.MapZoneChest;
import frutty.map.zones.MapZoneEmpty;
import frutty.map.zones.MapZoneFruit;
import frutty.map.zones.MapZoneFruit.EnumFruit;
import frutty.map.zones.MapZoneNormal;
import frutty.map.zones.MapZonePlayer;
import frutty.map.zones.MapZoneSky;
import frutty.map.zones.MapZoneSpawner;
import frutty.map.zones.MapZoneWater;
import frutty.plugin.FruttyPlugin;
import frutty.plugin.FruttyPluginMain;
import frutty.tools.Version;
import frutty.tools.internal.EventBase;
import frutty.tools.internal.EventHandleObject;
import frutty.tools.internal.Plugin;

public final class Main {
	public static final TreeMap<String, MapZoneBase> zoneRegistry = new TreeMap<>();
	public static final ArrayList<Plugin> pluginList = new ArrayList<>(2);
	public static final ArrayList<EventHandleObject> mapLoadEvents = new ArrayList<>(0);
	
	public static final Random rand = new Random();
	public static final Plugin gamePluginContainer = new Plugin("Frutty", "Base plugin for the game", null, Version.from(1, 0, 0), "https://pastebin.com/raw/m5qJbnks");

	public static final MapZoneNormal normalZone = new MapZoneNormal();
	public static final MapZoneEmpty emptyZone = new MapZoneEmpty();
	public static final MapZonePlayer player1Zone = new MapZonePlayer(1);
	public static final MapZonePlayer player2Zone = new MapZonePlayer(2);
	public static final MapZoneFruit appleZone = new MapZoneFruit(EnumFruit.APPLE);
	public static final MapZoneFruit cherryZone = new MapZoneFruit(EnumFruit.CHERRY);
	public static final MapZoneSpawner spawnerZone = new MapZoneSpawner();
	public static final MapZoneChest chestZone = new MapZoneChest();
	public static final MapZoneWater waterZone = new MapZoneWater();
	public static final MapZoneSky skyZone = new MapZoneSky();
	
	public static void main(String[] args) throws IOException{
		zoneRegistry.put("normalZone", normalZone);
		zoneRegistry.put("emptyZone", emptyZone);
		zoneRegistry.put("appleZone", appleZone);
		zoneRegistry.put("player1Zone", player1Zone);
		zoneRegistry.put("player2Zone", player2Zone);
		zoneRegistry.put("cherryZone", cherryZone);
		zoneRegistry.put("spawnerZone", spawnerZone);
		zoneRegistry.put("chestZone", chestZone);
		zoneRegistry.put("waterZone", waterZone);
		zoneRegistry.put("skyZone", skyZone);
		
		GuiMenu.showMenu(true);
		Settings.loadSettings();
		GuiStats.loadStats();
		
		var savePath = Paths.get("saves");
		if(!Files.exists(savePath)) {
			Files.createDirectory(savePath);
		}
		
		pluginList.add(gamePluginContainer);
		pluginList.add(Plugin.pluginLoaderPlugin);
		loadPlugins();
		
		mapLoadEvents.sort(EventHandleObject.PRIORITY_COMPARATOR);
	}
	
	public static String getZoneName(MapZoneBase zone) {
		var entries = zoneRegistry.entrySet();
		
		for(var entry : entries) {
			if(entry.getValue() == zone) {
				return entry.getKey();
			}
		}
		return null;
	}
	
	public static ImageIcon[] getEditorTextureVariants(String ID) {
		return ((ITexturable)zoneRegistry.get(ID)).getEditorTextureVars();
	}
	
	public static BufferedImage loadTexture(String prefix, String name) {
		try{
			return ImageIO.read(Files.newInputStream(Paths.get("./textures/" + prefix + "/" + name)));
		}catch(IOException e){
			System.err.println("Can't find texture: " + prefix + "/" + name + " from class: " + Thread.currentThread().getStackTrace()[3].getClassName());
			return null;
		}
	}
	
	public static void loadPlugins() throws IOException {
		var pluginPath = Paths.get("plugins");
		if(!Files.exists(pluginPath)) {
			Files.createDirectory(pluginPath);
		}
		
		var all = new File("./plugins/").listFiles((dir, name) -> name.endsWith(".jar"));
		if(all.length > 0) {
			var mainClassNames = new String[all.length];
			var classLoaderNames = new URL[all.length];
			
			for(int k = 0; k < all.length; ++k) {
				try {
					classLoaderNames[k] = all[k].toURI().toURL();
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				}
				
				try(var jar = new JarFile(all[k])){
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
						Class<?> loaded = urlClass.loadClass(mainClassNames[k]);
						if(loaded.isAnnotationPresent(FruttyPlugin.class)) {
							FruttyPlugin pluginAnnotation = loaded.getDeclaredAnnotation(FruttyPlugin.class);
							pluginList.add(new Plugin(pluginAnnotation.id(), pluginAnnotation.description(), pluginAnnotation.updateURL(), Version.fromString(pluginAnnotation.version()), pluginAnnotation.versionURL()));
							
							Method[] methods = loaded.getDeclaredMethods();
							boolean ranMain = false;
							for(Method method : methods) {
								if(method.isAnnotationPresent(FruttyPluginMain.class)) {
									if((method.getModifiers() & Modifier.STATIC) != 0) {
										method.invoke(null);
										ranMain = true;
										break;
									}
									System.err.println("Main method from plugin: " + all[k] + " is not static");
								}
							}
							
							if(!ranMain) {
								System.err.println("Can't find main method annotated with @FruttyPluginMain from plugin: " + all[k]);
							}
						}
					}
				}
			} catch (IOException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void handleEvent(EventBase event, ArrayList<EventHandleObject> methods) {
		for(EventHandleObject handles : methods) event.invoke(handles.handle);
	}
}