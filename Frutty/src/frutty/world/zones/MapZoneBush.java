package frutty.world.zones;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import frutty.Main;
import frutty.world.interfaces.ITransparentZone;
import frutty.world.interfaces.MapZoneBase;

public final class MapZoneBush extends MapZoneBase implements ITransparentZone{
	public static final BufferedImage texture = Main.loadTexture("map/special", "bush.png");
	
	public MapZoneBush() {
		super("bushZone", true, false, false);
	}
	
	@Override
	public void drawAfter(int x, int y, int textureIndex, Graphics graphics) {
		graphics.drawImage(texture, x, y, 64, 64, null);
	}

	@Override
	public void draw(int x, int y, int textureIndex, Graphics2D graphics) {
		graphics.setColor(Color.BLACK);
		graphics.fillRect(x, y, 64, 64);
	}

	@Override
	protected ImageIcon getEditorIcon() {
		BufferedImage toReturn = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
		Graphics drawer = toReturn.getGraphics();
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