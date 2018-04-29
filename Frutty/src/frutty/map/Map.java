package frutty.map;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import frutty.Main;
import frutty.entity.Entity;
import frutty.entity.EntityBall;
import frutty.entity.EntityEnemy;
import frutty.entity.EntityPlayer;
import frutty.gui.GuiHelper;
import frutty.gui.GuiMenu;
import frutty.gui.GuiSettings;
import frutty.map.zones.MapZoneEmpty;
import frutty.map.zones.MapZoneFruit;
import frutty.map.zones.MapZoneNormal;
import frutty.map.zones.MapZoneSpawner;
import frutty.stuff.EnumFruit;

public class Map implements Serializable{
	private static final long serialVersionUID = -5083163189200818535L;
	public static Map currentMap;
	
	public final EntityPlayer[] players;
	public final MapZone[] zones;
	public final ArrayList<Entity> entities = new ArrayList<>(); 
	public EntityEnemy[] enemies;
	public final int width, height;
	public int pickCount, score, zoneIndex, ticks;
	public transient BufferedImage texture;
	public final String textureStr;
	
	//0: width, 1: height, 2: p1X, 3: p1Y, 4: p2X, 5: p2Y
	private Map(String textureName, boolean isMulti, int... data) {
		zones = new MapZone[(data[0] / 64) * (data[1] / 64)];
		width = data[0] - 64;
		height = data[1] - 64;
		textureStr = textureName;
		texture = loadTexture(textureName);
		
		if(isMulti) {
			players = new EntityPlayer[]{new EntityPlayer(data[2], data[3], true), new EntityPlayer(data[4], data[5], false)};
		}else{
			players = new EntityPlayer[]{new EntityPlayer(data[2], data[3], true)};
		}
		entities.add(new EntityBall());
	}
	
	private static BufferedImage loadTexture(String textureName) {
		printDebug("Started loading texture " + textureName);
		try{
			printDebug(textureName + " loaded");
			return ImageIO.read(GuiMenu.class.getResource("/textures/map/" + textureName + ".png"));
		}catch (IOException e) {
			return null;
		}
	}
	
	private static void printDebug(String msg) {
		if(GuiSettings.isDebugEnabled()) {
			System.out.println(msg);
		}
	}
	
	public static void generateMap(int width, int height, boolean isMultiplayer) {
		printDebug("Generating map...");
		Random rand = Main.rand;
		int bigWidth = width * 64, bigHeight = height * 64;
		
		currentMap = new Map("normal", isMultiplayer, bigWidth, bigHeight, 0, 0, 0, 0);
		
		for(int x = 0; x < bigWidth; x += 64) {
			for(int y = 0, rng = rand.nextInt(10); y < bigHeight; y += 64, rng = rand.nextInt(10)) {
				if(rng < 6) {
					currentMap.zones[currentMap.zoneIndex] = new MapZoneNormal(x, y, currentMap.zoneIndex++);
				}else if(rng >= 6 && rng < 9) {
					currentMap.zones[currentMap.zoneIndex] = new MapZoneEmpty(x, y, currentMap.zoneIndex++);
				}else if(rng == 9) {
					if(rand.nextBoolean()) {   //isApple
						currentMap.zones[currentMap.zoneIndex] = new MapZoneFruit(x, y, EnumFruit.APPLE, currentMap.zoneIndex++);
					}else {
						currentMap.zones[currentMap.zoneIndex] = new MapZoneFruit(x, y, EnumFruit.CHERRY, currentMap.zoneIndex++);
						++currentMap.pickCount;
					}
				}
			}
		}
		
		int loopState = 0;
		
		outerLoop:
		for(int x = rand.nextInt(width) * 64, y = rand.nextInt(height) * 64; ;x = rand.nextInt(width) * 64, y = rand.nextInt(height) * 64) {
			for(int k = 0; k < currentMap.zoneIndex; ++k) {
				MapZone zone = currentMap.zones[k];
				if(zone.posX == x && zone.posY == y && zone instanceof MapZoneEmpty) {  //Üres zóna keresés
					if(loopState == 0) {
						currentMap.zones[k] = new MapZoneSpawner(x, y, currentMap.zoneIndex);
						loopState = 1;
						
						for(int ycheck = zone.posY + 64; ycheck < bigWidth; ycheck += 64) {
							MapZone toSet = Map.getZoneAtPos(zone.posX, ycheck);
							if(toSet != null)
								Map.setZoneEmptyAt(toSet.zoneIndex);
						}
						
						for(int xcheck = zone.posX + 64; xcheck < bigHeight; xcheck += 64) {
							MapZone toSet = Map.getZoneAtPos(xcheck, zone.posY);
							if(toSet != null)
								Map.setZoneEmptyAt(toSet.zoneIndex);
						}
						
						for(int ycheck = zone.posY - 64; ycheck > 0; ycheck -= 64) {
							MapZone toSet = Map.getZoneAtPos(zone.posX, ycheck);
							if(toSet != null)
								Map.setZoneEmptyAt(toSet.zoneIndex);
						}
						
						for(int xcheck = zone.posX - 64; xcheck > 0; xcheck -= 64) {
							MapZone toSet = Map.getZoneAtPos(xcheck, zone.posY);
							if(toSet != null)
								Map.setZoneEmptyAt(toSet.zoneIndex);
						}
						
						int difficulty = GuiSettings.getDifficulty(), enemyCount = 0;
						if(difficulty == 0) {
							enemyCount += currentMap.zoneIndex < 70 ? 1 : currentMap.zoneIndex / 70;
						}else if(difficulty == 1) {
							enemyCount += currentMap.zoneIndex / 50;
						}else{
							enemyCount += currentMap.zoneIndex / 30;
						}
						currentMap.enemies = new EntityEnemy[enemyCount];
						
						for(int l = 0; l < enemyCount; ++l) {
							currentMap.enemies[l] = new EntityEnemy(x, y);
						}
						continue outerLoop;
					}else if(loopState == 1) {
						currentMap.players[0].posX = x;
						currentMap.players[0].posY = y;
						loopState = 2;
						
						if(isMultiplayer) continue outerLoop;
						break outerLoop;
					}else{
						currentMap.players[1].posX = x;
						currentMap.players[1].posY = y;
						break outerLoop;
					}
				}
			}
		}
		
		if(currentMap.height / 64 + 1 > GuiHelper.recommendedMaxMapHeight || currentMap.width / 64 + 1 > GuiHelper.recommendedMaxMapWidth) {
			JOptionPane.showMessageDialog(null, "Warning: map size is bigger than the recommended max map size!");
		}
		
		printDebug("Generated map with size: " + width + "x" + height);
	}
	
	public static void loadMap(String name, boolean isMultiplayer) {
		printDebug("Loading map...");
		try(ObjectInputStream input = new ObjectInputStream(new FileInputStream("./maps/" + name + ".deg"))){
			int width, height;
			
			currentMap = new Map(input.readUTF(), isMultiplayer, width = input.readShort() * 64, height = input.readShort() * 64, input.readShort(), input.readShort(), input.readShort(), input.readShort());
			
			for(int y = 0; y < height; y += 64) {
				for(int x = 0; x < width; x += 64) {
					switch(input.readByte()) {
						case 0: currentMap.zones[currentMap.zoneIndex] = new MapZoneNormal(x, y, currentMap.zoneIndex++); break;
						case 2: currentMap.zones[currentMap.zoneIndex] = new MapZoneFruit(x, y, EnumFruit.APPLE, currentMap.zoneIndex++); break;
						case 3: currentMap.zones[currentMap.zoneIndex] = new MapZoneFruit(x, y, EnumFruit.CHERRY, currentMap.zoneIndex++); ++currentMap.pickCount; break;
						case 4: currentMap.zones[currentMap.zoneIndex] = new MapZoneSpawner(x, y, currentMap.zoneIndex++);
						
						int difficulty = GuiSettings.getDifficulty(), enemyCount = 0;
						if(difficulty == 0) {
							enemyCount += currentMap.zoneIndex < 70 ? 1 : currentMap.zoneIndex / 70;
						}else if(difficulty == 1) {
							enemyCount += currentMap.zoneIndex / 50;
						}else{
							enemyCount += currentMap.zoneIndex / 30;
						}
						
						currentMap.enemies = new EntityEnemy[enemyCount];
						for(int k = 0; k < enemyCount; ++k) {
							currentMap.enemies[k] = new EntityEnemy(x, y);
						}break;
						default: currentMap.zones[currentMap.zoneIndex] = new MapZoneEmpty(x, y, currentMap.zoneIndex++);
					}
				}
			}
		}catch(IOException e){}
		
		if(currentMap.height / 64 + 1 > GuiHelper.recommendedMaxMapHeight || currentMap.width / 64 + 1 > GuiHelper.recommendedMaxMapWidth) {
			JOptionPane.showMessageDialog(null, "Warning: map size is bigger than the recommended max map size!");
		}
		
		printDebug("Map loaded with name: " + name);
	}
	
	public static boolean createSave(String fileName) {
		if(fileName != null) {
			try(ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream("./saves/" + fileName + ".sav"))){
				output.writeObject(Map.currentMap);
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public static void loadSave(String fileName) {
		if(fileName != null) {
			try(ObjectInputStream input = new ObjectInputStream(new FileInputStream("./saves/" + fileName))){
				currentMap = (Map) input.readObject();
				currentMap.texture = loadTexture(currentMap.textureStr);
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static MapZone getZoneAtPos(int x, int y) {
		for(MapZone zone : currentMap.zones) {
			if(zone.posX == x && zone.posY == y) {
				return zone;
			}
		}
		return null;
	}
	
	public static EntityEnemy getEnemyAtPos(int x, int y) {
		for(EntityEnemy enemy : currentMap.enemies) {
			if(enemy.posY == y && enemy.posX == x) {
				return enemy;
			}
		}
		return null;
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