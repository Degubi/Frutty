package frutty.world.zones;

import frutty.tools.*;
import frutty.world.base.*;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

public final class MapZoneEmpty extends MapZoneBase{
	public MapZoneEmpty() {
		super("emptyZone", false, false);
	}

	@Override
	public void render(int x, int y, Material material, Graphics graphics) {
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
		graphics.dispose();
		return new ImageIcon(emptyZoneTexture);
	}
}