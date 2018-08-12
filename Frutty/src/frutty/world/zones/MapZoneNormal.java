package frutty.world.zones;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import frutty.gui.GuiIngame;
import frutty.gui.components.GuiTextureSelector;
import frutty.world.base.MapZoneTexturable;

public final class MapZoneNormal extends MapZoneTexturable{
	public MapZoneNormal() {
		super("normalZone");
	}
	
	@Override
	public void draw(int x, int y, int textureIndex, Graphics2D graphics) {
		graphics.drawImage(GuiIngame.textures[textureIndex], x, y, 64, 64, null);
	}

	@Override
	protected ImageIcon getEditorIcon() {
		return new ImageIcon("./textures/map/normal.png");
	}

	@Override
	public BufferedImage getOverlayTexture() {
		return null;
	}
	
	@Override
	public ImageIcon[] getEditorTextures() {
		ImageIcon[] toReturn = new ImageIcon[GuiTextureSelector.textureNames.length];
		for(int k = 0; k < GuiTextureSelector.textureNames.length; ++k) {
			toReturn[k] = new ImageIcon((new ImageIcon("./textures/map/" + GuiTextureSelector.textureNames[k])).getImage().getScaledInstance(64, 64, Image.SCALE_DEFAULT));
		}
		return toReturn;
	}
}