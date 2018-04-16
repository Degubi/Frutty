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

import frutty.entity.Entity;
import frutty.entity.EntityBall;
import frutty.entity.EntityEnemy;
import frutty.entity.EntityPlayer;
import frutty.gui.GuiHelper;
import frutty.gui.GuiIngame;
import frutty.gui.GuiMenu;
import frutty.gui.GuiSettings;
import frutty.map.zones.MapZoneEmpty;
import frutty.map.zones.MapZoneFruit;
import frutty.map.zones.MapZoneNormal;
import frutty.map.zones.MapZoneSpawner;
import frutty.stuff.EnumFruit;

/**
 * Map class, nem a legszebb de kb a leghasznosabb. Sok static függvény mentéskezelésre és mapkezelésre, illetve tartalmazza a currentMap static fieldet is
 */
public class Map implements Serializable{
	private static final long serialVersionUID = -5083163189200818535L;
	public static Map currentMap;
	
	private final EntityPlayer[] players;
	public final MapZone[] zones;
	public final ArrayList<Entity> entities = new ArrayList<>(); 
	public EntityEnemy[] enemies;
	public final int width, height;
	public int pickCount, score, zoneIndex, ticks;
	private final String texture;
	
	public static final BufferedImage[] textures = new BufferedImage[5];  //Static így nem menti le a serializálás
	
	/**
	 * Constructor a Map object létrehozására, labda hozzáadás, textúrabetöltés, játékos hozzáadása.
	 * @param mapWidth Map hossza
	 * @param mapHeight Map magasság
	 * @param playerPosX Játékos X koordinátája
	 * @param playerPosY Játékos Y koordinátája
	 * @param textureName Textúra név a normál zóna használatára
	 */
	private Map(int mapWidth, int mapHeight, int playerPosX, int playerPosY, boolean isMulti, String textureName) {
		zones = new MapZone[(mapWidth / 64 + 1) * (mapHeight / 64 + 1)];
		width = mapWidth;
		height = mapHeight;
		texture = textureName;
		
		if(isMulti) {
			players = new EntityPlayer[]{new EntityPlayer(playerPosX, playerPosY, true), new EntityPlayer(playerPosX, playerPosY + 64, false)};
		}else {
			players = new EntityPlayer[]{new EntityPlayer(playerPosX, playerPosY, true)};
		}
		
		entities.add(new EntityBall());
		loadTexture(textureName);
	}
	
	/**
	 * Textúra betöltésére szolgáló függvény. Alap textúra 1x, majd 4 másik sötétített változat generálása
	 * @param textureName Textúra neve
	 */
	private static void loadTexture(String textureName) {
		printDebug("Started loading texture " + textureName);
		try{
			textures[0] = ImageIO.read(GuiMenu.class.getResource("/textures/map/" + textureName + ".png"));
			for(int k = 1; k < 5; ++k) {
				textures[k] = copyDarkened(textures[k - 1]);
			}
			printDebug(textureName + " loaded");
		}catch (IOException e) {}
	}
	
	/**
	 * Új BufferedImage object készítése sötétíve
	 * @param image Reference kép
	 * @return Új kép
	 */
	private static BufferedImage copyDarkened(BufferedImage image) {
		// Új BufferedImage kell, mivel ha a setRGB módosítja a paraméter képét, akkor a tömbben fent az összes ugyan az lesz... (Ezért nem módosítjuk a pointert ami a kép objectre mutat)
		// Használjunk teljesen új BufferedImage-t, nem kell átmásolni a dolgokat az eredetibõl... (márc. 26)
		
		BufferedImage toReturn = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
		
		// Bit operátoros megoldás szintén márc. 26, -16500 object, 5 MB-al kevesebb ram használat map betöltéskor
		// Bit operatorok: '&' -> közös bitek, '|' -> ahol 1-es az a bit megtartása, '<<' && '>>' -> bit shiftelés balra/jobbra
		// Régi kód (2 color object (64 * 64 * 4 db inkább) eloptimalizálása)
		
		//toReturn.setRGB(x, y, new Color(image.getRGB(x, y)).darker().getRGB());
		for(int x = 0; x < 64; ++x) {
			for(int y = 0; y < 64; ++y) {
				int startColor = image.getRGB(x, y);
				toReturn.setRGB(x, y, ((255 & 0xFF) << 24) |  //Alpha
		                (((int) ((startColor >> 16 & 0xFF) * 0.7F) & 0xFF) << 16) |  //Red
		                (((int) ((startColor >> 8 & 0xFF) * 0.7F) & 0xFF) << 8) |  //Green
		                (((int) ((startColor & 0xFF) * 0.7F) & 0xFF) << 0));  //Blúú
			}
		}
		return toReturn;
	}
	
	/**
	 * Debug üzenet írása a konzolba ha a debug engedélyezve van
	 * @param msg Üzenet
	 */
	private static void printDebug(String msg) {
		if(GuiSettings.isDebugEnabled()) {
			System.out.println(msg);
		}
	}
	
	/**
	 * Map generálásra szolgáló függvény. Hát, random zóna generálás, arra jön rá 2 loop ami a játékos spawnt és a mob spawnert csinálja
	 * @param width Map hossza
	 * @param height Map magassága
	 */
	public static void generateMap(int width, int height, boolean isMultiplayer) {
		printDebug("Generating map...");
		Random rand = GuiIngame.rand;
		int bigWidth = width * 64, bigHeight = height * 64;
		
		currentMap = new Map(bigWidth - 64, bigHeight - 64, 0, 0, isMultiplayer, "normal");
		
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
		
		boolean spawnerSet = false;
		
		outerLoop:
		for(int x = rand.nextInt(width) * 64, y = rand.nextInt(height) * 64; ;x = rand.nextInt(width) * 64, y = rand.nextInt(height) * 64) {
			for(int k = 0; k < currentMap.zoneIndex; ++k) {
				MapZone zone = currentMap.zones[k];
				if(zone.posX == x && zone.posY == y && zone instanceof MapZoneEmpty) {  //Üres zóna keresés
					if(!spawnerSet) {
						currentMap.zones[k] = new MapZoneSpawner(x, y, currentMap.zoneIndex);
						spawnerSet = true;
						
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
						}else {
							enemyCount += currentMap.zoneIndex / 30;
						}
						currentMap.enemies = new EntityEnemy[enemyCount];
						
						for(int l = 0; l < enemyCount; ++l) {
							currentMap.enemies[l] = new EntityEnemy(x, y);
						}
						continue;
					}
					currentMap.players[0].posX = x;
					currentMap.players[0].posY = y;
					
					break outerLoop;
				}
			}
		}
		
		if(currentMap.height / 64 + 1 > GuiHelper.recommendedMaxMapHeight || currentMap.width / 64 + 1 > GuiHelper.recommendedMaxMapWidth) {
			JOptionPane.showMessageDialog(null, "Warning: map size is bigger than the recommended max map size!");
		}
		
		printDebug("Generated map with size: " + width + "x" + height);
	}
	
	/**
	 * Map betöltés, Map object létrehozás, enemies tömb inicializálás, koordinátakezelés, minden
	 * @param name Map neve
	 */
	public static void loadMap(String name, boolean isMultiplayer) {
		printDebug("Loading map...");
		try(ObjectInputStream input = new ObjectInputStream(new FileInputStream("./maps/" + name + ".deg"))){
			int width = input.readInt() * 64, height = input.readInt() * 64;
			int spawnerX = 0, spawnerY = 0;
			
			currentMap = new Map(width - 64, height - 64, input.readInt(), input.readInt(), isMultiplayer, input.readUTF());
			
			for(int y = 0; y < height; y += 64) {
				for(int x = 0; x < width; x += 64) {
					switch(input.readChar()) {
						case 'd': currentMap.zones[currentMap.zoneIndex] = new MapZoneEmpty(x, y, currentMap.zoneIndex++); break;
						case 's': currentMap.zones[currentMap.zoneIndex] = new MapZoneSpawner(x, y, currentMap.zoneIndex++); spawnerX = x; spawnerY = y; break;
						case 'a': currentMap.zones[currentMap.zoneIndex] = new MapZoneFruit(x, y, EnumFruit.APPLE, currentMap.zoneIndex++); break;
						case 'c': currentMap.zones[currentMap.zoneIndex] = new MapZoneFruit(x, y, EnumFruit.CHERRY, currentMap.zoneIndex++); ++currentMap.pickCount; break;
						default:  currentMap.zones[currentMap.zoneIndex] = new MapZoneNormal(x, y, currentMap.zoneIndex++);
					}
				}
			}
			
			int difficulty = GuiSettings.getDifficulty(), enemyCount = 0;
			if(difficulty == 0) {
				enemyCount += currentMap.zoneIndex < 70 ? 1 : currentMap.zoneIndex / 70;
			}else if(difficulty == 1) {
				enemyCount += currentMap.zoneIndex / 50;
			}else {
				enemyCount += currentMap.zoneIndex / 30;
			}
			
			currentMap.enemies = new EntityEnemy[enemyCount];
			for(int k = 0; k < enemyCount; ++k) {
				currentMap.enemies[k] = new EntityEnemy(spawnerX, spawnerY);
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
		
		if(currentMap.height / 64 + 1 > GuiHelper.recommendedMaxMapHeight || currentMap.width / 64 + 1 > GuiHelper.recommendedMaxMapWidth) {
			JOptionPane.showMessageDialog(null, "Warning: map size is bigger than the recommended max map size!");
		}
		
		printDebug("Map loaded with name: " + name);
	}
	
	/**
	 * Mentés fájl létrehozás (Serializáció itt)
	 * @param fileName Mentés neve
	 * @return True ha létrejött a mentés
	 */
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
	
	/**
	 * Mentés fájl betöltés (Serializáció itt is)
	 * @param fileName Mentés neve
	 */
	public static void loadSave(String fileName) {
		if(fileName != null) {
			try(ObjectInputStream input = new ObjectInputStream(new FileInputStream("./saves/" + fileName))){
				currentMap = (Map) input.readObject();
				loadTexture(currentMap.texture);
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Map méretének olvasása map fájlból (az elsö 2 int a map formátumban a szélesség és magasság), menübe kell
	 * @param fileName Map neve
	 * @return Map mérete String-ben
	 */
	public static String loadMapSize(String fileName) {
		try(ObjectInputStream input = new ObjectInputStream(new FileInputStream("./maps/" + fileName + ".deg"))){
			return input.readInt() + "x" + input.readInt();
		} catch (IOException e) {}
		return null;
	}
	
	/**
	 * Függvény koordináta szerinti zóna lekérésre
	 * @param x X koordináta
	 * @param y Y koordináta
	 * @return A MapZónát ha van egyébként null
	 */
	public static MapZone getZoneAtPos(int x, int y) {
		for(MapZone zone : currentMap.zones) {
			if(zone.posX == x && zone.posY == y) {
				return zone;
			}
		}
		return null;
	}
	
	/**
	 * Normál ellenség pozíciócheckolós függvény
	 * @param x X koordináta
	 * @param y Y koordináta
	 * @return Ha van akkor az enemy-t else null
	 */
	public static EntityEnemy getEnemyAtPos(int x, int y) {
		for(EntityEnemy enemy : currentMap.enemies) {
			if(enemy.posY == y && enemy.posX == x) {
				return enemy;
			}
		}
		return null;
	}
	
	/**
	 * Kis pozíció hackelõs dolog, hogy a labda so-so müködöképesnek nevezhetõ legyen. 
	 * @param x X koordináta
	 * @param y Y koordináta
	 * @param entity Labda instance
	 * @return Ha van ott enemy a akkor az enemy-t ha nincs akkor null
	 */
	public static EntityEnemy getEnemyPredictedAtPos(int x, int y, EntityBall entity) {
		for(EntityEnemy enemy : currentMap.enemies) {
			if((enemy.posY == y && enemy.posX == x) || (enemy.posY == y - entity.motionY && enemy.posX == x - entity.motionX)) {
				return enemy;
			}
		}
		return null;
	}
	
	public static EntityPlayer[] getPlayers() {
		return currentMap.players;
	}
	
	public static EntityBall getBall() {
		return (EntityBall) currentMap.entities.get(0);
	}
	
	public static void setZoneEmptyAt(int index) {
		currentMap.zones[index] = new MapZoneEmpty(currentMap.zones[index].posX, currentMap.zones[index].posY, index);
	}
}