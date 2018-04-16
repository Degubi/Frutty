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
 * Map class, nem a legszebb de kb a leghasznosabb. Sok static f�ggv�ny ment�skezel�sre �s mapkezel�sre, illetve tartalmazza a currentMap static fieldet is
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
	
	public static final BufferedImage[] textures = new BufferedImage[5];  //Static �gy nem menti le a serializ�l�s
	
	/**
	 * Constructor a Map object l�trehoz�s�ra, labda hozz�ad�s, text�rabet�lt�s, j�t�kos hozz�ad�sa.
	 * @param mapWidth Map hossza
	 * @param mapHeight Map magass�g
	 * @param playerPosX J�t�kos X koordin�t�ja
	 * @param playerPosY J�t�kos Y koordin�t�ja
	 * @param textureName Text�ra n�v a norm�l z�na haszn�lat�ra
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
	 * Text�ra bet�lt�s�re szolg�l� f�ggv�ny. Alap text�ra 1x, majd 4 m�sik s�t�t�tett v�ltozat gener�l�sa
	 * @param textureName Text�ra neve
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
	 * �j BufferedImage object k�sz�t�se s�t�t�ve
	 * @param image Reference k�p
	 * @return �j k�p
	 */
	private static BufferedImage copyDarkened(BufferedImage image) {
		// �j BufferedImage kell, mivel ha a setRGB m�dos�tja a param�ter k�p�t, akkor a t�mbben fent az �sszes ugyan az lesz... (Ez�rt nem m�dos�tjuk a pointert ami a k�p objectre mutat)
		// Haszn�ljunk teljesen �j BufferedImage-t, nem kell �tm�solni a dolgokat az eredetib�l... (m�rc. 26)
		
		BufferedImage toReturn = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
		
		// Bit oper�toros megold�s szint�n m�rc. 26, -16500 object, 5 MB-al kevesebb ram haszn�lat map bet�lt�skor
		// Bit operatorok: '&' -> k�z�s bitek, '|' -> ahol 1-es az a bit megtart�sa, '<<' && '>>' -> bit shiftel�s balra/jobbra
		// R�gi k�d (2 color object (64 * 64 * 4 db ink�bb) eloptimaliz�l�sa)
		
		//toReturn.setRGB(x, y, new Color(image.getRGB(x, y)).darker().getRGB());
		for(int x = 0; x < 64; ++x) {
			for(int y = 0; y < 64; ++y) {
				int startColor = image.getRGB(x, y);
				toReturn.setRGB(x, y, ((255 & 0xFF) << 24) |  //Alpha
		                (((int) ((startColor >> 16 & 0xFF) * 0.7F) & 0xFF) << 16) |  //Red
		                (((int) ((startColor >> 8 & 0xFF) * 0.7F) & 0xFF) << 8) |  //Green
		                (((int) ((startColor & 0xFF) * 0.7F) & 0xFF) << 0));  //Bl��
			}
		}
		return toReturn;
	}
	
	/**
	 * Debug �zenet �r�sa a konzolba ha a debug enged�lyezve van
	 * @param msg �zenet
	 */
	private static void printDebug(String msg) {
		if(GuiSettings.isDebugEnabled()) {
			System.out.println(msg);
		}
	}
	
	/**
	 * Map gener�l�sra szolg�l� f�ggv�ny. H�t, random z�na gener�l�s, arra j�n r� 2 loop ami a j�t�kos spawnt �s a mob spawnert csin�lja
	 * @param width Map hossza
	 * @param height Map magass�ga
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
				if(zone.posX == x && zone.posY == y && zone instanceof MapZoneEmpty) {  //�res z�na keres�s
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
	 * Map bet�lt�s, Map object l�trehoz�s, enemies t�mb inicializ�l�s, koordin�takezel�s, minden
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
	 * Ment�s f�jl l�trehoz�s (Serializ�ci� itt)
	 * @param fileName Ment�s neve
	 * @return True ha l�trej�tt a ment�s
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
	 * Ment�s f�jl bet�lt�s (Serializ�ci� itt is)
	 * @param fileName Ment�s neve
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
	 * Map m�ret�nek olvas�sa map f�jlb�l (az els� 2 int a map form�tumban a sz�less�g �s magass�g), men�be kell
	 * @param fileName Map neve
	 * @return Map m�rete String-ben
	 */
	public static String loadMapSize(String fileName) {
		try(ObjectInputStream input = new ObjectInputStream(new FileInputStream("./maps/" + fileName + ".deg"))){
			return input.readInt() + "x" + input.readInt();
		} catch (IOException e) {}
		return null;
	}
	
	/**
	 * F�ggv�ny koordin�ta szerinti z�na lek�r�sre
	 * @param x X koordin�ta
	 * @param y Y koordin�ta
	 * @return A MapZ�n�t ha van egy�bk�nt null
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
	 * Norm�l ellens�g poz�ci�checkol�s f�ggv�ny
	 * @param x X koordin�ta
	 * @param y Y koordin�ta
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
	 * Kis poz�ci� hackel�s dolog, hogy a labda so-so m�k�d�k�pesnek nevezhet� legyen. 
	 * @param x X koordin�ta
	 * @param y Y koordin�ta
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