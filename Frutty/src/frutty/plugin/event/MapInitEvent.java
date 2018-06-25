package frutty.plugin.event;

import java.util.ArrayList;

import frutty.entity.Entity;
import frutty.tools.internal.EventBase;

public final class MapInitEvent extends EventBase{
	public final int width, height;
	public final String[] textures;
	public final ArrayList<Entity> entities;
	
	public MapInitEvent(int w, int h, String[] text, ArrayList<Entity> ents) {
		width = w;
		height = h;
		textures = text;
		entities = ents;
	}
}