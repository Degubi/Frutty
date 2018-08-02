package frutty.world.zones;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import frutty.Main;
import frutty.entity.EntityPlayer;
import frutty.gui.GuiIngame;
import frutty.world.World;
import frutty.world.interfaces.MapZoneTexturable;

public final class MapZoneCherry extends MapZoneTexturable{
	public static final BufferedImage cherryTexture = Main.loadTexture("fruit", "cherry.png");
	
	public MapZoneCherry() {
		super("cherryZone");
	}

	@Override
	public void onZoneEntered(int x, int y, int zoneIndex, int textureIndex, EntityPlayer player) {
		super.onZoneEntered(x, y, zoneIndex, textureIndex, player);
		
		World.score += 50;
		if(--World.pickCount == 0) {
			GuiIngame.showMessageAndClose("You won!");
		}
	}
	
	@Override
	public void draw(int x, int y, int textureIndex, Graphics2D graphics) {
		graphics.drawImage(GuiIngame.textures[textureIndex], x, y, 64, 64, null);
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
		graphics.drawImage(Main.normalZone.editorTexture.get().getImage(), 0, 0, null);
		graphics.drawImage(cherryTexture, 0, 0, null);
		return new ImageIcon(returnTexture);
	}

	@Override
	public BufferedImage getOverlayTexture() {
		return cherryTexture;
	}
}