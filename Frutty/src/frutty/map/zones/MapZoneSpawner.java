package frutty.map.zones;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.ImageIcon;

import frutty.entity.EntityEnemy;
import frutty.gui.Settings;
import frutty.map.Map;
import frutty.map.interfaces.MapZoneBase;

public final class MapZoneSpawner extends MapZoneBase{
	private static final Color[] colorCache = new Color[32];
	private boolean decrease = false;
	private int colorIndexer = 0;
	
	//Csak jó lesz az a static init block a null check-os constructor helyett... :|
	static {
		for(int red = 125, green = 125, indexer = 0; green < 250; ++red, green += 4, ++indexer) {
			colorCache[indexer] = new Color(red, green, 125);
		}
	}

	public MapZoneSpawner() {
		super(false, false, false);
	}
	
	@Override
	public void draw(int x, int y, int textureIndex, Graphics2D graphics) {
		if(colorIndexer == 31) {
			decrease = true;
		}
		if(colorIndexer == 0) {
			decrease = false;
		}
		graphics.setColor(colorCache[decrease ? --colorIndexer : ++colorIndexer]);
		graphics.fillRect(x, y, 64, 64);
	}

	@Override
	public void onZoneAdded(boolean isBackground, int x, int y) {
		int enemyCount = 0, zoneCount = Map.zones.length;
		if(!Settings.disableEnemies && !isBackground) {
			if(Settings.difficulty == 0) {
				enemyCount += zoneCount < 70 ? 1 : zoneCount / 70;
			}else if(Settings.difficulty == 1) {
				enemyCount += zoneCount / 50;
			}else{
				enemyCount += zoneCount / 30;
			}
		}
		
		Map.enemies = new EntityEnemy[enemyCount];
		
		if(!isBackground) {
			for(int k = 0; k < +Map.enemies.length; ++k) {
				Map.enemies[k] = new EntityEnemy(x, y);
			}
		}
	}
	
	@Override
	public boolean canPlayerPass(int x, int y) {
		return false;
	}

	@Override
	protected ImageIcon getEditorIcon() {
		return new ImageIcon("./textures/dev/spawner.png");
	}
}