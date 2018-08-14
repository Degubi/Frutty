package frutty.world.zones;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import frutty.FruttyMain;
import frutty.entity.EntityPlayer;
import frutty.gui.GuiIngame;
import frutty.tools.IOHelper;
import frutty.tools.Material;
import frutty.world.World;
import frutty.world.base.MapZoneTexturable;

public final class MapZoneCherry extends MapZoneTexturable{
	public static final BufferedImage cherryTexture = IOHelper.loadTexture("fruit", "cherry.png");
	
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
	public void draw(int x, int y, Material material, Graphics2D graphics) {
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
		graphics.drawImage(FruttyMain.normalZone.editorTexture.get().getImage(), 0, 0, null);
		graphics.drawImage(cherryTexture, 0, 0, null);
		return new ImageIcon(returnTexture);
	}

	@Override
	public BufferedImage getOverlayTexture() {
		return cherryTexture;
	}
}