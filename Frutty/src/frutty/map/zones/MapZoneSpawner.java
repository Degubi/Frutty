package frutty.map.zones;

import java.awt.Color;
import java.awt.Graphics;

import frutty.map.MapZone;

/**
 * Spawner z�na class, static block-ban van 1 el�re cachelt Color t�mb a framenk�nti sz�n object gy�rt�s elker�l�se �rdek�ben.
 */
public class MapZoneSpawner extends MapZone{
	private static final Color[] colorCache = new Color[32];
	private boolean decrease = false;
	private int arrayIndexer = 0;
	
	//Csak j� lesz az a static init block a null check-os constructor helyett... :|
	static {
		for(int red = 125, green = 125, indexer = 0; green < 250; ++red, green += 4, ++indexer) {
			colorCache[indexer] = new Color(red, green, 125);
		}
	}
	
	public MapZoneSpawner(int xPos, int yPos, int zoneIndex) {
		super(xPos, yPos, zoneIndex);
	}

	/**
	 * Oda vissza halad a el�re cachelt Color object t�mbben
	 */
	@Override
	public void draw(Graphics graphics) {
		if(arrayIndexer == 24) {
			decrease = true;
		}
		if(arrayIndexer == 0) {
			decrease = false;
		}
		graphics.setColor(colorCache[decrease ? --arrayIndexer : ++arrayIndexer]);
		graphics.fillRect(posX, posY, 64, 64);
	}
}