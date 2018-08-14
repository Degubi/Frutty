package frutty.world.zones;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import frutty.entity.EntityPlayer;
import frutty.tools.IOHelper;
import frutty.tools.Material;
import frutty.world.World;
import frutty.world.base.MapZoneBase;

public final class MapZonePortal extends MapZoneBase{
	public static final BufferedImage portalTexture = IOHelper.loadTexture("map/special", "portal.png");
	
	public MapZonePortal() {
		super("portalZone", true, false);
	}

	@Override
	public void draw(int x, int y, Material material, Graphics2D graphics) {
		graphics.drawImage(portalTexture, x, y, 64, 64, null);
	}

	@Override
	public void onZoneEntered(int x, int y, int zoneIndex, Material material, EntityPlayer player) {
		World.loadMap(World.nextMap, World.players.length == 2);
	}
	
	@Override
	protected ImageIcon getEditorIcon() {
		return new ImageIcon(portalTexture);
	}
}