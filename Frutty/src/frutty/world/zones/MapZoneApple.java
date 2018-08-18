package frutty.world.zones;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import frutty.entity.EntityPlayer;
import frutty.entity.zone.EntityAppleZone;
import frutty.entity.zone.EntityZone;
import frutty.gui.GuiIngame;
import frutty.tools.IOHelper;
import frutty.tools.Material;
import frutty.world.World;
import frutty.world.base.IZoneEntityProvider;
import frutty.world.base.MapZoneBase;
import frutty.world.base.MapZoneTexturable;

public final class MapZoneApple extends MapZoneTexturable implements IZoneEntityProvider{
	public static final BufferedImage appleTexture = IOHelper.loadTexture("fruit", "apple.png");

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
	public EntityZone getZoneEntity(int x, int y, int zoneIndex) {
		return new EntityAppleZone(x, y, zoneIndex);
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
		return new ImageIcon(returnTexture);
	}

	@Override
	public BufferedImage getOverlayTexture() {
		return appleTexture;
	}
}