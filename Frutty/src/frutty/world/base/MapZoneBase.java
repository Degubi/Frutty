package frutty.world.base;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.Serializable;

import javax.swing.ImageIcon;

import frutty.FruttyMain;
import frutty.entity.EntityPlayer;
import frutty.gui.GuiSettings.Settings;
import frutty.gui.GuiStats;
import frutty.plugin.event.world.ZoneAddedEvent;
import frutty.plugin.internal.EventHandle;
import frutty.sound.CachedSoundClip;
import frutty.tools.GuiHelper;
import frutty.tools.Lazy;
import frutty.tools.Material;
import frutty.world.Particle;
import frutty.world.World;

@SuppressWarnings("unused")
public abstract class MapZoneBase implements Serializable{
	private static final long serialVersionUID = 392316063689927131L;
	public transient final Lazy<ImageIcon> editorTexture = new Lazy<>(this::getEditorIconInternal);
	
	protected static final CachedSoundClip breakSound = new CachedSoundClip("zonebreak.wav");
	public final String zoneName;
	public final boolean hasShadowRender, hasParticleSpawns;
	
	public MapZoneBase(String name, boolean hasDarkening, boolean enableParticles) {
		zoneName = name;
		hasShadowRender = hasDarkening;
		hasParticleSpawns = enableParticles;
	}
	
	public MapZoneBase(String name) {
		this(name, true, true);
	}
	
	public abstract void draw(int x, int y, Material material, Graphics2D graphics);
	protected abstract ImageIcon getEditorIcon();
	
	public boolean doesHidePlayer(int x, int y) {return false;}
	public boolean isBreakable(int x, int y) {return canPlayerPass(x, y);}
	public boolean canPlayerPass(int x, int y) {return true;}
	public boolean canNPCPass(int x, int y) {return false;}
	protected void onZoneAdded(boolean isCoop, int x, int y) {}
	
	public void onZoneEntered(int x, int y, int zoneIndex, Material material, EntityPlayer player) {
		player.hidden = doesHidePlayer(x, y);

		if(isBreakable(x, y)) {
			World.setZoneEmptyAt(zoneIndex);
			breakSound.start();
			GuiStats.zoneCount++;
			
			int checkIndex = zoneIndex - (World.width / 64) - 1;
			MapZoneBase up = World.getZoneAtIndex(checkIndex);
			if(up != null && up instanceof IZoneEntityProvider) {
				World.zoneEntities[checkIndex].onNotified();
			}
			
			Particle.spawnFallingParticles(2 + FruttyMain.rand.nextInt(10), x, y, material);
		}
	}
	
	
	/********************************************************INTERNALS***********************************************************/
	
	
	public final void render(int x, int y, Material material, Graphics2D graphics) {
		draw(x, y, material, graphics);
		
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
	
	public final void onZoneAddedInternal(boolean isCoop, int x, int y) {
		if(FruttyMain.hasPlugins && !EventHandle.zoneAddedEvents.isEmpty()) {
			ZoneAddedEvent event = new ZoneAddedEvent(this, x, y);
			EventHandle.handleEvent(event, EventHandle.zoneAddedEvents);
			
			if(!event.canceled) {
				onZoneAdded(isCoop, x, y);
			}
		}else{
			onZoneAdded(isCoop, x, y);
		}
	}
	
	private final ImageIcon getEditorIconInternal() {
		ImageIcon icon = getEditorIcon();
		return (icon.getIconWidth() == 64 && icon.getIconHeight() == 64) ? icon : new ImageIcon(icon.getImage().getScaledInstance(64, 64, Image.SCALE_DEFAULT));
	}
	
	public static boolean isEmpty(int x, int y) {
		MapZoneBase zone = World.getZoneAtPos(x, y);
		return zone != null && zone == FruttyMain.emptyZone;
	}
	
	public static boolean isEmptyAt(int index) {
		MapZoneBase zone = World.getZoneAtIndex(index);
		return zone != null && zone == FruttyMain.emptyZone;
	}
}