package frutty.gui.editor;

import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import frutty.entity.EntityEnemy;
import frutty.gui.editor.GuiEditor.TextureSelector;
import frutty.map.Map;
import frutty.map.MapZone;
import frutty.map.zones.MapZoneChest;
import frutty.map.zones.MapZoneEmpty;
import frutty.map.zones.MapZoneFruit;
import frutty.map.zones.MapZoneFruit.EnumFruit;
import frutty.map.zones.MapZoneNormal;
import frutty.map.zones.MapZoneSky;
import frutty.map.zones.MapZoneSpawner;
import frutty.map.zones.MapZoneWater;

public enum EnumEditorZone{
	Normal(0, "normal.png", true),
	Empty(1, "empty.png", false),
	Apple(2, "apple.png", true),
	Cherry(3, "cherry.png", true),
	Spawner(4, "spawner.png", false),
	Player1(5, "player1.png", false),
	Player2(6, "player2.png", false),
	Chest(7, "chest.png", true),
	Water(8, "water.png", false),
	Sky(9, "sky.png", false);
	
	private static final EnumEditorZone[] zones = values();
	
	public final int zoneIndex;
	public final ImageIcon icon;
	public final boolean hasTextureInfo;
	
	private EnumEditorZone(int index, String texture, boolean has){
		zoneIndex = index;
		icon = new ImageIcon("./textures/dev/" + texture);
		hasTextureInfo = has;
	}
	
	public MapZone handleMapZone(int index, int x, int y, boolean isBackground, ObjectInputStream input) throws IOException {
		switch(zoneIndex) {
			case 0: return new MapZoneNormal(x, y, index, input.readByte());
			case 2: return new MapZoneFruit(x, y, EnumFruit.APPLE, index, input.readByte());
			case 3: if(!isBackground) ++Map.currentMap.pickCount; 
					return new MapZoneFruit(x, y, EnumFruit.CHERRY, index, input.readByte());
			case 4: if(!isBackground) {
						for(int k = 0; k < +Map.currentMap.enemies.length; ++k) {
							Map.currentMap.enemies[k] = new EntityEnemy(x, y);
						}
			}
			return new MapZoneSpawner(x, y, index);
			case 7: return new MapZoneChest(x, y, index, input.readByte());	
			case 8: return new MapZoneWater(x, y, index);
			case 9: return new MapZoneSky(x, y, index);
			default: return new MapZoneEmpty(x, y, index);
		}
	}
	
	public ImageIcon[] getEditorTexture() {
		if(zoneIndex == 0) {
			return TextureSelector.normalTextures;
		}else if(zoneIndex == 2) {
			return TextureSelector.appleTextures;
		}else if(zoneIndex == 3) {
			return TextureSelector.cherryTextures;
		}else if(zoneIndex == 7) {
			return TextureSelector.chestTextures;
		}
		return null;
	}
	
	public static EnumEditorZone getFromIndex(int index) {
		return zones[index];
	}
	
	public void handleReading(GuiEditor editor, ObjectInputStream input, int x, int y, String[] textures) throws IOException {
		JButton button = new JButton(icon);
		button.setBounds(x * 64, y * 64, 64, 64);
		button.setMnemonic(zoneIndex);
		button.addMouseListener(editor);
		if(hasTextureInfo){
			int textureData = input.readByte();
			button.setActionCommand(textures[textureData]);
			button.setIcon(getEditorTexture()[TextureSelector.indexOf(textures[textureData] + ".png")]);
		}
		editor.zoneButtons.add(button);
		editor.add(button);
	}
}