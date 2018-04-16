package frutty.map;

import java.awt.Graphics;
import java.io.Serializable;

import frutty.gui.GuiStats;
import frutty.map.zones.MapZoneEmpty;

/**
 * <p>Az �sszes MapZone class Alap Class f�jlja. Serializ�lhat� a ment�s funkci� miatt.
 * <p>Minden abstract f�ggv�nye a GuiIngame update �s render update r�sz�ben h�v�dik be.
 */
public abstract class MapZone implements Serializable{
	private static final long serialVersionUID = 392316063689927131L;
	
	protected boolean notified;
	public final int posX, posY, zoneIndex;
	
	/**
	 * Alap constructor
	 * @param xPos X koordin�ta
	 * @param yPos Y koordin�ta
	 * @param pass Kereszt�l lehet e menni a z�n�n (NPC logika)
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
	 * EntityPlayer mozg�sakor haszn�latos, b�rmilyen mez� sz�tt�r�sekor h�v�dik be, lecser�li a jelenlegi z�n�t �resre, �s a felette lev� z�n�t notify-olja.
	 * @param currentIndex Z�na indexe a Map Z�naList�j�ban
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
	 * Framenk�nt megh�vand� draw f�ggv�ny
	 * @param panel JPanel instance
	 * @param graphics Graphics object a rajzol�sra
	 */
	public abstract void draw(Graphics graphics);
	
	/**
	 * Z�na T�pus kezel�sre �s lek�r�sre szolg�l� f�ggv�ny
	 * @return A z�na t�pusa
	 */
}