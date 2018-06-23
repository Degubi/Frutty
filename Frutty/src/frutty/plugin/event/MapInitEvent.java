package frutty.plugin.event;

import java.lang.invoke.MethodHandle;
import java.util.ArrayList;

import frutty.entity.Entity;

public final class MapInitEvent {
	public final int width, height;
	public final String[] textures;
	public final ArrayList<Entity> entities;
	
	public MapInitEvent(int w, int h, String[] text, ArrayList<Entity> ents) {
		width = w;
		height = h;
		textures = text;
		entities = ents;
	}
	
	public void invoke(MethodHandle handle) {
		try {
			handle.invokeExact(this);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}