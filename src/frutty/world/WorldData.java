package frutty.world;

import frutty.*;
import frutty.entity.zone.*;
import frutty.tools.*;
import java.io.*;
import java.nio.file.*;

public final class WorldData {

    public final int width;
    public final int height;
    public final int[] xCoords;
    public final int[] yCoords;
    public final WorldZone[] zones;
    public final Material[] materials;
    public final EntityZone[] zoneEntities;
    public final String skyTextureName;
    public final String nextWorldName;

    public WorldData(String name, boolean isCoop, boolean initializeZones) {
        System.out.println(Main.worldLoadingSystemLabel + "Started loading world: " + name);

        try(var input = new ObjectInputStream(Files.newInputStream(Path.of(GamePaths.WORLDS_DIR + name + GamePaths.WORLD_FILE_EXTENSION)))) {
            var zoneIDCache = (String[]) input.readObject();
            var textureCache = (String[]) input.readObject();
            var skyTextureName = input.readUTF();
            var width = (int) input.readShort();
            var height = (int) input.readShort();
            var nextWorldName = input.readUTF();

            var zoneCount = width * height;
            var zones = new WorldZone[zoneCount];
            var xCoords = new int[zoneCount];
            var yCoords = new int[zoneCount];
            var materials = new Material[zoneCount];
            var zoneEntities = new EntityZone[zoneCount];
            var materialRegistry = Material.materialRegistry;
            var zoneIndex = 0;
            var bigWorldWidth = width * 64;
            var bigWorldHeight = height * 64;

            for(var y = 0; y < bigWorldHeight; y += 64) {
                for(var x = 0; x < bigWorldWidth; x += 64) {
                    var zone = WorldZone.getZoneFromName(zoneIDCache[input.readByte()]);

                    if(initializeZones) {
                        zone.onZoneAddedInternal(isCoop, zoneCount, x, y);
                    }

                    if(zone instanceof InternalZone) {
                        zone = ((InternalZone) zone).getReplacementZone();
                    }

                    if(zone instanceof WorldZoneTexturable) {
                        materials[zoneIndex] = materialRegistry.get(textureCache[input.readByte()]);
                    }

                    xCoords[zoneIndex] = x;
                    yCoords[zoneIndex] = y;

                    if(zone instanceof ZoneEntityProvider) {
                        zoneEntities[zoneIndex] = ((ZoneEntityProvider) zone).getZoneEntity();
                    }
                    zones[zoneIndex++] = zone;
                }
            }

            this.width = bigWorldWidth;
            this.height = bigWorldHeight;
            this.xCoords = xCoords;
            this.yCoords = yCoords;
            this.zones = zones;
            this.materials = materials;
            this.zoneEntities = zoneEntities;
            this.skyTextureName = skyTextureName;
            this.nextWorldName = nextWorldName;

            System.out.println(Main.worldLoadingSystemLabel + "Finished loading world: " + name);
        }catch(IOException | ClassNotFoundException e) {
            System.out.println(Main.worldLoadingSystemLabel + "Can't load corrupt or missing world file: " + GamePaths.WORLDS_DIR + name + GamePaths.WORLD_FILE_EXTENSION);

            throw new RuntimeException(e);
        }
    }
}