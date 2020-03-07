package frutty.world;

import static java.nio.file.StandardOpenOption.*;

import frutty.*;
import frutty.entity.*;
import frutty.entity.zone.*;
import frutty.gui.GuiSettings.*;
import frutty.plugin.event.world.*;
import frutty.tools.*;
import frutty.world.base.*;
import frutty.world.zones.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class World{
	public static EntityPlayer[] players;
	public static MapZoneBase[] zones;
	public static ArrayList<Entity> entities = new ArrayList<>(10);
	public static ArrayList<Particle> particles = new ArrayList<>(10);
	public static EntityEnemy[] enemies;
	public static int width, height, pickCount, score, ticks;
	public static int[] xCoords, yCoords;
	public static EntityZone[] zoneEntities;
	public static String skyTextureName, mapName, nextMap;
	public static String[] textures;
	public static Material[] materials;
	
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
		textures = null;
	}
	
	private static void init(String[] textures, boolean isMultiplayer, String skyName, String levelName, int w, int h, String next) {
		mapName = levelName;
		nextMap = next;
		
		if(Main.worldInitEvents.length > 0) Main.invokeEvent(new WorldInitEvent(w, h, textures, entities), Main.worldInitEvents);
		
		var zoneCount = (w / 64) * (h / 64);
		zones = new MapZoneBase[zoneCount];
		width = w - 64;
		height = h - 64;
		
		xCoords = new int[zoneCount];
		yCoords = new int[zoneCount];
		materials = new Material[zoneCount];
		zoneEntities = new EntityZone[zoneCount];
		
		if(isMultiplayer) {
			players = new EntityPlayer[2];
		}else {
			players = new EntityPlayer[1];
		}
		
		MapZoneSky.loadSkyTexture(skyTextureName = skyName);
	}
	
	public static void generateMap(int genWidth, int genHeight, boolean isMultiplayer) {
		var rand = Main.rand;
		int bigWidth = genWidth * 64, bigHeight = genHeight * 64, zoneIndex = 0;
		
		init(new String[] {"normal"}, isMultiplayer, "null", "generated: " + genWidth + "x" + genHeight, bigWidth, bigHeight, null);
		
		for(int y = 0; y < bigHeight; y += 64) {
			for(int x = 0, rng = rand.nextInt(10); x < bigWidth; x += 64, rng = rand.nextInt(10)) {
				xCoords[zoneIndex] = x;
				yCoords[zoneIndex] = y;
				
				if(rng < 6) {
					zones[zoneIndex] = MapZoneBase.normalZone;
					materials[zoneIndex++] = Material.NORMAL;
				}else if(rng >= 6 && rng < 9) {
					zones[zoneIndex++] = MapZoneBase.emptyZone;
				}else if(rng == 9) {
					if(rand.nextBoolean()) {   //isApple
						zoneEntities[zoneIndex] = new EntityAppleZone();
						zones[zoneIndex] = MapZoneBase.appleZone;
					}else {
						zones[zoneIndex] = MapZoneBase.cherryZone;
						++pickCount;
					}
					
					materials[zoneIndex++] = Material.NORMAL;
				}
			}
		}
		
		enemies = new EntityEnemy[0];
		
		for(int randIndex = rand.nextInt(zoneIndex), loopState = 0; ; randIndex = rand.nextInt(zoneIndex)) {
			if(zones[randIndex] == MapZoneBase.emptyZone) {
				if(loopState == 0) {
					zones[randIndex] = MapZoneBase.spawnerZone;
					loopState = 1;
					
					for(int l = 0; l < enemies.length; ++l) {
						enemies[l] = new EntityEnemy(xCoords[randIndex], yCoords[randIndex]);
					}
					
					continue;
				}else if(loopState == 1) {
				    players[0] = new EntityPlayer(xCoords[randIndex], yCoords[randIndex], true);
					
					loopState = 2;
					
					if(isMultiplayer) continue;
					break;
				}else{
                    players[1] = new EntityPlayer(xCoords[randIndex], yCoords[randIndex], false);
					
                    break;
				}
			}
		}
		
		GuiHelper.mapSizeCheck(genWidth / 64 + 1, genHeight / 64 + 1);
	}
	
	public static void loadMap(String name, boolean isMultiplayer) {
		try(var input = new ObjectInputStream(Files.newInputStream(Path.of("./maps/" + name + ".fmap")))){
			int loadedWidth, loadedHeight, zoneIndex = 0;
			
			var zoneIDCache = (String[]) input.readObject();
			var textureCache = (String[]) input.readObject();
			
			init(textureCache, isMultiplayer, input.readUTF(), name, loadedWidth = input.readShort() * 64, loadedHeight = input.readShort() * 64, input.readUTF());
			
			var materials = World.materials;
			var xCoords = World.xCoords;
			var yCoords = World.yCoords;
			var zoneEntities = World.zoneEntities;
			var zones = World.zones;
			var materialRegistry = Material.materialRegistry;
			
			for(var y = 0; y < loadedHeight; y += 64) {
				for(var x = 0; x < loadedWidth; x += 64) {
					var zone = MapZoneBase.getZoneFromName(zoneIDCache[input.readByte()]);
					
					zone.onZoneAddedInternal(isMultiplayer, x, y);  //Fentre �gy a player z�n�k j�l m�k�dnek majd elm�letileg
					if(zone instanceof IInternalZone) {
						zone = ((IInternalZone) zone).getReplacementZone();
					}
					
					if(zone instanceof MapZoneTexturable) {
						materials[zoneIndex] = materialRegistry.get(textureCache[input.readByte()]);
					}
					
					xCoords[zoneIndex] = x;
					yCoords[zoneIndex] = y;
					
					if(zone instanceof IZoneEntityProvider) {
						zoneEntities[zoneIndex] = ((IZoneEntityProvider) zone).getZoneEntity();
					}
					zones[zoneIndex++] = zone;
				}
			}
			
		}catch(IOException | ClassNotFoundException e){
			System.err.println("Can't load invalid map: " + "./maps/" + name + ".fmap");
		}
		
		GuiHelper.mapSizeCheck(width / 64 + 1, height / 64 + 1);
	}
	
	public static void createSave(String fileName) {
		if(fileName != null) {
			try(var output = new ObjectOutputStream(Files.newOutputStream(Path.of("./saves/" + fileName + ".sav"), CREATE, WRITE, TRUNCATE_EXISTING))){
				output.writeObject(players);
				
				output.writeShort(zones.length);
				for(int k = 0; k < zones.length; ++k) {
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
				
				int materialCount = materials.length;
				output.writeInt(materials.length);
				
				for(int x = 0; x < materialCount; ++x) {
					var currentMat = materials[x];
					output.writeUTF(currentMat == null ? "null" : currentMat.name);
				}
				
				output.writeObject(zoneEntities);
				output.writeUTF(skyTextureName);
				output.writeUTF(mapName);
				output.writeUTF(nextMap);
				output.writeObject(textures);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static boolean loadSave(String fileName) {
		if(fileName != null) {
			try(var input = new ObjectInputStream(Files.newInputStream(Path.of("./saves/" + fileName)))){
				players = (EntityPlayer[]) input.readObject();
				
				zones = new MapZoneBase[input.readShort()];
				for(int k = 0; k < zones.length; ++k) {
					zones[k] = MapZoneBase.getZoneFromName(input.readUTF());
				}
				
				entities = (ArrayList<Entity>) input.readObject();
				particles = (ArrayList<Particle>) input.readObject();
				enemies = (EntityEnemy[]) input.readObject();
				width = input.readShort();
				height = input.readShort();
				pickCount = input.readByte();
				score = input.readShort();
				ticks = input.readInt();
				xCoords = (int[]) input.readObject();
				yCoords = (int[]) input.readObject();
				
				int materialCount = input.readInt();
				materials = new Material[materialCount];
				
				for(int x = 0; x < materialCount; ++x) {
					var currentReadName = input.readUTF();
					materials[x] = currentReadName.equals("null") ? null : Material.materialRegistry.get(currentReadName);
				}
				
				zoneEntities = (EntityZone[]) input.readObject();
				MapZoneSky.loadSkyTexture(skyTextureName = input.readUTF());
				mapName = input.readUTF();
				nextMap = input.readUTF();
				return true;
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public static MapZoneBase getZoneAt(int x, int y) {
	    var index = coordsToIndex(x, y);
	    
	    if(index < 0 || index > World.zones.length - 1) {
            return null;
        }
        return World.zones[index];
	}
	
	public static boolean isEmptyAt(int x, int y) {
        var zone = World.getZoneAt(x, y);
        
        return zone != null && zone == MapZoneBase.emptyZone;
    }
	
	public static boolean isPositionFree(int x, int y) {
        if(x < 0 || x > World.width || y < 0 || y > World.height) {
            return false;
        }
        
        var zone = World.getZoneAt(x, y);
        return zone != null && zone.canNPCPass(x, y);
    }
	
	public static void setZoneEmptyAt(int x, int y) {
	    var index = coordsToIndex(x, y);
	    
		zones[index] = MapZoneBase.emptyZone;
		zoneEntities[index] = null;
	}
	
	public static MapZoneBase getZoneAtIndex(int index) {
        if(index < 0 || index > World.zones.length - 1) {
            return null;
        }
        return World.zones[index];
    }
	
	public static int coordsToIndex(int x, int y) {
        return x / 64 + (y / 64 * ((World.width + 64) / 64));
    }
	
	public static int getEnemyCountBasedOnDifficulty(int zoneCount) {
        if(!Settings.disableEnemies) {
            if(Settings.difficulty == 0) {
                return zoneCount < 70 ? 1 : zoneCount / 70;
            }else if(Settings.difficulty == 1) {
                return zoneCount / 50;
            }else{
                return zoneCount / 30;
            }
        }
        
        return 0;
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