package frutty.map.zones;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import frutty.map.MapZoneBase;

public final class MapZoneEmpty extends MapZoneBase{
	public MapZoneEmpty() {
		super(false);
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
	protected ImageIcon getEditorIcon() {
		var emptyZoneTexture = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
		var graphics = emptyZoneTexture.getGraphics();
		graphics.setColor(Color.BLACK);
		graphics.fillRect(0, 0, 64, 64);
		return new ImageIcon(emptyZoneTexture);
	}
}