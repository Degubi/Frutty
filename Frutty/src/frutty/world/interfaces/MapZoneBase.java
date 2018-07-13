package frutty.world.interfaces;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import javax.swing.ImageIcon;

import frutty.Main;
import frutty.entity.EntityPlayer;
import frutty.entity.zone.EntityAppleZone;
import frutty.entity.zone.EntityZone;
import frutty.gui.GuiHelper;
import frutty.gui.GuiSettings;
import frutty.gui.GuiStats;
import frutty.gui.editor.EditorZoneButton;
import frutty.gui.editor.GuiEditor;
import frutty.gui.editor.GuiTextureSelector;
import frutty.tools.Lazy;
import frutty.world.Particle;
import frutty.world.World;

@SuppressWarnings("unused")
public abstract class MapZoneBase implements Serializable{
	private static final long serialVersionUID = 392316063689927131L;
	public final Lazy<ImageIcon> editorTexture = new Lazy<>(this::getEditorIconInternal);
	
	public final String zoneName;
	public final boolean hasShadowRender, hasZoneEntity, hasParticleSpawns;
	
	public MapZoneBase(String name, boolean hasDarkening, boolean hasZoneEntity, boolean enableParticles) {
		zoneName = name;
		hasShadowRender = hasDarkening;
		this.hasZoneEntity = hasZoneEntity;
		hasParticleSpawns = enableParticles;
	}
	
	public MapZoneBase(String name) {
		this(name, true, false, true);
	}
	
	public abstract void draw(int x, int y, int textureIndex, Graphics2D graphics);
	protected abstract ImageIcon getEditorIcon();
	
	public boolean doesHidePlayer(int x, int y) {return false;}
	public boolean isBreakable(int x, int y) {return canPlayerPass(x, y);}
	public boolean canPlayerPass(int x, int y) {return true;}
	public boolean canNPCPass(int x, int y) {return false;}
	public void onZoneAdded(boolean isBackground, int x, int y) {}
	public EntityZone getZoneEntity(int x, int y, int zoneIndex) {return null;}
	
	public void onZoneEntered(int x, int y, int zoneIndex, int textureIndex, EntityPlayer player) {
		player.hidden = doesHidePlayer(x, y);

		if(isBreakable(x, y)) {
			World.setZoneEmptyAt(zoneIndex);
			GuiStats.zoneCount++;
			
			int checkIndex = zoneIndex - (World.width / 64) - 1;
			MapZoneBase up = World.getZoneAtIndex(checkIndex);
			if(up != null && up == Main.appleZone) {
				((EntityAppleZone)World.zoneEntities[checkIndex]).notified = true;
			}
			Particle.addParticles(2 + Main.rand.nextInt(10), x, y, textureIndex);
		}
	}
	
	
	
	
	public final void render(int x, int y, int textureIndex, Graphics2D graphics) {
		draw(x, y, textureIndex, graphics);
		
		if(hasShadowRender && GuiSettings.graphicsLevel > 0) {
			graphics.setColor(GuiHelper.color_84Black);
			
			int till = y / 120;
			for(int k = 0; k < till && k < 4; ++k) {
				graphics.fillRect(x, y, 64, 64);
			}
		}
		
		if(GuiSettings.renderDebugLevel > 1) {
			graphics.setColor(Color.WHITE);
			graphics.drawRect(x, y, 64, 64);
		}
	}
	
	private final ImageIcon getEditorIconInternal() {
		ImageIcon icon = getEditorIcon();
		return (icon.getIconWidth() == 64 && icon.getIconHeight() == 64) ? icon : new ImageIcon(icon.getImage().getScaledInstance(64, 64, Image.SCALE_DEFAULT));
	}
	
	public final void handleEditorReading(GuiEditor editor, String zoneID, ObjectInputStream input, int x, int y, String[] textures) throws IOException {
		EditorZoneButton button = new EditorZoneButton(editorTexture.get(), editor);
		button.setBounds(x * 64, y * 64, 64, 64);
		button.zoneID = zoneID;
		if(this instanceof ITexturable){
			int textureData = input.readByte();
			button.zoneTexture = textures[textureData];
			button.setIcon(((ITexturable)this).getEditorTextureVars()[GuiTextureSelector.indexOf(textures[textureData] + ".png")]);
		}
		editor.zoneButtons.add(button);
		editor.add(button);
	}
	
	public static boolean isEmpty(int x, int y) {
		MapZoneBase zone = World.getZoneAtPos(x, y);
		return zone != null && zone == Main.emptyZone;
	}
	
	public static boolean isEmptyAt(int index) {
		MapZoneBase zone = World.getZoneAtIndex(index);
		return zone != null && zone == Main.emptyZone;
	}
}