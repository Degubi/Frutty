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
import frutty.gui.editor.GuiEditor.TextureSelector;
import frutty.map.Map;
import frutty.map.base.MapZone;
import frutty.map.interfaces.ITexturable;

public final class MapZoneFruit extends MapZone implements ITexturable{
	public static final BufferedImage cherryTexture = Main.loadTexture("fruit", "cherry.png");
	
	public final EnumFruit fruitType;
	
	public MapZoneFruit(EnumFruit type) {
		super(type == EnumFruit.APPLE ? 2 : 3, true);
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
		return fruitType == EnumFruit.APPLE ? TextureSelector.appleTextures : TextureSelector.cherryTextures;
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
}