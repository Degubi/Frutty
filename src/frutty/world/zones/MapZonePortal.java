package frutty.world.zones;

import frutty.entity.*;
import frutty.tools.*;
import frutty.world.*;
import frutty.world.base.*;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

public final class MapZonePortal extends MapZoneBase{
	public static final BufferedImage portalTexture = Material.loadTexture("map/special", "portal.png");
	
	public MapZonePortal() {
		super("portalZone", true, false);
	}

	@Override
	public void render(int x, int y, Material material, Graphics graphics) {
		graphics.drawImage(portalTexture, x, y, 64, 64, null);
	}

	@Override
	public void onZoneEntered(int x, int y, Material material, EntityPlayer player) {
		World.loadMap(World.nextMap, World.players.length == 2);
	}
	
	@Override
	protected ImageIcon getEditorIcon() {
		return new ImageIcon(portalTexture);
	}
}