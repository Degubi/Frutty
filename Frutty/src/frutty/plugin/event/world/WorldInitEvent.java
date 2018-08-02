package frutty.plugin.event.world;

import java.util.ArrayList;

import frutty.entity.Entity;
import frutty.plugin.internal.EventBase;

/**Event is fired when a map is initialized, can add entities, or change textures*/
public final class WorldInitEvent extends EventBase{
	public final int mapWidth, mapHeight;
	public final String[] mapTextureCache;
	public final ArrayList<Entity> mapEntities;
	
	public WorldInitEvent(int w, int h, String[] text, ArrayList<Entity> ents) {
		mapWidth = w;
		mapHeight = h;
		mapTextureCache = text;
		mapEntities = ents;
	}
}