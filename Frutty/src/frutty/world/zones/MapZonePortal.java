package frutty.world.zones;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import frutty.Main;
import frutty.entity.EntityPlayer;
import frutty.world.World;
import frutty.world.interfaces.MapZoneBase;

public final class MapZonePortal extends MapZoneBase{
	public static final BufferedImage portalTexture = Main.loadTexture("map/special", "portal.png");
	
	public MapZonePortal() {
		super("portalZone", false, false);
	}

	@Override
	public void draw(int x, int y, int textureIndex, Graphics2D graphics) {
		graphics.drawImage(portalTexture, x, y, 64, 64, null);
	}

	@Override
	public void onZoneEntered(int x, int y, int zoneIndex, int textureIndex, EntityPlayer player) {
		World.loadMap(World.nextMap, World.players.length == 2);
	}
	
	@Override
	protected ImageIcon getEditorIcon() {
		return new ImageIcon(portalTexture);
	}
}