package frutty.plugin.event.world;

import frutty.plugin.internal.*;
import frutty.world.base.*;

public final class ZoneAddedEvent implements EventBase{
	public final MapZoneBase zone;
	public final int x, y;
	public boolean canceled = false;
	
	public ZoneAddedEvent(MapZoneBase mapZoneBase, int x, int y) {
		zone = mapZoneBase;
		this.x = x;
		this.y = y;
	}
}