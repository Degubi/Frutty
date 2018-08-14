package frutty.world.zones;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import frutty.tools.Material;
import frutty.world.base.MapZoneBase;

public final class MapZoneEmpty extends MapZoneBase{
	public MapZoneEmpty() {
		super("emptyZone", false, false);
	}

	@Override
	public void draw(int x, int y, Material material, Graphics2D graphics) {
		graphics.setColor(Color.BLACK);
		graphics.fillRect(x, y, 64, 64);
	}

	@Override
	public boolean isBreakable(int x, int y) {
		return false;
	}
	
	@Override
	public boolean canNPCPass(int x, int y) {
		return true;
	}

	@Override
	protected ImageIcon getEditorIcon() {
		var emptyZoneTexture = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
		var graphics = emptyZoneTexture.createGraphics();
		graphics.setColor(Color.BLACK);
		graphics.fillRect(0, 0, 64, 64);
		return new ImageIcon(emptyZoneTexture);
	}
}