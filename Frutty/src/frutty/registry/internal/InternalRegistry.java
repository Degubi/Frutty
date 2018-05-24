package frutty.registry.internal;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import frutty.Main;
import frutty.gui.editor.GuiEditor;
import frutty.gui.editor.GuiEditor.TextureSelector;
import frutty.map.MapZone;
import frutty.map.interfaces.ITexturable;

public final class InternalRegistry {
	private InternalRegistry() {}
	
	public static final Lazy<ImageIcon>[] editorButtonIcons = new Lazy[20];
	public static final HashMap<Integer, MapZone> zoneRegistry = new HashMap<>();
	
	static{
		editorButtonIcons[5] = new Lazy<>(() -> new ImageIcon("./textures/dev/player1.png"));
		editorButtonIcons[6] = new Lazy<>(() -> new ImageIcon("./textures/dev/player2.png"));
	}
	
	public static void registerZone(MapZone zone, String editorName) {
		zoneRegistry.put(zone.zoneID, zone);
		editorButtonIcons[zone.zoneID] = new Lazy<>(() -> new ImageIcon("./textures/dev/" + editorName + ".png"));
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
}