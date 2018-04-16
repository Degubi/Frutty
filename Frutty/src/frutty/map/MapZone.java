package frutty.map;

import java.awt.Graphics;
import java.io.Serializable;

import frutty.gui.GuiStats;
import frutty.map.zones.MapZoneEmpty;

/**
 * <p>Az összes MapZone class Alap Class fájlja. Serializálható a mentés funkció miatt.
 * <p>Minden abstract függvénye a GuiIngame update és render update részében hívódik be.
 */
public abstract class MapZone implements Serializable{
	private static final long serialVersionUID = 392316063689927131L;
	
	protected boolean notified;
	public final int posX, posY, zoneIndex;
	
	/**
	 * Alap constructor
	 * @param xPos X koordináta
	 * @param yPos Y koordináta
	 * @param pass Keresztül lehet e menni a zónán (NPC logika)
	 */
	public MapZone(int xPos, int yPos, int index) {
		posX = xPos;
		posY = yPos;
		zoneIndex = index;
	}
	
	public static boolean isEmpty(int x, int y) {
		MapZone zone = Map.getZoneAtPos(x, y);
		return zone != null && zone instanceof MapZoneEmpty;
	}
	
	/**
	 * EntityPlayer mozgásakor használatos, bármilyen mezõ széttörésekor hívódik be, lecseréli a jelenlegi zónát üresre, és a felette levõ zónát notify-olja.
	 * @param currentIndex Zóna indexe a Map ZónaListájában
	 */
	public void onBreak() {
		Map.setZoneEmptyAt(zoneIndex);
		MapZone up = Map.getZoneAtPos(posX, posY - 64);
		++GuiStats.zoneCount;
		if(up != null) {
			up.notified = true;
		}
	}
	
	/**
	 * Framenként meghívandó draw függvény
	 * @param panel JPanel instance
	 * @param graphics Graphics object a rajzolásra
	 */
	public abstract void draw(Graphics graphics);
	
	/**
	 * Zóna Típus kezelésre és lekérésre szolgáló függvény
	 * @return A zóna típusa
	 */
}