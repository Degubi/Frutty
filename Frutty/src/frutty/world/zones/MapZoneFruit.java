package frutty.world.zones;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import frutty.Main;
import frutty.entity.EntityPlayer;
import frutty.entity.zone.EntityAppleZone;
import frutty.entity.zone.EntityZone;
import frutty.gui.GuiIngame;
import frutty.gui.editor.GuiTextureSelector;
import frutty.world.World;
import frutty.world.interfaces.ITexturable;
import frutty.world.interfaces.MapZoneBase;

public final class MapZoneFruit extends MapZoneBase implements ITexturable{
	public static final BufferedImage cherryTexture = Main.loadTexture("fruit", "cherry.png");
	public static final BufferedImage appleTexture = Main.loadTexture("fruit", "apple.png");
	public static int APPLE = 0, CHERRY = 1;
	
	public final int fruitType;
	
	public MapZoneFruit(int type) {
		super(type == APPLE ? "appleZone" : "cherryZone", true, type == APPLE, true);
		fruitType = type;
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
		if(fruitType == APPLE) {
			graphics.drawImage(appleTexture, x, y, null);
		}else if(fruitType == CHERRY) {
			graphics.drawImage(cherryTexture, x, y, null);
		}
	}

	@Override
	public EntityZone getZoneEntity(int x, int y, int zoneIndex) {
		return fruitType == APPLE ? new EntityAppleZone(x, y, zoneIndex) : null;
	}
	
	@Override
	public ImageIcon[] getEditorTextureVars() {
		return fruitType == APPLE ? GuiTextureSelector.appleTextures : GuiTextureSelector.cherryTextures;
	}
	
	@Override
	public void onZoneAdded(boolean isBackground, int x, int y) {
		if(!isBackground && this == Main.cherryZone) {
			++World.pickCount;
		}
	}
	
	@Override
	public boolean isBreakable(int x, int y) {
		return fruitType == CHERRY;
	}
	
	@Override
	public boolean canPlayerPass(int x, int y) {
		return fruitType == CHERRY;
	}

	@Override
	protected ImageIcon getEditorIcon() {
		var returnTexture = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
		var graphics = returnTexture.getGraphics();
		graphics.drawImage(Main.normalZone.editorTexture.get().getImage(), 0, 0, null);
		graphics.drawImage(fruitType == APPLE ? appleTexture : MapZoneFruit.cherryTexture, 0, 0, null);
		return new ImageIcon(returnTexture);
	}
}