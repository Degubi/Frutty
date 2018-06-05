package frutty.map.zones;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;

import frutty.gui.GuiIngame;
import frutty.gui.editor.GuiToolSelector.GuiTextureSelector;
import frutty.map.base.MapZone;
import frutty.map.interfaces.ITexturable;

public final class MapZoneNormal extends MapZone implements ITexturable{
	public MapZoneNormal() {
		super(0, true);
	}
	
	@Override
	public void draw(int x, int y, int textureIndex, Graphics graphics) {
		graphics.drawImage(GuiIngame.textures[textureIndex], x, y, 64, 64, null);
	}

	@Override
	public ImageIcon[] getEditorTextureVars() {
		return GuiTextureSelector.normalTextures;
	}

	@Override
	protected ImageIcon getEditorTexture() {
		return new ImageIcon(new ImageIcon("./textures/map/normal.png").getImage().getScaledInstance(64, 64, Image.SCALE_DEFAULT));
	}
}