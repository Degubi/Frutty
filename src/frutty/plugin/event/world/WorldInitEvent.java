package frutty.plugin.event.world;

import frutty.*;
import frutty.entity.*;
import java.util.*;

/**Event is fired when a map is initialized, can add entities, or change textures*/
@FruttyEventMarker
public final class WorldInitEvent{
	public final int mapWidth, mapHeight;
	public final String[] mapTextureCache;
	public final List<Entity> mapEntities;
	
	public WorldInitEvent(int w, int h, String[] textures, List<Entity> ents) {
		mapWidth = w;
		mapHeight = h;
		mapTextureCache = textures;
		mapEntities = ents;
	}
}