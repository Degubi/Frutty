package frutty.map;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

import frutty.Main;
import frutty.entity.Entity;
import frutty.entity.EntityBall;
import frutty.entity.EntityEnemy;
import frutty.entity.EntityPlayer;
import frutty.entity.zone.EntityZone;
import frutty.gui.GuiHelper;
import frutty.gui.GuiIngame;
import frutty.gui.GuiSettings.Settings;
import frutty.map.base.MapZone;
import frutty.map.interfaces.ITexturable;

public final class Map{
	public static EntityPlayer[] players;
	public static MapZone[] zones;
	public static ArrayList<Entity> entities = new ArrayList<>();
	public static ArrayList<Particle> particles = new ArrayList<>();
	public static EntityEnemy[] enemies;
	public static int width, height, pickCount, score, ticks;
	public static int[] xCoords, yCoords, textureData;
	public static EntityZone[] zoneEntities;
	public static String skyTextureName;
	public static String[] textures;
	
	private static void init(String[] txts, String skyName, boolean isMulti, int w, int h, int p1X, int p1Y, int p2X, int p2Y) {
		entities.clear();
		particles.clear();
		pickCount = 0;
		score = 0;
		ticks = 0;
		
		int zoneCount = (w / 64) * (h / 64);
		zones = new MapZone[zoneCount];
		width = w - 64;
		height = h - 64;
		loadTextures(textures = txts);
		loadSkyTexture(skyTextureName = skyName);
		
		xCoords = new int[zoneCount];
		yCoords = new int[zoneCount];
		textureData = new int[zoneCount];
		zoneEntities = new EntityZone[zoneCount];
		
		int enemyCount = 0;
		if(!Settings.disableEnemies) {
			if(Settings.difficulty == 0) {
				enemyCount += zoneCount < 70 ? 1 : zoneCount / 70;
			}else if(Settings.difficulty == 1) {
				enemyCount += zoneCount / 50;
			}else{
				enemyCount += zoneCount / 30;
			}
		}
		
		enemies = new EntityEnemy[enemyCount];
		
		if(isMulti) {
			players = new EntityPlayer[]{new EntityPlayer(p1X, p1Y, true), new EntityPlayer(p2X, p2Y, false)};
		}else{
			players = new EntityPlayer[]{new EntityPlayer(p1X, p1Y, true)};
		}
		entities.add(new EntityBall());
		
		Particle.precacheParticles();
	}
	
	public static void loadTextures(String[] textureNames) {
		GuiIngame.textures = new BufferedImage[textureNames.length];
		try{
			for(int k = 0; k < textureNames.length; ++k) {
				GuiIngame.textures[k] = ImageIO.read(new File("./textures/map/" + textureNames[k] + ".png"));
			}
		}catch (IOException e) {
		}
	}
	
	public static void loadSkyTexture(String textureName) {
		try{
			if(!textureName.equals("null")) {
				GuiIngame.skyTexture = ImageIO.read(new File("./textures/map/skybox/" + textureName + ".png"));
			}
		}catch (IOException e) {
			System.err.println("Can't find sky texture: " + textureName);
		}
	}

	public static void generateMap(int width, int height, boolean isMultiplayer) {
		Random rand = Main.rand;
		int bigWidth = width * 64, bigHeight = height * 64, zoneIndex = 0;
		
		init(new String[] {"normal"}, "null", isMultiplayer, bigWidth, bigHeight, 0, 0, 0, 0);
		
		for(int x = 0; x < bigWidth; x += 64) {
			for(int y = 0, rng = rand.nextInt(10); y < bigHeight; y += 64, rng = rand.nextInt(10)) {
				xCoords[zoneIndex] = x;
				yCoords[zoneIndex] = y;
				
				if(rng < 6) {
					zones[zoneIndex++] = Main.normalZone;
				}else if(rng >= 6 && rng < 9) {
					zones[zoneIndex++] = Main.emptyZone;
				}else if(rng == 9) {
					if(rand.nextBoolean()) {   //isApple
						zones[zoneIndex++] = Main.appleZone;
					}else {
						zones[zoneIndex++] = Main.cherryZone;
						++pickCount;
					}
				}
			}
		}
		
		int loopState = 0;
		
		outerLoop:
		for(int randIndex = rand.nextInt(zoneIndex); ; randIndex = rand.nextInt(zoneIndex)) {
			if(zones[randIndex] == Main.emptyZone) {
				if(loopState == 0) {
					zones[randIndex] = Main.spawnerZone;
					loopState = 1;
					
					//TODO tiszt�t�st itt megcsin�lni �jra
					
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
		
		GuiHelper.mapSizeCheck(width / 64 + 1, height / 64 + 1);
	}
	
	public static void loadMap(String name, boolean isMultiplayer) {
		try(ObjectInputStream input = new ObjectInputStream(new FileInputStream("./maps/" + name + ".deg"))){
			int width, height, zoneIndex = 0;
			int textureCount = input.readByte();
			String[] textures = new String[textureCount];
			
			for(int k = 0; k < textureCount; ++k) {
				textures[k] = input.readUTF();
			}
			
			init(textures, input.readUTF(), isMultiplayer, width = input.readShort() * 64, height = input.readShort() * 64, input.readShort(), input.readShort(), input.readShort(), input.readShort());
			
			for(int y = 0; y < height; y += 64) {
				for(int x = 0; x < width; x += 64) {
					MapZone zone = Main.handleMapReading(input.readByte());
					zone.onZoneAdded(false, x, y);
					
					if(zone instanceof ITexturable) {
						textureData[zoneIndex] = input.readByte();
					}
					
					xCoords[zoneIndex] = x;
					yCoords[zoneIndex] = y;
					
					if(zone.hasZoneEntity()) {
						zoneEntities[zoneIndex] = zone.getZoneEntity(x, y, zoneIndex);
					}
					zones[zoneIndex++] = zone;
				}
			}
		}catch(IOException e){
			System.err.println("Can't load invalid map: " + name);
		}
		
		GuiHelper.mapSizeCheck(width / 64 + 1, height / 64 + 1);
	}
	
	public static void loadBackground(MapZone[] backZones, int[] backXCoords, int[] backYCoords, int[] backTextureData) {
		try(ObjectInputStream input = new ObjectInputStream(new FileInputStream("./maps/background" + Main.rand.nextInt(4) + ".deg"))){
			String[] textures = new String[input.readByte()];
			
			for(int k = 0; k < textures.length; ++k) {
				textures[k] = input.readUTF();
			}
			
			loadTextures(textures);
			loadSkyTexture(input.readUTF());
			
			input.readShort(); input.readShort();
			
			int zoneIndex = 0;
			
			for(int y = 0; y < 640; y += 64) {
				for(int x = 0; x < 896; x += 64) {
					MapZone zone = Main.handleMapReading(input.readByte());
					if(zone instanceof ITexturable) {
						backTextureData[zoneIndex] = input.readByte();
					}
					backXCoords[zoneIndex] = x;
					backYCoords[zoneIndex] = y;
					backZones[zoneIndex++] = zone;
				}
			}
		}catch(IOException e){
		}
	}
	
	public static String loadMapSize(String fileName) {
		try(ObjectInputStream input = new ObjectInputStream(new FileInputStream("./maps/" + fileName + ".deg"))){
			int textureCount = input.readByte();
			for(int k = 0; k < textureCount; ++k) {
				input.readUTF();
			}
			input.readUTF();
			return input.readShort() + "x" + input.readShort();
		} catch (IOException e) {
			System.err.println("Can't load map size for menu: " + fileName + ".deg");
		}
		return "";
	}
	
	public static void createSave(String fileName) {
		if(fileName != null) {
			try(ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream("./saves/" + fileName + ".sav"))){
				output.writeObject(players);
				
				output.writeInt(zones.length);
				for(MapZone zone : zones) {
					output.writeByte(zone.zoneID);
				}
				
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
		}
	}
	
	public static boolean loadSave(String fileName) {
		if(fileName != null) {
			try(ObjectInputStream input = new ObjectInputStream(new FileInputStream("./saves/" + fileName))){
				players = (EntityPlayer[]) input.readObject();
				
				int count = input.readInt();
				zones = new MapZone[count];
				
				for(int k = 0; k < count; ++k) {
					zones[k] = Main.zoneRegistry.get((int)input.readByte());
				}
				
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
				loadSkyTexture(skyTextureName = input.readUTF());
				loadTextures(textures = (String[]) input.readObject());
				
				Particle.precacheParticles();
				return true;
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public static MapZone getZoneAtPos(int x, int y) {
		for(int k = 0; k < zones.length; ++k) {
			if(xCoords[k] == x && yCoords[k] == y) {
				return zones[k];
			}
		}
		return null;
	}
	
	public static MapZone getZoneAtIndex(int index) {
		if(index < 0 || index > zones.length - 1) {
			return null;
		}
		return zones[index];
	}
	
	public static EntityEnemy getEnemyPredictedAtPos(int x, int y, EntityBall entity) {
		for(EntityEnemy enemy : enemies) {
			if((enemy.renderPosY == y && enemy.renderPosX == x) || (enemy.renderPosY == y - entity.motionY && enemy.renderPosX == x - entity.motionX)) {
				return enemy;
			}
		}
		return null;
	}
	
	public static EntityZone getZoneEntity(int ID) {
		return zoneEntities[ID];
	}
	
	public static void setZoneEmptyAt(int index) {
		zones[index] = Main.emptyZone;
	}
}