package frutty.world.zones;

import frutty.tools.*;
import frutty.world.*;

public final class WorldZoneWater extends WorldZoneFluid {

    public WorldZoneWater() {
        super("waterZone", Material.loadTexture("world/special/water.png"));
    }
}