package frutty.world.zones;

import java.awt.Graphics2D;

import javax.swing.ImageIcon;

import frutty.gui.GuiIngame;
import frutty.world.interfaces.MapZoneBase;

public final class MapZoneSky extends MapZoneBase{
	public MapZoneSky() {
		super(false, false, false);
	}

	@Override
	public void draw(int x, int y, int textureIndex, Graphics2D graphics) {
		graphics.drawImage(GuiIngame.skyTexture, x, y, x + 64, y + 64, x, y, x + 64, y + 64, null);
	}
	
	@Override
	public boolean canPlayerPass(int x, int y) {
		return false;
	}
	
	@Override
	protected ImageIcon getEditorIcon() {
		return new ImageIcon("./textures/dev/sky.png");
	}
}