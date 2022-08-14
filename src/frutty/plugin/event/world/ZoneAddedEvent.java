package frutty.plugin.event.world;

import frutty.*;
import frutty.world.*;

/**Event is fired when a zone gets added to the world*/
@FruttyEventMarker
public final class ZoneAddedEvent {
    public final WorldZone zone;
    public final int x, y;
    public boolean canceled = false;

    public ZoneAddedEvent(WorldZone zone, int x, int y) {
        this.zone = zone;
        this.x = x;
        this.y = y;
    }
}