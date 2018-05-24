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
import frutty.gui.editor.GuiEditor.TextureSelector;
import frutty.map.Map;
import frutty.map.MapZone;
import frutty.map.Particle;
import frutty.map.interfaces.ITexturable;
import frutty.registry.internal.InternalRegistry;

public final class MapZoneChest extends MapZone implements ITexturable{
	private static final BufferedImage chest = InternalRegistry.loadTexture("map/special", "chest.png");

	public MapZoneChest() {
		super(7, true);
	}
	
	@Override
	public void draw(int x, int y, int textureIndex, Graphics graphics) {
		graphics.drawImage(GuiIngame.textures[textureIndex], x, y, 64, 64, null);
		graphics.drawImage(chest, x, y, 64, 64, null);
	}
	
	@Override
	public void onBreak(int x, int y, int zoneIndex, int textureIndex, EntityPlayer player) {
		player.entityEffects.add(new EntityEffectInvisible());
		Map.setZoneEmptyAt(zoneIndex);
		int upZoneIndex = x / 64 + ((y - 64) / 64 * ((Map.currentMap.width + 64) / 64));
		MapZone up = Map.getZoneAtIndex(upZoneIndex);
		++GuiStats.zoneCount;
		if(up != null && up == Main.appleZone) {
			((EntityAppleZone)Map.currentMap.zoneEntities[upZoneIndex]).notified = true;
		}
		
		Particle.addParticles(2 + Main.rand.nextInt(10), x, y, textureIndex);
	}

	@Override
	public ImageIcon[] getEditorTextureVars() {
		return TextureSelector.chestTextures;
	}
}