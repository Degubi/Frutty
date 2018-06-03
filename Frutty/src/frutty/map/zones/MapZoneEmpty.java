package frutty.map.zones;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import frutty.map.base.MapZone;

public final class MapZoneEmpty extends MapZone{
	public MapZoneEmpty() {
		super(1, false);
	}

	@Override
	public void draw(int x, int y, int textureIndex, Graphics graphics) {
		graphics.setColor(Color.BLACK);
		graphics.fillRect(x, y, 64, 64);
	}

	@Override
	public boolean isBreakable(int x, int y) {
		return true;
	}

	@Override
	protected ImageIcon getEditorTexture() {
		BufferedImage emptyZoneTexture = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
		Graphics emptyGraphics = emptyZoneTexture.getGraphics();
		emptyGraphics.setColor(Color.BLACK);
		emptyGraphics.fillRect(0, 0, 64, 64);
		return new ImageIcon(emptyZoneTexture);
	}
}