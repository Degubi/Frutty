package frutty.gui.components;

import frutty.*;
import frutty.gui.GuiSettings.*;
import frutty.tools.*;
import frutty.world.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import javax.swing.*;

public final class GuiMapBackground extends JPanel {
    private final MapZoneBase[] zones;
    private final int[] xCoords, yCoords;
    private final Material[] materials;

    public GuiMapBackground(String mapName) {
        this.zones = new MapZoneBase[140];
        this.xCoords = new int[140];
        this.yCoords = new int[140];
        this.materials = new Material[140];

        loadBackgroundMap(mapName, zones, xCoords, yCoords, materials);
    }

    public GuiMapBackground(MapZoneBase[] zones, int[] xCoords, int[] yCoords, Material[] materials) {
        this.zones = zones;
        this.xCoords = xCoords;
        this.yCoords = yCoords;
        this.materials = materials;
    }

    public static void loadBackgroundMap(String mapName, MapZoneBase[] zones, int[] xCoords, int[] yCoords, Material[] materials) {
        System.out.println(Main.worldLoadingSystemLabel + "Started loading background world: " + mapName);

        try(var input = new ObjectInputStream(Files.newInputStream(Path.of(Main.executionDir + mapName)))){
            var zoneIDCache = (String[]) input.readObject();
            var textureCache = (String[]) input.readObject();

            input.readUTF(); //Sky texture
            input.readShort(); input.readShort();  //Width height felesleges, 14x10 az összes
            input.readUTF(); //Next map

            var materialRegistry = Material.materialRegistry;
            for(int y = 0, zoneIndex = 0; y < 640; y += 64) {
                for(int x = 0; x < 896; x += 64) {
                    var zone = MapZoneBase.getZoneFromName(zoneIDCache[input.readByte()]);

                    if(zone instanceof IInternalZone) {
                        zone = ((IInternalZone) zone).getReplacementZone();
                    }

                    if(zone instanceof MapZoneTexturable) {
                        materials[zoneIndex] = materialRegistry.get(textureCache[input.readByte()]);
                    }
                    xCoords[zoneIndex] = x;
                    yCoords[zoneIndex] = y;
                    zones[zoneIndex++] = zone;
                }
            }
        }catch(IOException | ClassNotFoundException e){
            System.out.println(Main.worldLoadingSystemLabel + "Failed to load background world: " + mapName);
            e.printStackTrace();
        }

        System.out.println(Main.worldLoadingSystemLabel + "Finished loading background world: " + mapName);
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

            if(zone instanceof ITransparentZone) {
                ((ITransparentZone) zone).drawAfter(xCoordsLocal[k], yCoordsLocal[k], materialsLocal[k], graphics);
            }
        }
    }
}