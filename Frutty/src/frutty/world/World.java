package frutty.world;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Random;

import frutty.Main;
import frutty.entity.Entity;
import frutty.entity.EntityEnemy;
import frutty.entity.EntityPlayer;
import frutty.entity.zone.EntityAppleZone;
import frutty.entity.zone.EntityZone;
import frutty.gui.GuiHelper;
import frutty.plugin.event.MapInitEvent;
import frutty.world.interfaces.IInternalZone;
import frutty.world.interfaces.ITexturable;
import frutty.world.interfaces.IZoneEntityProvider;
import frutty.world.interfaces.MapZoneBase;

public final class World{
	public static EntityPlayer[] players;
	public static MapZoneBase[] zones;
	public static ArrayList<Entity> entities = new ArrayList<>(10);
	public static ArrayList<Particle> particles = new ArrayList<>(10);
	public static EntityEnemy[] enemies;
	public static int width, height, pickCount, score, ticks;
	public static int[] xCoords, yCoords, textureData;
	public static EntityZone[] zoneEntities;
	public static String skyTextureName, mapName, nextMap;
	public static String[] textures;
	
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
		textureData = null;
		textures = null;
	}
	
	private static void init(String[] txts, boolean isMultiplayer, String skyName, String levelName, int w, int h, String next) {
		mapName = levelName;
		nextMap = next;
		
		if(Main.mapLoadEvents != null) Main.handleEvent(new MapInitEvent(w, h, txts, entities), Main.mapLoadEvents);
		
		int zoneCount = (w / 64) * (h / 64);
		zones = new MapZoneBase[zoneCount];
		width = w - 64;
		height = h - 64;
		
		xCoords = new int[zoneCount];
		yCoords = new int[zoneCount];
		textureData = new int[zoneCount];
		zoneEntities = new EntityZone[zoneCount];
		
		if(isMultiplayer) {
			players = new EntityPlayer[2];
		}else {
			players = new EntityPlayer[1];
		}
		
		Main.loadTextures(textures = txts);
		Main.loadSkyTexture(skyTextureName = skyName);
		Particle.precacheParticles();
	}
	
	public static void generateMap(int genWidth, int genHeight, boolean isMultiplayer) {
		Random rand = Main.rand;
		int bigWidth = genWidth * 64, bigHeight = genHeight * 64, zoneIndex = 0;
		
		init(new String[] {"normal"}, isMultiplayer, null, "generated: " + genWidth + "x" + genHeight, bigWidth, bigHeight, null);
		
		for(int y = 0; y < bigHeight; y += 64) {
			for(int x = 0, rng = rand.nextInt(10); x < bigWidth; x += 64, rng = rand.nextInt(10)) {
				xCoords[zoneIndex] = x;
				yCoords[zoneIndex] = y;
				
				if(rng < 6) {
					zones[zoneIndex++] = Main.normalZone;
				}else if(rng >= 6 && rng < 9) {
					zones[zoneIndex++] = Main.emptyZone;
				}else if(rng == 9) {
					if(rand.nextBoolean()) {   //isApple
						zoneEntities[zoneIndex] = new EntityAppleZone(x, y, zoneIndex);
						zones[zoneIndex++] = Main.appleZone;
					}else {
						zones[zoneIndex++] = Main.cherryZone;
						++pickCount;
					}
				}
			}
		}
		
		outerLoop:
		for(int randIndex = rand.nextInt(zoneIndex), loopState = 0; ; randIndex = rand.nextInt(zoneIndex)) {
			if(zones[randIndex] == Main.emptyZone) {
				if(loopState == 0) {
					zones[randIndex] = Main.spawnerZone;
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
		try(var input = new ObjectInputStream(new FileInputStream("./maps/" + name + ".deg"))){
			int width, height, zoneIndex = 0;
			int textureCount = input.readByte();
			String[] textures = new String[textureCount];
			
			for(int k = 0; k < textureCount; ++k) {
				textures[k] = input.readUTF();
			}
			
			int zoneIDCount = input.readByte();
			String[] zoneIDS = new String[zoneIDCount];
			
			for(int k = 0; k < zoneIDCount; ++k) {
				zoneIDS[k] = input.readUTF();
			}
			
			init(textures, isMultiplayer, input.readUTF(), name, width = input.readShort() * 64, height = input.readShort() * 64, input.readUTF());
			
			for(int y = 0; y < height; y += 64) {
				for(int x = 0; x < width; x += 64) {
					MapZoneBase zone = Main.getZoneFromName(zoneIDS[input.readByte()]);
					
					zone.onZoneAdded(isMultiplayer, x, y);  //Fentre így a player zónák jól müködnek majd elméletileg
					if(zone instanceof IInternalZone) {
						zone = ((IInternalZone) zone).getReplacementZone();
					}
					
					if(zone instanceof ITexturable) {
						textureData[zoneIndex] = input.readByte();
					}
					
					xCoords[zoneIndex] = x;
					yCoords[zoneIndex] = y;
					
					if(zone instanceof IZoneEntityProvider) {
						zoneEntities[zoneIndex] = ((IZoneEntityProvider) zone).getZoneEntity(x, y, zoneIndex);
					}
					zones[zoneIndex++] = zone;
				}
			}
			
		}catch(IOException e){
			System.err.println("Can't load invalid map: " + "./maps/" + name + ".deg");
		}
		
		GuiHelper.mapSizeCheck(width / 64 + 1, height / 64 + 1);
	}
	
	public static void createSave(String fileName) {
		if(fileName != null) {
			/*try(var output = new ObjectOutputStream(new FileOutputStream("./saves/" + fileName + ".sav"))){
				output.writeShort(zones.length);
				
				var zoneIDS = new HashMap<Integer, String>();
				
				var entries = Main.zoneRegistry.entrySet();
				for(MapZoneBase zone : zones) {
					for(var entry : entries) {
						if(entry.getValue() == zone && !zoneIDS.containsValue(entry.getKey())) {
							zoneIDS.put(zoneIDS.size(), entry.getKey());
							break;
						}
					}
				}
				output.writeByte(zoneIDS.size());
				
				for(int k = 0; k < zoneIDS.size(); ++k) {
					output.writeUTF(zoneIDS.get(k));
				}
				
				for(MapZoneBase zone : zones) {
					String zoneName = Main.getZoneName(zone);
					
					for(var meh : zoneIDS.entrySet()) {
						if(meh.getValue() == zoneName) {
							output.writeByte(meh.getKey());
						}
					}
				}
				
				output.writeObject(players);
				output.writeObject(entities);
				output.writeObject(particles);
				output.writeObject(enemies);
				output.writeInt(width);
				output.writeInt(height);
				output.writeInt(pickCount);
				output.writeInt(score);
				output.writeInt(ticks);
				output.writeObject(xCoords);
				output.writeObject(yCoords);
				output.writeObject(textureData);
				output.writeObject(zoneEntities);
				output.writeUTF(skyTextureName);
				output.writeObject(textures);
			} catch (IOException e) {
				e.printStackTrace();
			}
			*/
		}
	}
	
	public static boolean loadSave(String fileName) {
		if(fileName != null) {
			try(var input = new ObjectInputStream(new FileInputStream("./saves/" + fileName))){
				zones = new MapZoneBase[input.readShort()];
				String[] zoneIDs = new String[input.readByte()];
				
				for(int k = 0; k < zoneIDs.length; ++k) {
					zoneIDs[k] = input.readUTF();
				}
				
				for(int k = 0; k < zones.length; ++k) {
					zones[k] = Main.getZoneFromName(zoneIDs[input.readByte()]);
				}
				
				players = (EntityPlayer[]) input.readObject();
				entities = (ArrayList<Entity>) input.readObject();
				particles = (ArrayList<Particle>) input.readObject();
				enemies = (EntityEnemy[]) input.readObject();
				width = input.readInt();
				height = input.readInt();
				pickCount = input.readInt();
				score = input.readInt();
				ticks = input.readInt();
				xCoords = (int[]) input.readObject();
				yCoords = (int[]) input.readObject();
				textureData = (int[]) input.readObject();
				zoneEntities = (EntityZone[]) input.readObject();
				Main.loadSkyTexture(skyTextureName = input.readUTF());
				Main.loadTextures(textures = (String[]) input.readObject());
				
				Particle.precacheParticles();
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
		zones[index] = Main.emptyZone;
	}
}