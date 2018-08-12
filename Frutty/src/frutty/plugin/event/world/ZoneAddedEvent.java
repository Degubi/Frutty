package frutty.plugin.event.world;

import frutty.plugin.internal.EventBase;
import frutty.world.base.MapZoneBase;

public final class ZoneAddedEvent extends EventBase{
	public final MapZoneBase zone;
	public final int x, y;
	public boolean canceled = false;
	
	public ZoneAddedEvent(MapZoneBase mapZoneBase, int x, int y) {
		zone = mapZoneBase;
		this.x = x;
		this.y = y;
	}
}