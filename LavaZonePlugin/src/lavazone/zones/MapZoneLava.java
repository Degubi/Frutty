package lavazone.zones;

import frutty.entity.*;
import frutty.tools.*;
import frutty.world.base.*;

public final class MapZoneLava extends MapZoneFluid {
    
    public MapZoneLava() {
        super("lava:lavaZone", Material.loadTexture("map/special", "lava.png"));
    }
    
    @Override
    public void onZoneEntered(int x, int y, Material material, EntityPlayer player) {
        player.onKilled(null);
        
        super.onZoneEntered(x, y, material, player);
    }
}