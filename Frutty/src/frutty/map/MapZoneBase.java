package frutty.map;

import java.awt.Color;
import java.awt.Graphics;
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
import frutty.gui.GuiStats;
import frutty.gui.Settings;
import frutty.gui.editor.GuiEditor;
import frutty.gui.editor.GuiEditor.ZoneButton;
import frutty.gui.editor.GuiTextureSelector;
import frutty.map.interfaces.ITexturable;
import frutty.tools.Lazy;

@SuppressWarnings("unused")
public abstract class MapZoneBase implements Serializable{
	private static final long serialVersionUID = 392316063689927131L;
	public transient final Lazy<ImageIcon> editorTexture = new Lazy<>(this::getEditorIconInternal);
	
	public final boolean hasShadowRender;
	
	public MapZoneBase(boolean hasDarkening) {
		hasShadowRender = hasDarkening;
	}
	
	public abstract void draw(int x, int y, int textureIndex, Graphics graphics);
	
	protected abstract ImageIcon getEditorIcon();
	private final ImageIcon getEditorIconInternal() {
		ImageIcon icon = getEditorIcon();
		return (icon.getIconWidth() == 64 && icon.getIconHeight() == 64) ? icon : new ImageIcon(icon.getImage().getScaledInstance(64, 64, Image.SCALE_DEFAULT));
	}
	
	public boolean hasZoneEntity() {return false;}
	public boolean isBreakable(int x, int y) {return true;}
	public void onZoneAdded(boolean isBackground, int x, int y) {}
	public EntityZone getZoneEntity(int x, int y, int zoneIndex) {return null;}
	
	/**Call super.onBreak if you want a normal breakable zone
	 * @param player Player obj used in subclasses */
	public void onBreak(int x, int y, int zoneIndex, int textureIndex, EntityPlayer player) {
		if(isBreakable(x, y) && this != Main.emptyZone && this != Main.waterZone) {
			Map.setZoneEmptyAt(zoneIndex);
			++GuiStats.zoneCount;
			
			int checkIndex = zoneIndex - (Map.width / 64) - 1;
			MapZoneBase up = Map.getZoneAtIndex(checkIndex);
			if(up != null && up == Main.appleZone) {
				((EntityAppleZone)Map.zoneEntities[checkIndex]).notified = true;
			}
			Particle.addParticles(2 + Main.rand.nextInt(10), x, y, textureIndex);
		}
	}
	
	public final void handleEditorReading(GuiEditor editor, String zoneID, ObjectInputStream input, int x, int y, String[] textures) throws IOException {
		ZoneButton button = new ZoneButton(editorTexture.get(), editor);
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
	
	public final void render(int x, int y, int textureIndex, Graphics graphics) {
		draw(x, y, textureIndex, graphics);
		
		if(hasShadowRender && Settings.graphicsLevel > 0) {
			graphics.setColor(GuiHelper.color_84Black);
			
			int till = y / 120;
			for(int k = 0; k < till && k < 4; ++k) {
				graphics.fillRect(x, y, 64, 64);
			}
		}
		
		if(Settings.renderDebugLevel > 1) {
			graphics.setColor(Color.WHITE);
			graphics.drawRect(x, y, 64, 64);
		}
	}
	
	public static boolean isEmpty(int x, int y) {
		MapZoneBase zone = Map.getZoneAtPos(x, y);
		return zone != null && zone == Main.emptyZone;
	}
	
	public static boolean isEmptyAt(int index) {
		MapZoneBase zone = Map.getZoneAtIndex(index);
		return zone != null && zone == Main.emptyZone;
	}
}