package frutty.world;

import frutty.*;
import frutty.entity.zone.*;
import frutty.tools.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

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

    private WorldData(int width, int height, int[] xCoords, int[] yCoords, WorldZone[] zones, Material[] materials,
                     EntityZone[] zoneEntities, String skyTextureName, String nextWorldName) {

        this.width = width;
        this.height = height;
        this.xCoords = xCoords;
        this.yCoords = yCoords;
        this.zones = zones;
        this.materials = materials;
        this.zoneEntities = zoneEntities;
        this.skyTextureName = skyTextureName;
        this.nextWorldName = nextWorldName;
    }



    public static WorldData load(String name, boolean isCoop, boolean initializeZones) {
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
                    var readZone = WorldZone.getZoneFromName(zoneIDCache[input.readByte()]);

                    if(initializeZones) {
                        readZone.onZoneAddedInternal(isCoop, zoneCount, x, y);
                    }

                    var finalZone = readZone instanceof InternalZone internalZone ? internalZone.getReplacementZone() : readZone;

                    if(finalZone instanceof WorldZoneTexturable) {
                        materials[zoneIndex] = materialRegistry.get(textureCache[input.readByte()]);
                    }

                    xCoords[zoneIndex] = x;
                    yCoords[zoneIndex] = y;

                    if(finalZone instanceof ZoneEntityProvider entityProviderZone) {
                        zoneEntities[zoneIndex] = entityProviderZone.getZoneEntity();
                    }

                    zones[zoneIndex++] = finalZone;
                }
            }

            System.out.println(Main.worldLoadingSystemLabel + "Finished loading world: " + name);

            return new WorldData(bigWorldWidth, bigWorldHeight, xCoords, yCoords, zones, materials, zoneEntities, skyTextureName, nextWorldName);
        }catch(IOException | ClassNotFoundException e) {
            System.out.println(Main.worldLoadingSystemLabel + "Can't load corrupt or missing world file: " + GamePaths.WORLDS_DIR + name + GamePaths.WORLD_FILE_EXTENSION);

            throw new RuntimeException(e);
        }
    }

    public static WorldData generate(int width, int height, boolean isCoop, long worldSeed, double noiseResolution, boolean initializeZones) {
        System.out.println(Main.worldLoadingSystemLabel + "Started generating world: " + worldSeed);

        var zoneCount = width * height;
        var zones = new WorldZone[zoneCount];
        var xCoords = new int[zoneCount];
        var yCoords = new int[zoneCount];
        var materials = new Material[zoneCount];
        var zoneEntities = new EntityZone[zoneCount];

        var zoneIndex = 0;
        var rand = new Random(worldSeed);
        var permutation = PerlinNoise.generatePermutation(rand);

        for(var y = 0; y < height; ++y) {
            for(var x = 0; x < width; ++x) {
                var noise = PerlinNoise.generateNoise(x * noiseResolution, y * noiseResolution, permutation);

                if(noise < 0.45) {
                    zones[zoneIndex] = WorldZone.emptyZone;
                }else{
                    var specialZoneRng = rand.nextInt(25);

                    zones[zoneIndex] = specialZoneRng == 7 ? WorldZone.appleZone : specialZoneRng == 9 ? WorldZone.cherryZone : WorldZone.normalZone;
                    materials[zoneIndex] = noise > 0.6 ? Material.STONE : Material.DIRT;
                }

                xCoords[zoneIndex] = x * 64;
                yCoords[zoneIndex] = y * 64;

                ++zoneIndex;
            }
        }

        zones[findEmptyZone(rand, zones, zoneCount)] = WorldZone.spawnerZone;
        zones[findEmptyZone(rand, zones, zoneCount)] = WorldZone.player1Zone;

        if(isCoop) {
            zones[findEmptyZone(rand, zones, zoneCount)] = WorldZone.player2Zone;
        }

        for(var i = 0; i < zoneCount; ++i) {
            var originalZone = zones[i];

            if(initializeZones) {
                originalZone.onZoneAddedInternal(isCoop, zoneCount, xCoords[i], yCoords[i]);
            }

            var finalZone = originalZone instanceof InternalZone internalZone ? internalZone.getReplacementZone() : originalZone;

            if(finalZone instanceof ZoneEntityProvider providerZone) {
                zoneEntities[i] = providerZone.getZoneEntity();
            }

            zones[i] = finalZone;
        }

        System.out.println(Main.worldLoadingSystemLabel + "Finished generating world: " + worldSeed);

        return new WorldData(width * 64, height * 64, xCoords, yCoords, zones, materials, zoneEntities, "null", null);
    }

    private static int findEmptyZone(Random rand, WorldZone[] zones, int zoneCount) {
        for(var randIndex = rand.nextInt(zoneCount); ; randIndex = rand.nextInt(zoneCount)) {
            if(zones[randIndex] == WorldZone.emptyZone) {
                return randIndex;
            }
        }
    }
}