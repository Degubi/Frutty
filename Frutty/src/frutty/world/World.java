package frutty.world;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;

import frutty.FruttyMain;
import frutty.entity.Entity;
import frutty.entity.EntityEnemy;
import frutty.entity.EntityPlayer;
import frutty.entity.zone.EntityAppleZone;
import frutty.entity.zone.EntityZone;
import frutty.plugin.event.world.WorldInitEvent;
import frutty.plugin.internal.EventHandle;
import frutty.tools.GuiHelper;
import frutty.tools.Material;
import frutty.world.base.IInternalZone;
import frutty.world.base.IZoneEntityProvider;
import frutty.world.base.MapZoneBase;
import frutty.world.base.MapZoneTexturable;

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
	
	private static void init(String[] txts, boolean isMultiplayer, String skyName, String levelName, int w, int h, String next) {
		mapName = levelName;
		nextMap = next;
		
		if(FruttyMain.hasPlugins && !EventHandle.mapLoadEvents.isEmpty()) EventHandle.handleEvent(new WorldInitEvent(w, h, txts, entities), EventHandle.mapLoadEvents);
		
		int zoneCount = (w / 64) * (h / 64);
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
		
		FruttyMain.loadTextures(textures = txts);
		FruttyMain.loadSkyTexture(skyTextureName = skyName);
	}
	
	public static void generateMap(int genWidth, int genHeight, boolean isMultiplayer) {
		Random rand = FruttyMain.rand;
		int bigWidth = genWidth * 64, bigHeight = genHeight * 64, zoneIndex = 0;
		
		init(new String[] {"normal"}, isMultiplayer, null, "generated: " + genWidth + "x" + genHeight, bigWidth, bigHeight, null);
		
		for(int y = 0; y < bigHeight; y += 64) {
			for(int x = 0, rng = rand.nextInt(10); x < bigWidth; x += 64, rng = rand.nextInt(10)) {
				xCoords[zoneIndex] = x;
				yCoords[zoneIndex] = y;
				
				if(rng < 6) {
					zones[zoneIndex++] = FruttyMain.normalZone;
				}else if(rng >= 6 && rng < 9) {
					zones[zoneIndex++] = FruttyMain.emptyZone;
				}else if(rng == 9) {
					if(rand.nextBoolean()) {   //isApple
						zoneEntities[zoneIndex] = new EntityAppleZone(x, y, zoneIndex);
						zones[zoneIndex++] = FruttyMain.appleZone;
					}else {
						zones[zoneIndex++] = FruttyMain.cherryZone;
						++pickCount;
					}
				}
			}
		}
		
		outerLoop:
		for(int randIndex = rand.nextInt(zoneIndex), loopState = 0; ; randIndex = rand.nextInt(zoneIndex)) {
			if(zones[randIndex] == FruttyMain.emptyZone) {
				if(loopState == 0) {
					zones[randIndex] = FruttyMain.spawnerZone;
					loopState = 1;
					
					//TODO tisztítást itt megcsinálni újra
					
					/*for(int yClear = 0; yClear < bigHeight; yClear += 64) {
						zones[Entity.coordsToIndex(xCoords[randIndex], yClear)] = Main.emptyZone;
						/*MapZone toSet = Map.getZoneAtPos(zone.posX, yClear);
							if(toSet != null && yClear != zone.posY) {
								Map.setZoneEmptyAt(toSet.zoneIndex);
						}*/
					//}
					
					/*for(int xClear = 0; xClear < bigWidth; xClear += 64) {
						zones[Entity.coordsToIndex(xClear, yCoords[randIndex])] = Main.emptyZone;
						/*MapZone toSet = Map.getZoneAtPos(xClear, zone.posY);
							if(toSet != null && xClear != zone.posX) {
								Map.setZoneEmptyAt(toSet.zoneIndex);
						}*/
					//}
					for(int l = 0; l < enemies.length; ++l) {
						enemies[l] = new EntityEnemy(xCoords[randIndex], yCoords[randIndex]);
					}
					
					continue outerLoop;
				}else if(loopState == 1) {
					players[0].renderPosX = xCoords[randIndex];
					players[0].renderPosY = yCoords[randIndex];
					players[0].serverPosX = xCoords[randIndex];
					players[0].serverPosY = yCoords[randIndex];
					
					loopState = 2;
					
					if(isMultiplayer) continue outerLoop;
					break outerLoop;
				}else{
					players[1].renderPosX = xCoords[randIndex];
					players[1].renderPosY = yCoords[randIndex];
					players[1].serverPosX = xCoords[randIndex];
					players[1].serverPosY = yCoords[randIndex];
					break outerLoop;
				}
			}
		}
		
		GuiHelper.mapSizeCheck(genWidth / 64 + 1, genHeight / 64 + 1);
	}
	
	public static void loadMap(String name, boolean isMultiplayer) {
		try(var input = new ObjectInputStream(new FileInputStream("./maps/" + name + ".fmap"))){
			int loadedWidth, loadedHeight, zoneIndex = 0;
			
			String[] zoneIDCache = (String[]) input.readObject();
			String[] textureCache = (String[]) input.readObject();
			
			init(textureCache, isMultiplayer, input.readUTF(), name, loadedWidth = input.readShort() * 64, loadedHeight = input.readShort() * 64, input.readUTF());
			
			for(int y = 0; y < loadedHeight; y += 64) {
				for(int x = 0; x < loadedWidth; x += 64) {
					MapZoneBase zone = FruttyMain.getZoneFromName(zoneIDCache[input.readByte()]);
					
					zone.onZoneAddedInternal(isMultiplayer, x, y);  //Fentre így a player zónák jól müködnek majd elméletileg
					if(zone instanceof IInternalZone) {
						zone = ((IInternalZone) zone).getReplacementZone();
					}
					
					if(zone instanceof MapZoneTexturable) {
						materials[zoneIndex] = Material.materialRegistry.get(textureCache[input.readByte()]);
					}
					
					xCoords[zoneIndex] = x;
					yCoords[zoneIndex] = y;
					
					if(zone instanceof IZoneEntityProvider) {
						zoneEntities[zoneIndex] = ((IZoneEntityProvider) zone).getZoneEntity(x, y, zoneIndex);
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
			try(var output = new ObjectOutputStream(new FileOutputStream("./saves/" + fileName + ".sav"))){
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
				output.writeObject(materials);
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
			try(var input = new ObjectInputStream(new FileInputStream("./saves/" + fileName))){
				players = (EntityPlayer[]) input.readObject();
				
				zones = new MapZoneBase[input.readShort()];
				for(int k = 0; k < zones.length; ++k) {
					zones[k] = FruttyMain.getZoneFromName(input.readUTF());
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
				materials = (Material[]) input.readObject();
				zoneEntities = (EntityZone[]) input.readObject();
				FruttyMain.loadSkyTexture(skyTextureName = input.readUTF());
				mapName = input.readUTF();
				nextMap = input.readUTF();
				FruttyMain.loadTextures(textures = (String[]) input.readObject());
				return true;
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public static MapZoneBase getZoneAtPos(int x, int y) {
		for(int k = 0; k < zones.length; ++k) {
			if(xCoords[k] == x && yCoords[k] == y) {
				return zones[k];
			}
		}
		return null;
	}
	
	public static MapZoneBase getZoneAtIndex(int index) {
		if(index < 0 || index > zones.length - 1) {
			return null;
		}
		return zones[index];
	}
	
	public static EntityZone getZoneEntity(int ID) {
		return zoneEntities[ID];
	}
	
	public static void setZoneEmptyAt(int index) {
		zones[index] = FruttyMain.emptyZone;
		zoneEntities[index] = null;
	}
}