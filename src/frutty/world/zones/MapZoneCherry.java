package frutty.world.zones;

import frutty.entity.*;
import frutty.gui.*;
import frutty.tools.*;
import frutty.world.*;
import frutty.world.base.*;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

public final class MapZoneCherry extends MapZoneTexturable{
	public static final BufferedImage cherryTexture = Material.loadTexture("fruit", "cherry.png");
	
	public MapZoneCherry() {
		super("cherryZone");
	}

	@Override
	public void onZoneEntered(int x, int y, int zoneIndex, Material material, EntityPlayer player) {
		super.onZoneEntered(x, y, zoneIndex, material, player);
		
		World.score += 50;
		if(--World.pickCount == 0) {
			GuiIngame.showMessageAndClose("You won!");
		}
	}
	
	@Override
	public void draw(int x, int y, Material material, Graphics graphics) {
		graphics.drawImage(material.texture, x, y, 64, 64, null);
		graphics.drawImage(cherryTexture, x, y, null);
	}
	
	@Override
	public void onZoneAdded(boolean isCoop, int x, int y) {
		++World.pickCount;
	}

	@Override
	protected ImageIcon getEditorIcon() {
		var returnTexture = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
		var graphics = returnTexture.createGraphics();
		graphics.drawImage(MapZoneBase.normalZone.editorTexture.get().getImage(), 0, 0, null);
		graphics.drawImage(cherryTexture, 0, 0, null);
		graphics.dispose();
		return new ImageIcon(returnTexture);
	}

	@Override
	public BufferedImage getOverlayTexture() {
		return cherryTexture;
	}
}