package frutty.map.zones;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import frutty.Main;
import frutty.entity.EntityApple;
import frutty.entity.EntityPlayer;
import frutty.entity.zone.EntityAppleZone;
import frutty.entity.zone.EntityZone;
import frutty.gui.GuiIngame;
import frutty.gui.GuiStats;
import frutty.gui.editor.GuiTextureSelector;
import frutty.map.Map;
import frutty.map.MapZoneBase;
import frutty.map.interfaces.ITexturable;

public final class MapZoneFruit extends MapZoneBase implements ITexturable{
	public static final BufferedImage cherryTexture = Main.loadTexture("fruit", "cherry.png");
	
	public final EnumFruit fruitType;
	
	public MapZoneFruit(EnumFruit type) {
		super(true);
		fruitType = type;
	}

	@Override
	public void onBreak(int x, int y, int zoneIndex, int textureIndex, EntityPlayer player) {
		super.onBreak(x, y, zoneIndex, textureIndex, player);
		
		Map.score += 50;
		if(--Map.pickCount == 0) {
			GuiIngame.showMessageAndClose("You won!");
			GuiStats.compareScores();
		}
	}
	
	@Override
	public void draw(int x, int y, int textureIndex, Graphics graphics) {
		graphics.drawImage(GuiIngame.textures[textureIndex], x, y, 64, 64, null);
		if(fruitType == EnumFruit.APPLE) {
			graphics.drawImage(EntityApple.appleTexture, x, y, null);
		}else if(fruitType == EnumFruit.CHERRY) {
			graphics.drawImage(cherryTexture, x, y, null);
		}
	}

	@Override
	public EntityZone getZoneEntity(int x, int y, int zoneIndex) {
		return fruitType == EnumFruit.APPLE ? new EntityAppleZone(x, y, zoneIndex) : null;
	}
	
	@Override
	public boolean hasZoneEntity() {
		return fruitType == EnumFruit.APPLE;
	}
	
	@Override
	public ImageIcon[] getEditorTextureVars() {
		return fruitType == EnumFruit.APPLE ? GuiTextureSelector.appleTextures : GuiTextureSelector.cherryTextures;
	}
	
	@Override
	public void onZoneAdded(boolean isBackground, int x, int y) {
		if(!isBackground && this == Main.cherryZone) {
			++Map.pickCount;
		}
	}
	
	@Override
	public boolean isBreakable(int x, int y) {
		return fruitType == EnumFruit.CHERRY;
	}
	
	public static enum EnumFruit{
		APPLE,
		CHERRY;
	}

	@Override
	protected ImageIcon getEditorIcon() {
		var returnTexture = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
		var graphics = returnTexture.getGraphics();
		graphics.drawImage(Main.normalZone.editorTexture.get().getImage(), 0, 0, null);
		graphics.drawImage(fruitType == EnumFruit.APPLE ? EntityApple.appleTexture : MapZoneFruit.cherryTexture, 0, 0, null);
		return new ImageIcon(returnTexture);
	}
}