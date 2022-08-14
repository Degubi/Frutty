package frutty.gui.components;

import frutty.gui.GuiSettings.*;
import frutty.tools.*;
import frutty.world.*;
import java.awt.*;
import javax.swing.*;

public final class GuiWorldBackground extends JPanel {
    private final WorldZone[] zones;
    private final int[] xCoords, yCoords;
    private final Material[] materials;

    public GuiWorldBackground(WorldData worldData) {
        this.zones = worldData.zones;
        this.xCoords = worldData.xCoords;
        this.yCoords = worldData.yCoords;
        this.materials = worldData.materials;
    }

    public GuiWorldBackground(String worldName) {
        this(new WorldData(worldName, false, false));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        var zones = this.zones;
        var xCoords = this.xCoords;
        var yCoords = this.yCoords;
        var materials = this.materials;

        for(var k = 0; k < zones.length; ++k) {
            var zone = zones[k];

            if(Settings.renderDebugLevel < 2) {
                zone.renderInternal(xCoords[k], yCoords[k], materials[k], graphics);
            }else{
                zone.renderDebug(xCoords[k], yCoords[k], materials[k], graphics);
            }

            if(zone instanceof TransparentZone transparentZone) {
                transparentZone.drawAfter(xCoords[k], yCoords[k], materials[k], graphics);
            }
        }
    }
}