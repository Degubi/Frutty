package frutty.world.zones;

import frutty.entity.*;
import frutty.tools.*;
import frutty.world.*;
import frutty.world.base.*;
import java.awt.*;
import javax.swing.*;

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
		super("spawnerZone", false, false);
	}
	
	@Override
	public void draw(int x, int y, Material material, Graphics graphics) {
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
	public void onZoneAdded(boolean isCoop, int x, int y) {
	    var enemyCount = World.getEnemyCountBasedOnDifficulty(World.zones.length);
		World.enemies = new EntityEnemy[enemyCount];
		
		for(int k = 0; k < enemyCount; ++k) {
			World.enemies[k] = new EntityEnemy(x, y);
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