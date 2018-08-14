package frutty.world.zones;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import frutty.FruttyMain;
import frutty.entity.EntityPlayer;
import frutty.entity.effects.EntityEffectInvisible;
import frutty.tools.IOHelper;
import frutty.tools.Material;
import frutty.world.base.MapZoneTexturable;

public final class MapZoneChest extends MapZoneTexturable{
	public static final BufferedImage chestTexture = IOHelper.loadTexture("map/special", "chest.png");
	
	public MapZoneChest() {
		super("chestZone");
	}
	
	@Override
	public void draw(int x, int y, Material material, Graphics2D graphics) {
		graphics.drawImage(material.texture, x, y, 64, 64, null);
		graphics.drawImage(chestTexture, x, y, 64, 64, null);
	}
	
	@Override
	public void onZoneEntered(int x, int y, int zoneIndex, Material material, EntityPlayer player) {
		player.entityEffects.add(new EntityEffectInvisible());
		
		super.onZoneEntered(x, y, zoneIndex, material, player);
	}
	
	@Override
	protected ImageIcon getEditorIcon() {
		var toReturn = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
		var graphics = toReturn.createGraphics();
		graphics.drawImage(FruttyMain.normalZone.editorTexture.get().getImage(), 0, 0, null);
		graphics.drawImage(chestTexture, 0, 0, null);
		return new ImageIcon(toReturn);
	}

	@Override
	public BufferedImage getOverlayTexture() {
		return chestTexture;
	}
}