package frutty.world;

import frutty.*;
import frutty.entity.living.*;
import frutty.gui.*;
import frutty.gui.GuiSettings.*;
import frutty.plugin.event.world.*;
import frutty.sound.*;
import frutty.tools.*;
import frutty.world.zones.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

@SuppressWarnings("unused")
public abstract class WorldZone implements Serializable {
    private static final long serialVersionUID = 392316063689927131L;
    public transient final Lazy<ImageIcon> editorTexture = new Lazy<>(this::getEditorIconInternal);

    protected static final CachedSoundClip breakSound = new CachedSoundClip("zonebreak.wav");
    public final String zoneName;
    public final boolean hasShadowRender, hasParticleSpawns;

    public WorldZone(String name, boolean hasDarkening, boolean enableParticles) {
        zoneName = name;
        hasShadowRender = hasDarkening;
        hasParticleSpawns = enableParticles;
    }

    public WorldZone(String name) {
        this(name, true, true);
    }

    public abstract void render(int x, int y, Material material, Graphics graphics);
    protected abstract ImageIcon getEditorIcon();

    public boolean doesHidePlayer(int x, int y) {return false;}
    public boolean isBreakable(int x, int y) {return canPlayerPass(x, y);}
    public boolean canPlayerPass(int x, int y) {return true;}
    public boolean canNPCPass(int x, int y) {return false;}
    protected void onZoneAdded(boolean isCoop, int zoneCount, int x, int y) {}

    public void onZoneEntered(int x, int y, Material material, EntityPlayer player) {
        player.hidden = doesHidePlayer(x, y);

        if(isBreakable(x, y)) {
            World.setZoneEmptyAt(x, y);
            breakSound.start();
            GuiStats.zoneCount++;

            var aboveZoneIndex = World.worldCoordsToZoneIndex(x, y - 64);
            var zoneAbove = World.getZoneAtIndex(aboveZoneIndex);

            if(zoneAbove != null && zoneAbove instanceof ZoneEntityProvider) {
                World.zoneEntities[aboveZoneIndex].onNotified();
            }

            World.spawnFallingParticles(4 + Main.rand.nextInt(10), x, y, material);
        }
    }

    /********************************************************INTERNALS***********************************************************/


    public final void renderInternal(int x, int y, Material material, Graphics graphics) {
        render(x, y, material, graphics);

        if(hasShadowRender && Settings.graphicsLevel > 0) {
            graphics.setColor(GuiHelper.color_84Black);

            var till = y / 120;
            for(var k = 0; k < till && k < 4; ++k) {
                graphics.fillRect(x, y, 64, 64);
            }
        }
    }

    public final void renderDebug(int x, int y, Material material, Graphics graphics) {
        renderInternal(x, y, material, graphics);

        graphics.setColor(Color.WHITE);
        graphics.drawRect(x, y, 64, 64);
    }

    public final void renderPathfinding(int x, int y, Material material, Graphics graphics, boolean isActiveZone) {
        renderInternal(x, y, material, graphics);

        if(isActiveZone) {
            graphics.setColor(Color.RED);
            graphics.fillRect(x, y, 64, 64);
        }
    }

    public final void onZoneAddedInternal(boolean isCoop, int zoneCount, int x, int y) {
        if(Main.zoneAddedEvents.length > 0) {
            ZoneAddedEvent event = new ZoneAddedEvent(this, x, y);
            Main.invokeEvent(event, Main.zoneAddedEvents);

            if(!event.canceled) {
                onZoneAdded(isCoop, zoneCount, x, y);
            }
        }else{
            onZoneAdded(isCoop, zoneCount, x, y);
        }
    }

    private final ImageIcon getEditorIconInternal() {
        var icon = getEditorIcon();
        return (icon.getIconWidth() == 64 && icon.getIconHeight() == 64) ? icon : new ImageIcon(icon.getImage().getScaledInstance(64, 64, Image.SCALE_DEFAULT));
    }

    public static final WorldZoneNormal normalZone = new WorldZoneNormal();
    public static final WorldZoneEmpty emptyZone = new WorldZoneEmpty();
    public static final WorldZonePlayer player1Zone = new WorldZonePlayer(1);
    public static final WorldZonePlayer player2Zone = new WorldZonePlayer(2);
    public static final WorldZoneApple appleZone = new WorldZoneApple();
    public static final WorldZoneCherry cherryZone = new WorldZoneCherry();
    public static final WorldZoneSpawner spawnerZone = new WorldZoneSpawner();
    public static final WorldZoneChest chestZone = new WorldZoneChest();
    public static final WorldZoneWater waterZone = new WorldZoneWater();
    public static final WorldZoneSky skyZone = new WorldZoneSky();
    public static final WorldZoneBush bushZone = new WorldZoneBush();
    public static final WorldZonePortal portalZone = new WorldZonePortal();

    private static final List<WorldZone> zoneRegistry = toMutableList(normalZone, emptyZone, appleZone, player1Zone, player2Zone, cherryZone, spawnerZone, chestZone, waterZone, skyZone, bushZone, portalZone);

    /**
     * Register zone
     * @param zoneID String ID for the zone (format: "pluginID:zoneName")
     * @param zone The zone to register
     */
    public static void registerZone(WorldZone zone) {
        if(!zone.zoneName.contains(":")) {
            throw new IllegalArgumentException("Tried to register zone without plugin ID: " + zone.zoneName);
        }

        if(zoneRegistry.stream().anyMatch(zones -> zones.zoneName.equals(zone.zoneName))) {
            throw new IllegalArgumentException("Zone already registered with ID: " + zone.zoneName);
        }

        zoneRegistry.add(zone);
    }

    public static WorldZone getZoneFromName(String name) {
        for(var zone : zoneRegistry) {
            if(zone.zoneName.equals(name)) {
                return zone;
            }
        }

        throw new IllegalArgumentException("Unable to find zone by name '" + name + "'");
    }

    public static String[] zoneNames() {
        return zoneRegistry.stream().map(zone -> zone.zoneName).toArray(String[]::new);
    }

    @SafeVarargs
    private static<T> List<T> toMutableList(T... objs){
        var list = new ArrayList<T>(objs.length);

        for(var obj : objs) {
            list.add(obj);
        }
        return list;
    }
}