package frutty.world.zones;

import frutty.tools.*;
import frutty.world.*;
import frutty.world.base.*;
import java.awt.*;
import javax.swing.*;

public final class MapZoneSky extends MapZoneBase{
	public MapZoneSky() {
		super("skyZone", false, false);
	}

	@Override
	public void draw(int x, int y, Material material, Graphics graphics) {
		graphics.drawImage(World.skyTexture, x, y, x + 64, y + 64, x, y, x + 64, y + 64, null);
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