package frutty.world.zones;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import frutty.tools.Material;
import frutty.world.base.MapZoneTexturable;

public final class MapZoneNormal extends MapZoneTexturable{
	public MapZoneNormal() {
		super("normalZone");
	}
	
	@Override
	public void draw(int x, int y, Material material, Graphics2D graphics) {
		graphics.drawImage(material.texture, x, y, 64, 64, null);
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
		return new ImageIcon[] {Material.NORMAL.editorTexture.get(), Material.STONE.editorTexture.get(), Material.DIRT.editorTexture.get(), Material.BRICK.editorTexture.get()};
	}
}