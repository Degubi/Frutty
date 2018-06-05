package frutty;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
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

@SuppressWarnings("boxing")
public final class Main {
	public static final HashMap<Integer, MapZone> zoneRegistry = new HashMap<>();
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
		zoneRegistry.put(normalZone.zoneID, normalZone);
		zoneRegistry.put(emptyZone.zoneID, emptyZone);
		zoneRegistry.put(appleZone.zoneID, appleZone);
		zoneRegistry.put(cherryZone.zoneID, cherryZone);
		zoneRegistry.put(spawnerZone.zoneID, spawnerZone);
		zoneRegistry.put(chestZone.zoneID, chestZone);
		zoneRegistry.put(waterZone.zoneID, waterZone);
		zoneRegistry.put(skyZone.zoneID, skyZone);
		
		loadPlugins();
		GuiMenu.showMenu(true);
		Settings.loadSettings();
		GuiStats.loadStats();
		new File("./saves/").mkdir();
	}
	
	public static boolean hasTextureInfo(int ID) {
		return zoneRegistry.get(ID) instanceof ITexturable;
	}
	
	public static ImageIcon[] getEditorTextureVariants(int ID) {
		return ((ITexturable)zoneRegistry.get(ID)).getEditorTextureVars();
	}
	
	public static MapZone handleMapReading(int ID) {
		if(ID == 5 || ID == 6) {
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
	
	public static void handleEditorReading(GuiEditor editor, ObjectInputStream input, int x, int y, String[] textures) throws IOException {
		int ID = input.readByte();
		
		if(ID == 5) {
			ZoneButton button = new ZoneButton(GuiToolSelector.player1Texture, editor);
			button.setBounds(x * 64, y * 64, 64, 64);
			button.zoneID = ID;
			editor.zoneButtons.add(button);
			editor.add(button);
		}else if(ID == 6) {
			ZoneButton button = new ZoneButton(GuiToolSelector.player2Texture, editor);
			button.setBounds(x * 64, y * 64, 64, 64);
			button.zoneID = ID;
			editor.zoneButtons.add(button);
			editor.add(button);
		}else{
			MapZone zone = zoneRegistry.get(ID);
			
			ZoneButton button = new ZoneButton(zone.editorTexture.get(), editor);
			button.setBounds(x * 64, y * 64, 64, 64);
			button.zoneID = ID;
			if(zone instanceof ITexturable){
				int textureData = input.readByte();
				button.zoneTexture = textures[textureData];
				button.setIcon(((ITexturable)zone).getEditorTextureVars()[GuiTextureSelector.indexOf(textures[textureData] + ".png")]);
			}
			editor.zoneButtons.add(button);
			editor.add(button);
		}
	}
	
	private static void loadPlugins() {
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
						if(hasInterface(loaded)) {
							Method method = loaded.getMethod("pluginMain");
							method.invoke(loaded.getDeclaredConstructor().newInstance());
						}
					}
				}
			} catch (IOException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static boolean hasInterface(Class<?> theClass) {
		for(var faces : theClass.getInterfaces()) {
			if(IFruttyPlugin.class.isAssignableFrom(faces)){
				return true;
			}
		}
		return false;
	}
}