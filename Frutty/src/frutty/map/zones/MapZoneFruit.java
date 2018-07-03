package frutty.map.zones;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import frutty.Main;
import frutty.entity.EntityPlayer;
import frutty.entity.zone.EntityAppleZone;
import frutty.entity.zone.EntityZone;
import frutty.gui.GuiIngame;
import frutty.gui.GuiStats;
import frutty.gui.editor.GuiTextureSelector;
import frutty.map.Map;
import frutty.map.interfaces.ITexturable;
import frutty.map.interfaces.MapZoneBase;

public final class MapZoneFruit extends MapZoneBase implements ITexturable{
	public static final BufferedImage cherryTexture = Main.loadTexture("fruit", "cherry.png");
	public static final BufferedImage appleTexture = Main.loadTexture("fruit", "apple.png");
	public static int APPLE = 0, CHERRY = 1;
	
	public final int fruitType;
	
	public MapZoneFruit(int type) {
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
	public boolean hasZoneEntity() {
		return fruitType == APPLE;
	}
	
	@Override
	public ImageIcon[] getEditorTextureVars() {
		return fruitType == APPLE ? GuiTextureSelector.appleTextures : GuiTextureSelector.cherryTextures;
	}
	
	@Override
	public void onZoneAdded(boolean isBackground, int x, int y) {
		if(!isBackground && this == Main.cherryZone) {
			++Map.pickCount;
		}
	}
	
	@Override
	public boolean isBreakable(int x, int y) {
		return fruitType == CHERRY;
	}
	
	@Override
	public boolean isPassable(int x, int y) {
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