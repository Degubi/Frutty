package frutty.world.zones;

import frutty.entity.living.*;
import frutty.tools.*;
import frutty.world.*;
import java.awt.*;
import javax.swing.*;

public final class MapZoneSpawner extends MapZoneBase {
    private static final Color[] colorCache = createColorCache();
    private boolean decrease = false;
    private int colorIndexer = 0;

    public MapZoneSpawner() {
        super("spawnerZone", false, false);
    }

    @Override
    public void render(int x, int y, Material material, Graphics graphics) {
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

        for(var k = 0; k < enemyCount; ++k) {
            World.enemies[k] = EntityEnemy.create(x, y);
        }
    }

    @Override
    public boolean canPlayerPass(int x, int y) {
        return false;
    }

    @Override
    protected ImageIcon getEditorIcon() {
        return new ImageIcon(Material.loadTexture("dev", "spawner.png"));
    }

    private static Color[] createColorCache() {
        var cache = new Color[32];
        for(int red = 125, green = 125, indexer = 0; green < 250; ++red, green += 4, ++indexer) {
            cache[indexer] = new Color(red, green, 125);
        }
        return cache;
    }
}