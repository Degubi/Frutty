package frutty.map.zones;

import java.awt.Graphics2D;

import javax.swing.ImageIcon;

import frutty.gui.GuiIngame;
import frutty.gui.editor.GuiTextureSelector;
import frutty.map.interfaces.ITexturable;
import frutty.map.interfaces.MapZoneBase;

public final class MapZoneNormal extends MapZoneBase implements ITexturable{
	
	@Override
	public void draw(int x, int y, int textureIndex, Graphics2D graphics) {
		graphics.drawImage(GuiIngame.textures[textureIndex], x, y, 64, 64, null);
	}

	@Override
	public ImageIcon[] getEditorTextureVars() {
		return GuiTextureSelector.normalTextures;
	}

	@Override
	protected ImageIcon getEditorIcon() {
		return new ImageIcon("./textures/map/normal.png");
	}
}