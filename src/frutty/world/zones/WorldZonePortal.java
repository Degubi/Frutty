package frutty.world.zones;

import frutty.entity.living.*;
import frutty.tools.*;
import frutty.world.*;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

public final class WorldZonePortal extends WorldZone {
    public static final BufferedImage portalTexture = Material.loadTexture("world/special/portal.png");

    public WorldZonePortal() {
        super("portalZone", true, false);
    }

    @Override
    public void render(int x, int y, Material material, Graphics graphics) {
        graphics.drawImage(portalTexture, x, y, 64, 64, null);
    }

    @Override
    public void onZoneEntered(int x, int y, Material material, EntityPlayer player) {
        World.load(World.nextWorldName, World.players.length == 2);
    }

    @Override
    protected ImageIcon getEditorIcon() {
        return new ImageIcon(portalTexture);
    }
}