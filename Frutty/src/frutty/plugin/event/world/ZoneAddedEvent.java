package frutty.plugin.event.world;

import frutty.*;
import frutty.world.base.*;

@FruttyEventMarker
public final class ZoneAddedEvent{
	public final MapZoneBase zone;
	public final int x, y;
	public boolean canceled = false;
	
	public ZoneAddedEvent(MapZoneBase mapZoneBase, int x, int y) {
		zone = mapZoneBase;
		this.x = x;
		this.y = y;
	}
}