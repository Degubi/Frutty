package frutty.map;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

import frutty.Main;
import frutty.entity.Entity;
import frutty.entity.EntityBall;
import frutty.entity.EntityEnemy;
import frutty.entity.EntityPlayer;
import frutty.gui.GuiHelper;
import frutty.gui.GuiIngame;
import frutty.gui.GuiSettings.Settings;
import frutty.gui.editor.EnumEditorZone;
import frutty.map.zones.MapZoneEmpty;
import frutty.map.zones.MapZoneFruit;
import frutty.map.zones.MapZoneFruit.EnumFruit;
import frutty.map.zones.MapZoneNormal;
import frutty.map.zones.MapZoneSpawner;

public final class Map implements Serializable{
	private static final long serialVersionUID = -5083163189200818535L;
	public static Map currentMap;
	
	public final EntityPlayer[] players;
	public final MapZone[] zones;
	public final ArrayList<Entity> entities = new ArrayList<>();
	public final ArrayList<Particle> particles = new ArrayList<>();
	public final EntityEnemy[] enemies;
	public final int width, height;
	public int pickCount, score, ticks;
	public final String skyTextureName;
	public final String[] textures;
	
	private Map(String[] txts, String skyName, boolean isMulti, int w, int h, int p1X, int p1Y, int p2X, int p2Y) {
		int zoneCount = (w / 64) * (h / 64);
		zones = new MapZone[zoneCount];
		width = w - 64;
		height = h - 64;
		GuiIngame.textures = loadTextures(textures = txts);
		GuiIngame.skyTexture = loadSkyTexture(skyTextureName = skyName);
		
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
	
	public static BufferedImage[] loadTextures(String[] textureNames) {
		BufferedImage[] textures = new BufferedImage[textureNames.length];
		try{
			for(int k = 0; k < textureNames.length; ++k) {
				textures[k] = ImageIO.read(new File("./textures/map/" + textureNames[k] + ".png"));
			}
			return textures;
		}catch (IOException e) {
			return null;
		}
	}
	
	public static BufferedImage loadSkyTexture(String textureName) {
		try{
			if(!textureName.equals("null")) {
				return ImageIO.read(new File("./textures/map/skybox/" + textureName + ".png"));
			}
			return null;
		}catch (IOException e) {
			System.err.println("Can't find sky texture: " + textureName);
			return null;
		}
	}

	public static void generateMap(int width, int height, boolean isMultiplayer) {
		Random rand = Main.rand;
		int bigWidth = width * 64, bigHeight = height * 64, zoneIndex = 0;
		
		currentMap = new Map(new String[] {"normal"}, "null", isMultiplayer, bigWidth, bigHeight, 0, 0, 0, 0);
		
		for(int x = 0; x < bigWidth; x += 64) {
			for(int y = 0, rng = rand.nextInt(10); y < bigHeight; y += 64, rng = rand.nextInt(10)) {
				if(rng < 6) {
					currentMap.zones[zoneIndex] = new MapZoneNormal(x, y, zoneIndex++, 0);
				}else if(rng >= 6 && rng < 9) {
					currentMap.zones[zoneIndex] = new MapZoneEmpty(x, y, zoneIndex++);
				}else if(rng == 9) {
					if(rand.nextBoolean()) {   //isApple
						currentMap.zones[zoneIndex] = new MapZoneFruit(x, y, EnumFruit.APPLE, zoneIndex++, 0);
					}else {
						currentMap.zones[zoneIndex] = new MapZoneFruit(x, y, EnumFruit.CHERRY, zoneIndex++, 0);
						++currentMap.pickCount;
					}
				}
			}
		}
		
		int loopState = 0;
		
		outerLoop:
		for(int x = rand.nextInt(width) * 64, y = rand.nextInt(height) * 64; ;x = rand.nextInt(width) * 64, y = rand.nextInt(height) * 64) {
			for(int k = 0; k < zoneIndex; ++k) {
				MapZone zone = currentMap.zones[k];
				if(zone.posX == x && zone.posY == y && zone instanceof MapZoneEmpty) {  //Üres zóna keresés
					if(loopState == 0) {
						currentMap.zones[k] = new MapZoneSpawner(x, y, zoneIndex);
						loopState = 1;
						
						for(int yClear = 0; yClear < bigHeight; yClear += 64) {
							MapZone toSet = Map.getZoneAtPos(zone.posX, yClear);
								if(toSet != null && yClear != zone.posY) {
									Map.setZoneEmptyAt(toSet.zoneIndex);
							}
						}
						
						for(int xClear = 0; xClear < bigWidth; xClear += 64) {
							MapZone toSet = Map.getZoneAtPos(xClear, zone.posY);
								if(toSet != null && xClear != zone.posX) {
									Map.setZoneEmptyAt(toSet.zoneIndex);
							}
						}
						
						for(int l = 0; l < currentMap.enemies.length; ++l) {
							currentMap.enemies[l] = new EntityEnemy(x, y);
						}
						
						continue outerLoop;
					}else if(loopState == 1) {
						currentMap.players[0].posX = x;
						currentMap.players[0].posY = y;
						currentMap.players[0].serverPosX = x;
						currentMap.players[0].serverPosY = y;
						loopState = 2;
						
						if(isMultiplayer) continue outerLoop;
						break outerLoop;
					}else{
						currentMap.players[1].posX = x;
						currentMap.players[1].posY = y;
						currentMap.players[1].serverPosX = x;
						currentMap.players[1].serverPosY = y;
						break outerLoop;
					}
				}
			}
		}
		
		GuiHelper.mapSizeCheck(currentMap.width / 64 + 1, currentMap.height / 64 + 1);
	}
	
	public static void loadMap(String name, boolean isMultiplayer) {
		try(ObjectInputStream input = new ObjectInputStream(new FileInputStream("./maps/" + name + ".deg"))){
			int width, height, zoneIndex = 0;
			
			int textureCount = input.readByte();
			String[] textures = new String[textureCount];
			
			for(int k = 0; k < textureCount; ++k) {
				textures[k] = input.readUTF();
			}
			
			currentMap = new Map(textures, input.readUTF(), isMultiplayer, width = input.readShort() * 64, height = input.readShort() * 64, input.readShort(), input.readShort(), input.readShort(), input.readShort());
			
			for(int y = 0; y < height; y += 64) {
				for(int x = 0; x < width; x += 64) {
					int data = input.readByte();
					currentMap.zones[zoneIndex] = EnumEditorZone.getFromIndex(data).handleMapZone(zoneIndex++, x, y, false, input);
				}
			}
		}catch(IOException e){}
		
		GuiHelper.mapSizeCheck(currentMap.width / 64 + 1, currentMap.height / 64 + 1);
	}
	
	public static boolean createSave(String fileName) {
		if(fileName != null) {
			try(ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream("./saves/" + fileName + ".sav"))){
				output.writeObject(Map.currentMap);
				return true;
			} catch (IOException e) {}
		}
		return false;
	}
	
	public static MapZone[] loadBackground() {
		try(ObjectInputStream input = new ObjectInputStream(new FileInputStream("./maps/background" + Main.rand.nextInt(4) + ".deg"))){
			String[] textures = new String[input.readByte()];
			
			for(int k = 0; k < textures.length; ++k) {
				textures[k] = input.readUTF();
			}
			
			GuiIngame.textures = loadTextures(textures);
			GuiIngame.skyTexture = loadSkyTexture(input.readUTF());
			int width = input.readShort() * 64, height = input.readShort() * 64, zoneIndex = 0;
			MapZone[] zonee = new MapZone[(width / 64) * (height / 64)];
			
			for(int y = 0; y < height; y += 64) {
				for(int x = 0; x < width; x += 64) {
					zonee[zoneIndex] = EnumEditorZone.getFromIndex(input.readByte()).handleMapZone(zoneIndex++, x, y, true, input);
				}
			}
			return zonee;
		}catch(IOException e){
			return null;
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
		} catch (IOException e) {}
		return "";
	}
	
	public static boolean loadSave(String fileName) {
		if(fileName != null) {
			try(ObjectInputStream input = new ObjectInputStream(new FileInputStream("./saves/" + fileName))){
				currentMap = (Map) input.readObject();
				GuiIngame.textures = loadTextures(currentMap.textures);
				GuiIngame.skyTexture = loadSkyTexture(currentMap.skyTextureName);
				return true;
			} catch (ClassNotFoundException | IOException e) {}
		}
		return false;
	}
	
	public static MapZone getZoneAtPos(int x, int y) {
		for(MapZone zone : currentMap.zones) {
			if(zone.posX == x && zone.posY == y) {
				return zone;
			}
		}
		return null;
	}
	
	public static MapZone getZoneAtIndex(int index) {
		if(index < 0 || index > currentMap.zones.length) {
			return null;
		}
		return currentMap.zones[index];
	}
	
	public static EntityEnemy getEnemyPredictedAtPos(int x, int y, EntityBall entity) {
		for(EntityEnemy enemy : currentMap.enemies) {
			if((enemy.posY == y && enemy.posX == x) || (enemy.posY == y - entity.motionY && enemy.posX == x - entity.motionX)) {
				return enemy;
			}
		}
		return null;
	}
	
	public static EntityBall getBall() {
		return (EntityBall) currentMap.entities.get(0);
	}
	
	public static void setZoneEmptyAt(int index) {
		currentMap.zones[index] = new MapZoneEmpty(currentMap.zones[index].posX, currentMap.zones[index].posY, index);
	}
}