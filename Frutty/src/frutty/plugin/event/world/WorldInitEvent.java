package frutty.plugin.event.world;

import frutty.entity.*;
import frutty.plugin.internal.*;
import java.util.*;

/**Event is fired when a map is initialized, can add entities, or change textures*/
public final class WorldInitEvent implements EventBase{
	public final int mapWidth, mapHeight;
	public final String[] mapTextureCache;
	public final List<Entity> mapEntities;
	
	public WorldInitEvent(int w, int h, String[] text, List<Entity> ents) {
		mapWidth = w;
		mapHeight = h;
		mapTextureCache = text;
		mapEntities = ents;
	}
}