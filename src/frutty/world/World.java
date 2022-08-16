package frutty.world;

import frutty.*;
import frutty.entity.*;
import frutty.entity.living.*;
import frutty.entity.zone.*;
import frutty.plugin.event.world.*;
import frutty.tools.*;
import frutty.world.zones.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class World {
    public static EntityPlayer[] players;
    public static WorldZone[] zones;
    public static ArrayList<EntityBase> entities = new ArrayList<>(10);
    public static ArrayList<Particle> particles = new ArrayList<>(20);
    public static EntityEnemy[] enemies;
    public static int width, height, pickCount, score, ticks;
    public static int[] xCoords, yCoords;
    public static EntityZone[] zoneEntities;
    public static String skyTextureName, name, nextWorldName;
    public static Material[] materials;
    public static boolean[] isActivePathfindingZone;

    public static void cleanUp() {
        entities.clear();
        particles.clear();

        pickCount = 0;
        score = 0;
        ticks = 0;
        players = null;
        zones = null;
        enemies = null;
        zoneEntities = null;
        xCoords = null;
        yCoords = null;
        materials = null;
        isActivePathfindingZone = null;
    }

    public static void load(String name, boolean isCoop) {
        World.name = name;
        World.players = new EntityPlayer[isCoop ? 2 : 1];

        var worldData = WorldData.load(name, isCoop, true);

        World.width = worldData.width - 64;
        World.height = worldData.height - 64;
        World.xCoords = worldData.xCoords;
        World.yCoords = worldData.yCoords;
        World.zones = worldData.zones;
        World.materials = worldData.materials;
        World.zoneEntities = worldData.zoneEntities;
        World.isActivePathfindingZone = new boolean[worldData.zones.length];
        World.skyTextureName = worldData.skyTextureName;
        World.nextWorldName = worldData.nextWorldName;

        WorldZoneSky.loadSkyTexture(worldData.skyTextureName);

        if(Main.worldInitEvents.length > 0) Main.invokeEvent(new WorldInitEvent(worldData.width / 64, worldData.height / 64, entities), Main.worldInitEvents);
    }

    public static void generate(int width, int height, boolean isCoop, long worldSeed, double noiseResolution) {
        World.name = "generated: " + width + "x" + height;
        World.players = new EntityPlayer[isCoop ? 2 : 1];

        var worldData = WorldData.generate(width, height, isCoop, worldSeed, noiseResolution, true);

        World.width = worldData.width - 64;
        World.height = worldData.height - 64;
        World.xCoords = worldData.xCoords;
        World.yCoords = worldData.yCoords;
        World.zones = worldData.zones;
        World.materials = worldData.materials;
        World.zoneEntities = worldData.zoneEntities;
        World.isActivePathfindingZone = new boolean[worldData.zones.length];
        World.skyTextureName = worldData.skyTextureName;
        World.nextWorldName = worldData.nextWorldName;

        WorldZoneSky.loadSkyTexture(worldData.skyTextureName);

        if(Main.worldInitEvents.length > 0) Main.invokeEvent(new WorldInitEvent(worldData.width / 64, worldData.height / 64, entities), Main.worldInitEvents);
    }

    public static void createSave(String fileName) {
        System.out.println(Main.ioSystemLabel + "Creating save file: " + fileName);

        if(fileName != null) {
            try(var output = new ObjectOutputStream(Files.newOutputStream(Path.of(GamePaths.SAVES_DIR + fileName + ".sav")))){
                output.writeObject(players);

                output.writeShort(zones.length);
                for(var k = 0; k < zones.length; ++k) {
                    output.writeUTF(zones[k].zoneName);
                }

                output.writeObject(entities);
                output.writeObject(particles);
                output.writeObject(enemies);
                output.writeShort(width);
                output.writeShort(height);
                output.writeByte(pickCount);
                output.writeShort(score);
                output.writeInt(ticks);
                output.writeObject(xCoords);
                output.writeObject(yCoords);

                var materialCount = materials.length;
                output.writeInt(materials.length);

                for(var x = 0; x < materialCount; ++x) {
                    var currentMat = materials[x];
                    output.writeUTF(currentMat == null ? "null" : currentMat.name);
                }

                output.writeObject(zoneEntities);
                output.writeUTF(skyTextureName);
                output.writeUTF(name);
                output.writeUTF(nextWorldName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static boolean loadSave(String fileName) {
        if(fileName != null) {
            System.out.println(Main.ioSystemLabel + "Loading save file: " + fileName);

            try(var input = new ObjectInputStream(Files.newInputStream(Path.of(GamePaths.SAVES_DIR + fileName)))){
                players = (EntityPlayer[]) input.readObject();

                zones = new WorldZone[input.readShort()];
                for(var k = 0; k < zones.length; ++k) {
                    zones[k] = WorldZone.getZoneFromName(input.readUTF());
                }

                entities = (ArrayList<EntityBase>) input.readObject();
                particles = (ArrayList<Particle>) input.readObject();
                enemies = (EntityEnemy[]) input.readObject();
                width = input.readShort();
                height = input.readShort();
                pickCount = input.readByte();
                score = input.readShort();
                ticks = input.readInt();
                xCoords = (int[]) input.readObject();
                yCoords = (int[]) input.readObject();

                var materialCount = input.readInt();
                materials = new Material[materialCount];

                for(var x = 0; x < materialCount; ++x) {
                    var currentReadName = input.readUTF();
                    materials[x] = currentReadName.equals("null") ? null : Material.materialRegistry.get(currentReadName);
                }

                zoneEntities = (EntityZone[]) input.readObject();
                WorldZoneSky.loadSkyTexture(skyTextureName = input.readUTF());
                name = input.readUTF();
                nextWorldName = input.readUTF();
                return true;
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static WorldZone getZoneAt(int x, int y) {
        var index = worldCoordsToZoneIndex(x, y);

        return index < 0 || index > World.zones.length - 1 ? null : World.zones[index];
    }

    public static boolean isEmptyAt(int x, int y) {
        var zone = World.getZoneAt(x, y);

        return zone != null && zone == WorldZone.emptyZone;
    }

    public static boolean isPositionFree(int x, int y) {
        if(x < 0 || x > World.width || y < 0 || y > World.height) {
            return false;
        }

        var zone = World.getZoneAt(x, y);
        return zone != null && zone.canNPCPass(x, y);
    }

    public static void setZoneEmptyAt(int x, int y) {
        var index = worldCoordsToZoneIndex(x, y);

        zones[index] = WorldZone.emptyZone;
        zoneEntities[index] = null;
    }

    public static WorldZone getZoneAtIndex(int index) {
        return index < 0 || index > World.zones.length - 1 ? null : World.zones[index];
    }

    public static int worldCoordsToZoneIndex(int x, int y) {
        return x / 64 + (y / 64 * ((World.width + 64) / 64));
    }

    public static int getEnemyCountBasedOnDifficulty(int zoneCount) {
        if(Settings.difficulty == 0) {
            return zoneCount < 70 ? 1 : zoneCount / 70;
        }
        if(Settings.difficulty == 1) {
            return zoneCount / 50;
        }
        return zoneCount / 30;
    }


    public static void spawnFallingParticles(int count, int x, int y, Material material) {
        if(Settings.graphicsLevel == 2) {
            var rand = Main.rand;
            var color = material.particleColor;
            var particles = World.particles;

            for(var k = 0; k < count; ++k) {
                particles.add(new Particle(x + rand.nextInt(64), y + 64 + rand.nextInt(32), 0, 2 + rand.nextInt(3), color));
            }
        }
    }

    public static void spawnRandomParticles(int count, int x, int y, Color color) {
        if(Settings.graphicsLevel == 2) {
            var particles = World.particles;
            var rand = Main.rand;

            for(var k = 0; k < count; ++k) {
                particles.add(new Particle(x + 32, y + 60, -2 + rand.nextInt(5), -2 + rand.nextInt(2), color));
            }
        }
    }
}