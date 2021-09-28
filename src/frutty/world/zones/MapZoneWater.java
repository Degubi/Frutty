package frutty.world.zones;

import frutty.tools.*;
import frutty.world.*;

public final class MapZoneWater extends MapZoneFluid {
    
    public MapZoneWater() {
        super("waterZone", Material.loadTexture("map/special", "water.png"));
    }
}