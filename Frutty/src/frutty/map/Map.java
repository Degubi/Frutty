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
import frutty.entity.EntityPlayer;
import frutty.entity.enemies.EntityAbstractEnemy;
import frutty.entity.enemies.EntityFastEnemy;
import frutty.entity.enemies.EntityNormalEnemy;
import frutty.gui.GuiHelper;
import frutty.gui.GuiIngame;
import frutty.gui.GuiMenu;
import frutty.gui.GuiSettings;
import frutty.map.zones.MapZoneEmpty;
import frutty.map.zones.MapZoneFruit;
import frutty.map.zones.MapZoneNormal;
import frutty.map.zones.MapZoneSpawner;
import frutty.stuff.EnumFruit;

public final class Map implements Serializable{
	private static final long serialVersionUID = -5083163189200818535L;
	public static Map currentMap;
	
	public final EntityPlayer[] players;
	public final MapZone[] zones;
	public final ArrayList<Entity> entities = new ArrayList<>(); 
	public final EntityAbstractEnemy[] enemies;
	public final int width, height;
	public int pickCount, score, ticks;
	public final String textureStr;
	
	//0: width, 1: height, 2: p1X, 3: p1Y, 4: p2X, 5: p2Y
	private Map(String textureName, boolean isMulti, int w, int h, int p1X, int p1Y, int p2X, int p2Y) {
		int zoneCount = (w / 64) * (h / 64);
		zones = new MapZone[zoneCount];
		width = w - 64;
		height = h - 64;
		textureStr = textureName;
		GuiIngame.texture = loadTexture(textureName);
		
		int difficulty = GuiSettings.getDifficulty(), enemyCount = 0;
		if(difficulty == 0) {
			enemyCount += zoneCount < 70 ? 1 : zoneCount / 70;
		}else if(difficulty == 1) {
			enemyCount += zoneCount / 50;
		}else{
			enemyCount += zoneCount / 30;
		}
		
		enemies = new EntityAbstractEnemy[enemyCount];
		
		if(isMulti) {
			players = new EntityPlayer[]{new EntityPlayer(p1X, p1Y, true), new EntityPlayer(p2X, p2Y, false)};
		}else{
			players = new EntityPlayer[]{new EntityPlayer(p1X, p1Y, true)};
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
	
	private static void sizeCheck() {
		if(currentMap.height / 64 + 1 > GuiHelper.recommendedMaxMapHeight || currentMap.width / 64 + 1 > GuiHelper.recommendedMaxMapWidth) {
			JOptionPane.showMessageDialog(null, "Warning: map size is bigger than the recommended max map size!");
		}
	}
	
	public static void generateMap(int width, int height, boolean isMultiplayer) {
		printDebug("Generating map...");
		Random rand = Main.rand;
		int bigWidth = width * 64, bigHeight = height * 64, zoneIndex = 0;
		
		currentMap = new Map("normal", isMultiplayer, bigWidth, bigHeight, 0, 0, 0, 0);
		
		for(int x = 0; x < bigWidth; x += 64) {
			for(int y = 0, rng = rand.nextInt(10); y < bigHeight; y += 64, rng = rand.nextInt(10)) {
				if(rng < 6) {
					currentMap.zones[zoneIndex] = new MapZoneNormal(x, y, zoneIndex++);
				}else if(rng >= 6 && rng < 9) {
					currentMap.zones[zoneIndex] = new MapZoneEmpty(x, y, zoneIndex++);
				}else if(rng == 9) {
					if(rand.nextBoolean()) {   //isApple
						currentMap.zones[zoneIndex] = new MapZoneFruit(x, y, EnumFruit.APPLE, zoneIndex++);
					}else {
						currentMap.zones[zoneIndex] = new MapZoneFruit(x, y, EnumFruit.CHERRY, zoneIndex++);
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
							currentMap.enemies[l] = new EntityNormalEnemy(x, y);
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
		sizeCheck();
		printDebug("Generated map with size: " + width + "x" + height);
	}
	
	public static void loadMap(String name, boolean isMultiplayer) {
		printDebug("Loading map...");
		try(ObjectInputStream input = new ObjectInputStream(new FileInputStream("./maps/" + name + ".deg"))){
			int width, height, zoneIndex = 0;
			
			currentMap = new Map(input.readUTF(), isMultiplayer, width = input.readShort() * 64, height = input.readShort() * 64, input.readShort(), input.readShort(), input.readShort(), input.readShort());
			
			for(int y = 0; y < height; y += 64) {
				for(int x = 0; x < width; x += 64) {
					switch(input.readByte()) {
						case 0: currentMap.zones[zoneIndex] = new MapZoneNormal(x, y, zoneIndex++); break;
						case 2: currentMap.zones[zoneIndex] = new MapZoneFruit(x, y, EnumFruit.APPLE, zoneIndex++); break;
						case 3: currentMap.zones[zoneIndex] = new MapZoneFruit(x, y, EnumFruit.CHERRY, zoneIndex++); ++currentMap.pickCount; break;
						case 4: currentMap.zones[zoneIndex] = new MapZoneSpawner(x, y, zoneIndex++);
						
						for(int k = 0, rng = Main.rand.nextInt(2); k < currentMap.enemies.length; ++k, rng = Main.rand.nextInt(2))
							currentMap.enemies[k] = rng == 0 ? new EntityFastEnemy(x, y) : new EntityNormalEnemy(x, y); break;
							
						default: currentMap.zones[zoneIndex] = new MapZoneEmpty(x, y, zoneIndex++);
					}
				}
			}
		}catch(IOException e){}
		
		sizeCheck();
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
	
	public static MapZone[] loadBackground() {
		try(ObjectInputStream input = new ObjectInputStream(new FileInputStream("./maps/background" + Main.rand.nextInt(3) + ".deg"))){
			GuiIngame.texture = Map.loadTexture(input.readUTF());
			int width = input.readShort() * 64, height = input.readShort() * 64, zoneIndex = 0;;
			MapZone[] zonee = new MapZone[(width / 64) * (height / 64)];
			
			for(int y = 0; y < height; y += 64) {
				for(int x = 0; x < width; x += 64) {
					switch(input.readByte()) {
						case 0: zonee[zoneIndex] = new MapZoneNormal(x, y, zoneIndex++); break;
						case 2: zonee[zoneIndex] = new MapZoneFruit(x, y, EnumFruit.APPLE, zoneIndex++); break;
						case 3: zonee[zoneIndex] = new MapZoneFruit(x, y, EnumFruit.CHERRY, zoneIndex++); break;
						default: zonee[zoneIndex] = new MapZoneEmpty(x, y, zoneIndex++);
					}
				}
			}
			return zonee;
		}catch(IOException e){
			return null;
		}
	}
	
	public static String loadMapSize(String fileName) {
		try(ObjectInputStream input = new ObjectInputStream(new FileInputStream("./maps/" + fileName + ".deg"))){
			input.readUTF();
			return input.readShort() + "x" + input.readShort();
		} catch (IOException e) {}
		return null;
	}
	
	public static void loadSave(String fileName) {
		if(fileName != null) {
			try(ObjectInputStream input = new ObjectInputStream(new FileInputStream("./saves/" + fileName))){
				currentMap = (Map) input.readObject();
				GuiIngame.texture = loadTexture(currentMap.textureStr);
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
	
	public static EntityAbstractEnemy getEnemyAtPos(int x, int y) {
		for(EntityAbstractEnemy enemy : currentMap.enemies) {
			if(enemy.posY == y && enemy.posX == x) {
				return enemy;
			}
		}
		return null;
	}
	
	public static EntityAbstractEnemy getEnemyPredictedAtPos(int x, int y, EntityBall entity) {
		for(EntityAbstractEnemy enemy : currentMap.enemies) {
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