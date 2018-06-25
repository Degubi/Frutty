package frutty;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
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
import frutty.map.MapZone;
import frutty.map.interfaces.ITexturable;
import frutty.map.zones.MapZoneChest;
import frutty.map.zones.MapZoneEmpty;
import frutty.map.zones.MapZoneFruit;
import frutty.map.zones.MapZoneFruit.EnumFruit;
import frutty.map.zones.MapZoneNormal;
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
	public static final HashMap<String, MapZone> zoneRegistry = new HashMap<>(8);
	public static final ArrayList<Plugin> pluginList = new ArrayList<>(2);
	public static final ArrayList<EventHandleObject> mapLoadEvents = new ArrayList<>(0);
	
	public static final Random rand = new Random();
	public static final Plugin gamePluginContainer = new Plugin("Frutty", "Base plugin for the game", null, Version.from(1, 0, 0), "https://pastebin.com/raw/m5qJbnks");

	public static final MapZoneNormal normalZone = new MapZoneNormal();
	public static final MapZoneEmpty emptyZone = new MapZoneEmpty();
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
	
	public static String getZoneName(MapZone zone) {
		var entries = zoneRegistry.entrySet();
		
		for(var entry : entries) {
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
			return ImageIO.read(Files.newInputStream(Paths.get("./textures/" + prefix + "/" + name)));
		}catch(IOException e){
			System.err.println("Can't find texture: " + prefix + "/" + name + " from class: " + Thread.currentThread().getStackTrace()[3].getClassName());
			return null;
		}
	}
	
	public static void handleEditorReading(GuiEditor editor, String zoneID, ObjectInputStream input, int x, int y, String[] textures) throws IOException {
		if(zoneID.equals("player1Zone")) {
			var button = new ZoneButton(GuiToolSelector.player1Texture, editor);
			button.setBounds(x * 64, y * 64, 64, 64);
			button.zoneID = zoneID;
			editor.zoneButtons.add(button);
			editor.add(button);
		}else if(zoneID.equals("player2Zone")) {
			var button = new ZoneButton(GuiToolSelector.player2Texture, editor);
			button.setBounds(x * 64, y * 64, 64, 64);
			button.zoneID = zoneID;
			editor.zoneButtons.add(button);
			editor.add(button);
		}else{
			var zone = zoneRegistry.get(zoneID);
			
			var button = new ZoneButton(zone.editorTexture.get(), editor);
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