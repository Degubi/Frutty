package frutty;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
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
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.imageio.ImageIO;

import frutty.gui.GuiMenu;
import frutty.gui.GuiStats;
import frutty.gui.Settings;
import frutty.map.interfaces.MapZoneBase;
import frutty.map.zones.MapZoneChest;
import frutty.map.zones.MapZoneEmpty;
import frutty.map.zones.MapZoneFruit;
import frutty.map.zones.MapZoneFruit.EnumFruit;
import frutty.map.zones.MapZoneNormal;
import frutty.map.zones.MapZonePlayer;
import frutty.map.zones.MapZoneSky;
import frutty.map.zones.MapZoneSpawner;
import frutty.map.zones.MapZoneWater;
import frutty.plugin.FruttyEvent;
import frutty.plugin.FruttyPlugin;
import frutty.plugin.FruttyPluginMain;
import frutty.plugin.event.MapInitEvent;
import frutty.tools.Version;
import frutty.tools.internal.EventBase;
import frutty.tools.internal.EventHandleObject;
import frutty.tools.internal.Plugin;

public final class Main {
	public static final Random rand = new Random();
	public static final Plugin gamePluginContainer = new Plugin("Frutty", "Base plugin for the game", null, Version.from(1, 1, 0), "https://pastebin.com/raw/m5qJbnks");
	public static final ArrayList<Plugin> pluginList = new ArrayList<>(2);
	public static final ArrayList<EventHandleObject> mapLoadEvents = new ArrayList<>(0);
	
	public static void main(String[] args){
		GuiMenu.showMenu(true);
		Settings.loadSettings();
		GuiStats.loadStats();
			
		var savePath = Paths.get("saves");
		if(!Files.exists(savePath)) {
			try {
				Files.createDirectory(savePath);
			} catch (IOException e) {}
		}
			
		pluginList.add(gamePluginContainer);
		pluginList.add(Plugin.pluginLoaderPlugin);
		loadPlugins();
		
		mapLoadEvents.sort(EventHandleObject.PRIORITY_COMPARATOR);
	}
	
	public static BufferedImage loadTexture(String prefix, String name) {
		try{
			return ImageIO.read(Files.newInputStream(Paths.get("./textures/" + prefix + "/" + name)));
		}catch(IOException e){
			System.err.println("Can't find texture: " + prefix + "/" + name + " from class: " + Thread.currentThread().getStackTrace()[3].getClassName());
			return null;
		}
	}
	
	public static void loadPlugins() {
		var pluginPath = Paths.get("plugins");
		if(!Files.exists(pluginPath)) {
			try {
				Files.createDirectory(pluginPath);
			} catch (IOException e) {}
		}
		
		var pluginNames = new File("./plugins/").listFiles((dir, name) -> name.endsWith(".jar"));
		if(pluginNames.length > 0) {
			var mainClassNames = new String[pluginNames.length];
			var classLoaderNames = new URL[pluginNames.length];
			
			for(int k = 0; k < pluginNames.length; ++k) {
				try {
					classLoaderNames[k] = pluginNames[k].toURI().toURL();
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				}
				
				try(var jar = new JarFile(pluginNames[k])){
					Manifest mani = jar.getManifest();
					if(mani == null) {
						throw new IllegalStateException("Can't find manifest file from plugin: " + pluginNames[k]);
					}
					String pluginClass = mani.getMainAttributes().getValue("Plugin-Class");
					if(pluginClass == null) {
						throw new IllegalStateException("Can't find \"Plugin-Class\" attribute from plugin: " + pluginNames[k]);
					}
					mainClassNames[k] = pluginClass;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			try(URLClassLoader urlClass = new URLClassLoader(classLoaderNames)){
				for(int k = 0; k < mainClassNames.length; ++k) {
					if(mainClassNames[k] == null) {
						throw new IllegalStateException("Can't load main class from plugin: " + pluginNames[k]);
					}
					Class<?> loaded = urlClass.loadClass(mainClassNames[k]);
					if(!loaded.isAnnotationPresent(FruttyPlugin.class)) {
						throw new IllegalStateException("Main class from plugin: " + pluginNames[k] + " is not annotated with @FruttyPlugin");
					}
					
					FruttyPlugin pluginAnnotation = loaded.getDeclaredAnnotation(FruttyPlugin.class);
					pluginList.add(new Plugin(pluginAnnotation.name(), pluginAnnotation.description(), pluginAnnotation.updateURL(), Version.fromString(pluginAnnotation.version()), pluginAnnotation.versionURL()));
						
					Method[] methods = loaded.getDeclaredMethods();
					boolean ranMain = false;
					for(Method method : methods) {
						if(ranMain) {
							throw new IllegalStateException("Found more than one main methods from plugin: " + pluginNames[k]);
						}
						if(method.isAnnotationPresent(FruttyPluginMain.class)) {
							if((method.getModifiers() & Modifier.STATIC) != 0 || method.getParameterCount() > 0) {
								Class<?> eventClass = method.getAnnotation(FruttyPluginMain.class).eventClass();
										
								if(eventClass != void.class) {
									Method[] eventMethods = eventClass.getDeclaredMethods();
									
									Lookup lookup = MethodHandles.publicLookup();
									
									for(Method eventMts : eventMethods) {
										if(eventMts.isAnnotationPresent(FruttyEvent.class)) {
											if((eventMts.getModifiers() & Modifier.STATIC) != 0 && eventMts.getParameterCount() == 1) {
												Class<?> eventTypeClass = eventMts.getParameterTypes()[0];
												
												if(eventTypeClass.getSuperclass() != EventBase.class) {
													throw new IllegalArgumentException("Illegal type of argument for method: " + eventMts.getName());
												}
												if(eventTypeClass == MapInitEvent.class) {
													try {
														Main.mapLoadEvents.add(new EventHandleObject(lookup.unreflect(eventMts), EventHandleObject.ordinal(eventMts.getAnnotation(FruttyEvent.class).priority())));
													} catch (IllegalAccessException e) {
														e.printStackTrace();
													}
												}
											}else {
												throw new IllegalStateException("Method from class: " + eventClass + ", methodName: " + eventMts.getName() + " is not static or has more than 1 parameters");
											}
										}
									}
								}
								
								method.invoke(null);
								ranMain = true;
								break;
							}
							throw new IllegalStateException("Main method from plugin: " + pluginNames[k] + " is not static or has method arguments");
						}
					}
					
					if(!ranMain) {
						System.err.println("Can't find main method annotated with @FruttyPluginMain from plugin: " + pluginNames[k] + ", ignoring");
					}
				}
			} catch (IOException | SecurityException | IllegalArgumentException | ClassNotFoundException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void handleEvent(EventBase event, ArrayList<EventHandleObject> methods) {
		for(EventHandleObject handles : methods) event.invoke(handles.handle);
	}
	
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
	
	private Main() {}
	public static Object[] zoneStorage = {"normalZone", normalZone, "emptyZone", emptyZone, "appleZone", appleZone, "player1Zone", player1Zone, "player2Zone", player2Zone,
										   "cherryZone", cherryZone, "spawnerZone", spawnerZone, "chestZone", chestZone, "waterZone", waterZone, "skyZone", skyZone};
	public static int zoneIndex = 20;
	
	public static MapZoneBase getZoneFromName(String name) {
		for(int k = 0; k < zoneIndex; k += 2) {
			if(zoneStorage[k].equals(name)) {
				return (MapZoneBase) zoneStorage[k + 1];
			}
		}
		return null;
	}
}