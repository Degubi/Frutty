package frutty.plugin;

import java.awt.image.BufferedImage;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import frutty.Main;
import frutty.map.MapZone;
import frutty.plugin.event.MapInitEvent;

public final class PluginRegistry {
	private static final Lookup lookup = MethodHandles.lookup();

	/**
	 * @param zoneID Zone ID
	 * @param zone The zone object
	 * @param editorTextureName Name of the texture used in editor, put it in the dev folder
	 */
	public static void registerZone(String zoneID, MapZone zone) {
		if(Main.zoneRegistry.containsKey(zoneID)) {
			throw new IllegalArgumentException("Zone already registered with ID: " + zoneID);
		}
		Main.zoneRegistry.put(zoneID, zone);
	}
	
	public static BufferedImage loadTexture(String prefix, String name) {
		return Main.loadTexture(prefix, name);
	}
	
	public static void registerEventClass(Class<?> eventClass) {
		Method[] methods = eventClass.getMethods();
		
		for(Method method : methods) {
			if(method.isAnnotationPresent(EventHandler.class)) {
				if((method.getModifiers() & Modifier.STATIC) != 0 && method.getParameterCount() == 1) {
					Class<?> eventType = method.getParameterTypes()[0];
					if(eventType == MapInitEvent.class) {
						try {
							Main.mapLoadEvents.add(lookup.unreflect(method));
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					}
					
				}else {
					System.err.println("Method from class: " + eventClass + ", methodName: " + method.getName() + " is not static or has more than 1 parameters");
				}
			}
		}
	}
}