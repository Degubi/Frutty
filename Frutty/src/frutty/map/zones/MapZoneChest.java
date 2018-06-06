package frutty.map.zones;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import frutty.Main;
import frutty.entity.EntityPlayer;
import frutty.entity.effects.EntityEffectInvisible;
import frutty.entity.zone.EntityAppleZone;
import frutty.gui.GuiIngame;
import frutty.gui.GuiStats;
import frutty.gui.editor.GuiToolSelector.GuiTextureSelector;
import frutty.map.Map;
import frutty.map.Particle;
import frutty.map.base.MapZone;
import frutty.map.interfaces.ITexturable;

public final class MapZoneChest extends MapZone implements ITexturable{
	public static final BufferedImage chestTexture = Main.loadTexture("map/special", "chest.png");

	public MapZoneChest() {
		super(true);
	}
	
	@Override
	public void draw(int x, int y, int textureIndex, Graphics graphics) {
		graphics.drawImage(GuiIngame.textures[textureIndex], x, y, 64, 64, null);
		graphics.drawImage(chestTexture, x, y, 64, 64, null);
	}
	
	@Override
	public void onBreak(int x, int y, int zoneIndex, int textureIndex, EntityPlayer player) {
		player.entityEffects.add(new EntityEffectInvisible());
		Map.setZoneEmptyAt(zoneIndex);
		int upZoneIndex = x / 64 + ((y - 64) / 64 * ((Map.width + 64) / 64));
		MapZone up = Map.getZoneAtIndex(upZoneIndex);
		++GuiStats.zoneCount;
		if(up != null && up == Main.appleZone) {
			((EntityAppleZone)Map.zoneEntities[upZoneIndex]).notified = true;
		}
		
		Particle.addParticles(2 + Main.rand.nextInt(10), x, y, textureIndex);
	}

	@Override
	public ImageIcon[] getEditorTextureVars() {
		return GuiTextureSelector.chestTextures;
	}

	@Override
	protected ImageIcon getEditorIcon() {
		BufferedImage toReturn = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
		Graphics chestGraphics = toReturn.getGraphics();
		chestGraphics.drawImage(Main.normalZone.editorTexture.get().getImage(), 0, 0, null);
		chestGraphics.drawImage(chestTexture, 0, 0, null);
		return new ImageIcon(toReturn);
	}
}