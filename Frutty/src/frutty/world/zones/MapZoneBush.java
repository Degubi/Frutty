package frutty.world.zones;

import frutty.tools.*;
import frutty.world.base.*;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

public final class MapZoneBush extends MapZoneBase implements ITransparentZone{
	public static final BufferedImage texture = IOHelper.loadTexture("map/special", "bush.png");
	
	public MapZoneBush() {
		super("bushZone", true, false);
	}
	
	@Override
	public void drawAfter(int x, int y, Material material, Graphics graphics) {
		graphics.drawImage(texture, x, y, 64, 64, null);
	}

	@Override
	public void draw(int x, int y, Material material, Graphics2D graphics) {
		graphics.setColor(Color.BLACK);
		graphics.fillRect(x, y, 64, 64);
	}

	@Override
	protected ImageIcon getEditorIcon() {
		var toReturn = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
		var drawer = toReturn.createGraphics();
		drawer.setColor(Color.BLACK);
		drawer.fillRect(0, 0, 64, 64);
		drawer.drawImage(texture, 0, 0, 64, 64, null);
		return new ImageIcon(toReturn);
	}
	
	@Override
	public boolean canNPCPass(int x, int y) {
		return true;
	}
	
	@Override
	public boolean isBreakable(int x, int y) {
		return false;
	}
	
	@Override
	public boolean doesHidePlayer(int x, int y) {
		return true;
	}
}