package frutty;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
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
import javax.swing.JButton;

import frutty.entity.EntityApple;
import frutty.gui.GuiMenu;
import frutty.gui.GuiSettings.Settings;
import frutty.gui.GuiStats;
import frutty.gui.editor.GuiEditor;
import frutty.gui.editor.GuiEditor.TextureSelector;
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
import frutty.plugin.Lazy;

@SuppressWarnings({"boxing", "unchecked"})
public final class Main {
	public static final Lazy<ImageIcon>[] editorButtonIcons = new Lazy[30];
	public static final HashMap<Integer, MapZone> zoneRegistry = new HashMap<>();
	
	public static final Random rand = new Random();
	
	public static final MapZoneNormal normalZone = new MapZoneNormal();
	public static final MapZoneEmpty emptyZone = new MapZoneEmpty();
	public static final MapZoneFruit appleZone = new MapZoneFruit(EnumFruit.APPLE);
	public static final MapZoneFruit cherryZone = new MapZoneFruit(EnumFruit.CHERRY);
	public static final MapZoneSpawner spawnerZone = new MapZoneSpawner();
	public static final MapZoneChest chestZone = new MapZoneChest();
	public static final MapZoneWater waterZone = new MapZoneWater();
	public static final MapZoneSky skyZone = new MapZoneSky();
	
	public static void main(String[] args){
		registerZone(normalZone, "map", "normal");
		registerZone(emptyZone, null, null);
		registerZone(appleZone, null, null);
		registerZone(cherryZone, null, null);
		registerZone(spawnerZone, "dev", "spawner");
		registerZone(chestZone, null, null);
		registerZone(waterZone, null, null);
		registerZone(skyZone, "dev", "sky");
		
		loadPlugins();
		GuiMenu.showMenu();
		Settings.loadSettings();
		GuiStats.loadStats();
		new File("./saves/").mkdir();
		
		editorButtonIcons[1] = new Lazy<>(() -> {
			BufferedImage emptyZoneTexture = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
			Graphics emptyGraphics = emptyZoneTexture.getGraphics();
			emptyGraphics.setColor(Color.BLACK);
			emptyGraphics.fillRect(0, 0, 64, 64);
			return new ImageIcon(emptyZoneTexture);
		});
		editorButtonIcons[2] = new Lazy<>(() -> {
			BufferedImage apple = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
			Graphics graph = apple.getGraphics();
			graph.drawImage(editorButtonIcons[0].get().getImage(), 0, 0, null);
			graph.drawImage(EntityApple.appleTexture, 0, 0, null);
			return new ImageIcon(apple);
		});
		editorButtonIcons[3] = new Lazy<>(() -> {
			BufferedImage apple = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
			Graphics graph = apple.getGraphics();
			graph.drawImage(editorButtonIcons[0].get().getImage(), 0, 0, null);
			graph.drawImage(MapZoneFruit.cherryTexture, 0, 0, null);
			return new ImageIcon(apple);
		});
		editorButtonIcons[5] = new Lazy<>(() -> new ImageIcon("./textures/dev/player1.png"));
		editorButtonIcons[6] = new Lazy<>(() -> new ImageIcon("./textures/dev/player2.png"));
		editorButtonIcons[7] = new Lazy<>(() -> {
			BufferedImage chestTexture = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
			Graphics chestGraphics = chestTexture.getGraphics();
			chestGraphics.drawImage(editorButtonIcons[0].get().getImage(), 0, 0, null);
			chestGraphics.drawImage(MapZoneChest.chest, 0, 0, null);
			return new ImageIcon(chestTexture);
		});
		editorButtonIcons[8] = new Lazy<>(() -> {
			BufferedImage waterTexture = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
			Graphics waterGraphics = waterTexture.getGraphics();
			waterGraphics.setColor(Color.BLACK);
			waterGraphics.fillRect(0, 0, 64, 64);
			waterGraphics.drawImage(MapZoneWater.texture, 0, 0, 64, 64, 0, 0, 16, 16, null);
			return new ImageIcon(waterTexture);
		});
	}
	
	public static void registerZone(MapZone zone, String folder, String editorName) {
		zoneRegistry.put(zone.zoneID, zone);
		if(folder != null) {
			editorButtonIcons[zone.zoneID] = new Lazy<>(() -> folder.equals("map")
					? new ImageIcon(new ImageIcon("./textures/map/" + editorName + ".png").getImage().getScaledInstance(64, 64, Image.SCALE_DEFAULT))
					: new ImageIcon("./textures/" + folder + "/" + editorName + ".png"));
		}
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
		MapZone zone = zoneRegistry.get(ID);
		
		JButton button = new JButton(editorButtonIcons[ID].get());
		button.setBounds(x * 64, y * 64, 64, 64);
		button.setMnemonic(ID);
		button.addMouseListener(editor);
		if(zone != null && zone instanceof ITexturable){
			int textureData = input.readByte();
			button.setActionCommand(textures[textureData]);
			button.setIcon(((ITexturable)zone).getEditorTextureVars()[TextureSelector.indexOf(textures[textureData] + ".png")]);
		}
		editor.zoneButtons.add(button);
		editor.add(button);
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
						Class<?> loaded = urlClass.loadClass(mainClassNames[k]);
						if(hasInterface(loaded)) {
							Method method = loaded.getMethod("register");
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
		for(Class<?> faces : theClass.getInterfaces()) {
			if(IFruttyPlugin.class.isAssignableFrom(faces)){
				return true;
			}
		}
		return false;
	}
}