package frutty.map.zones;

import java.awt.Graphics;

import javax.swing.ImageIcon;

import frutty.gui.GuiIngame;
import frutty.map.interfaces.MapZoneBase;

public final class MapZoneSky extends MapZoneBase{
	public MapZoneSky() {
		super(false);
	}

	@Override
	public void draw(int x, int y, int textureIndex, Graphics graphics) {
		graphics.drawImage(GuiIngame.skyTexture, x, y, x + 64, y + 64, x, y, x + 64, y + 64, null);
	}
	
	@Override
	public boolean isPassable(int x, int y) {
		return false;
	}

	@Override
	protected ImageIcon getEditorIcon() {
		return new ImageIcon("./textures/dev/sky.png");
	}
}