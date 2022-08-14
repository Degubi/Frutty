package frutty.gui.components;

import frutty.*;
import frutty.gui.GuiSettings.*;
import frutty.tools.*;
import frutty.world.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import javax.swing.*;

public final class GuiWorldBackground extends JPanel {
    private final WorldZone[] zones;
    private final int[] xCoords, yCoords;
    private final Material[] materials;

    public GuiWorldBackground(String worldName) {
        this.zones = new WorldZone[140];
        this.xCoords = new int[140];
        this.yCoords = new int[140];
        this.materials = new Material[140];

        loadBackgroundWorld(worldName, zones, xCoords, yCoords, materials);
    }

    public GuiWorldBackground(WorldZone[] zones, int[] xCoords, int[] yCoords, Material[] materials) {
        this.zones = zones;
        this.xCoords = xCoords;
        this.yCoords = yCoords;
        this.materials = materials;
    }

    public static void loadBackgroundWorld(String worldName, WorldZone[] zones, int[] xCoords, int[] yCoords, Material[] materials) {
        System.out.println(Main.worldLoadingSystemLabel + "Started loading background world: " + worldName);

        try(var input = new ObjectInputStream(Files.newInputStream(Path.of(GamePaths.WORLDS_DIR + worldName)))){
            var zoneIDCache = (String[]) input.readObject();
            var textureCache = (String[]) input.readObject();

            input.readUTF(); //Sky texture
            input.readShort(); input.readShort();  //Width height
            input.readUTF(); //Next world

            var materialRegistry = Material.materialRegistry;
            for(int y = 0, zoneIndex = 0; y < 640; y += 64) {
                for(int x = 0; x < 896; x += 64) {
                    var zone = WorldZone.getZoneFromName(zoneIDCache[input.readByte()]);

                    if(zone instanceof InternalZone) {
                        zone = ((InternalZone) zone).getReplacementZone();
                    }

                    if(zone instanceof WorldZoneTexturable) {
                        materials[zoneIndex] = materialRegistry.get(textureCache[input.readByte()]);
                    }
                    xCoords[zoneIndex] = x;
                    yCoords[zoneIndex] = y;
                    zones[zoneIndex++] = zone;
                }
            }
        }catch(IOException | ClassNotFoundException e){
            System.out.println(Main.worldLoadingSystemLabel + "Failed to load background world: " + worldName);
            e.printStackTrace();
        }

        System.out.println(Main.worldLoadingSystemLabel + "Finished loading background world: " + worldName);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        var zonesLocal = zones;
        var xCoordsLocal = xCoords;
        var yCoordsLocal = yCoords;
        var materialsLocal = materials;

        for(var k = 0; k < zonesLocal.length; ++k) {
            var zone = zonesLocal[k];

            if(Settings.renderDebugLevel < 2) {
                zone.renderInternal(xCoordsLocal[k], yCoordsLocal[k], materialsLocal[k], graphics);
            }else{
                zone.renderDebug(xCoordsLocal[k], yCoordsLocal[k], materialsLocal[k], graphics);
            }

            if(zone instanceof TransparentZone) {
                ((TransparentZone) zone).drawAfter(xCoordsLocal[k], yCoordsLocal[k], materialsLocal[k], graphics);
            }
        }
    }
}