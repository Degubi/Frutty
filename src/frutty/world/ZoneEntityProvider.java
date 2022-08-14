package frutty.world;

import frutty.entity.zone.*;

/**Interface used for adding unique data per zone. Sometimes we need this because the zones in the game are implemented via FlyWeight pattern.*/
public interface ZoneEntityProvider {
    /**
     * This method needs to return a new instance of a EntityZone. E.g: For the apple zone, we need to store some counters per zone for delaying apple dropping.
     * @param x X coordinate of the zone
     * @param y Y coordinate of the zone
     * @return The new EntityZone instance
     */
    EntityZone getZoneEntity();
}