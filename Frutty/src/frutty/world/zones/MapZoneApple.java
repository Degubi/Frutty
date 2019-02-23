package frutty.world.zones;

import frutty.entity.*;
import frutty.entity.zone.*;
import frutty.gui.*;
import frutty.tools.*;
import frutty.world.*;
import frutty.world.base.*;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

public final class MapZoneApple extends MapZoneTexturable implements IZoneEntityProvider{
	public static final BufferedImage appleTexture = Material.loadTexture("fruit", "apple.png");

	public MapZoneApple() {
		super("appleZone");
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
		graphics.drawImage(appleTexture, x, y, null);
	}

	@Override
	public EntityZone getZoneEntity(int x, int y) {
		return new EntityAppleZone(x, y);
	}
	
	@Override
	public boolean isBreakable(int x, int y) {
		return false;
	}
	
	@Override
	public boolean canPlayerPass(int x, int y) {
		return false;
	}

	@Override
	protected ImageIcon getEditorIcon() {
		var returnTexture = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
		var graphics = returnTexture.createGraphics();
		graphics.drawImage(MapZoneBase.normalZone.editorTexture.get().getImage(), 0, 0, null);
		graphics.drawImage(appleTexture, 0, 0, null);
		graphics.dispose();
		return new ImageIcon(returnTexture);
	}

	@Override
	public BufferedImage getOverlayTexture() {
		return appleTexture;
	}
}