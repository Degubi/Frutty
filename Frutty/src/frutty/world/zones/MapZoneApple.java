package frutty.world.zones;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import frutty.Main;
import frutty.entity.EntityPlayer;
import frutty.entity.zone.EntityAppleZone;
import frutty.entity.zone.EntityZone;
import frutty.gui.GuiIngame;
import frutty.gui.components.GuiTextureSelector;
import frutty.world.World;
import frutty.world.interfaces.ITexturable;
import frutty.world.interfaces.IZoneEntityProvider;
import frutty.world.interfaces.MapZoneBase;

public final class MapZoneApple extends MapZoneBase implements ITexturable, IZoneEntityProvider{
	public static final BufferedImage appleTexture = Main.loadTexture("fruit", "apple.png");

	public MapZoneApple() {
		super("appleZone");
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
		graphics.drawImage(appleTexture, x, y, null);
	}

	@Override
	public EntityZone getZoneEntity(int x, int y, int zoneIndex) {
		return new EntityAppleZone(x, y, zoneIndex);
	}
	
	@Override
	public ImageIcon[] getEditorTextureVars() {
		return GuiTextureSelector.appleTextures;
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
		var graphics = returnTexture.getGraphics();
		graphics.drawImage(Main.normalZone.editorTexture.get().getImage(), 0, 0, null);
		graphics.drawImage(appleTexture, 0, 0, null);
		return new ImageIcon(returnTexture);
	}
}