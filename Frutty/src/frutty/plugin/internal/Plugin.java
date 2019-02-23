package frutty.plugin.internal;

import frutty.*;
import frutty.plugin.*;
import frutty.tools.*;
import java.io.*;
import java.lang.invoke.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;

public final class Plugin{
	public static final List<Plugin> plugins = FruttyMain.toList(new Plugin("Frutty", "Base module for the game.", "", Version.from(1, 4, 1), "https://pastebin.com/raw/m5qJbnks"), new Plugin("Frutty Plugin Loader", "Base module for the plugin loader", "", Version.from(1, 0, 0), ""));
	
	public final String description, ID, updateURL, versionURL;
	public final Version version;
	public boolean needsUpdate = false;
	
	public Plugin(String name, String desc, String url, Version ver, String verURL) {
		description = desc;
		ID = name;
		updateURL = url;
		version = ver;
		versionURL = verURL;
	}
		
	@Override
	public String toString() {
		return ID;
	}
		
	public String getInfo() {
		return "Name: " + ID + 
				"<br>Version: " + version + 
				"<br>URL: " + (updateURL == null ? "" : ("<a href=" + updateURL + ">" + updateURL + "</a>")) + 
				"<br>Needs update: <b><font color=" + (needsUpdate ? "red>" : "green>") + (needsUpdate ? "Yes" : "No") + "</font></b>" + (
				description != null ? ("<br>Description: " + description) : "");
	}
	
	private static Manifest getManifestFromJar(File jarPath) {
		try(var jar = new JarFile(jarPath)){
			return jar.getManifest();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean loadPlugins() {
		File[] pluginNames = new File("plugins").listFiles((dir, name) -> name.endsWith(".jar"));
		int pluginCount = pluginNames.length;
		
		if(pluginCount > 0) {
			String[] mainClassNames = new String[pluginCount];
			URL[] classLoaderNames = new URL[pluginCount];
			
			for(int k = 0; k < pluginCount; ++k) {
				try {
					classLoaderNames[k] = pluginNames[k].toURI().toURL();
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				}
				
				var mani = getManifestFromJar(pluginNames[k]);
				
				if(mani == null) {
					throw new IllegalStateException("Can't find manifest file from plugin: " + pluginNames[k]);
				}
					
				String pluginClass = mani.getMainAttributes().getValue("Plugin-Class");
				if(pluginClass == null) {
					throw new IllegalStateException("Can't find \"Plugin-Class\" attribute from plugin: " + pluginNames[k]);
				}
				mainClassNames[k] = pluginClass;
			}
			
			try{
				@SuppressWarnings("resource")
				var urlClass = new URLClassLoader(classLoaderNames, FruttyMain.class.getClassLoader()); //Dont close this or shit breaks
				
				for(int k = 0; k < mainClassNames.length; ++k) {
					if(mainClassNames[k] == null) {
						throw new IllegalStateException("Can't load main class from plugin: " + pluginNames[k]);
					}
					
					var loaded = urlClass.loadClass(mainClassNames[k]);
					if(!loaded.isAnnotationPresent(FruttyPlugin.class)) {
						throw new IllegalStateException("Main class from plugin: " + pluginNames[k] + " is not annotated with @FruttyPlugin");
					}
					
					var pluginAnnotation = loaded.getDeclaredAnnotation(FruttyPlugin.class);
					plugins.add(new Plugin(pluginAnnotation.name(), pluginAnnotation.description(), pluginAnnotation.updateURL(), Version.fromString(pluginAnnotation.version()), pluginAnnotation.versionURL()));
					
					var ranMain = false;
					for(var method : loaded.getDeclaredMethods()) {
						if(ranMain) {
							throw new IllegalStateException("Found more than one main methods from plugin: " + pluginNames[k]);
						}
						
						if(method.isAnnotationPresent(FruttyPluginMain.class)) {
							if((method.getModifiers() & Modifier.STATIC) != 0 || method.getParameterCount() > 0) {
								var eventClass = method.getAnnotation(FruttyPluginMain.class).eventClass();
										
								if(eventClass != void.class) {
									var eventMethods = eventClass.getDeclaredMethods();
									var lookup = MethodHandles.publicLookup();
									
									for(var eventMts : eventMethods) {
										if(eventMts.isAnnotationPresent(FruttyEventHandler.class)) {
											if((eventMts.getModifiers() & Modifier.STATIC) != 0 && eventMts.getParameterCount() == 1) {
												var eventTypeClass = eventMts.getParameterTypes()[0];
												
												if(!eventTypeClass.isAnnotationPresent(FruttyEvent.class)) {
													throw new IllegalArgumentException("Illegal type of argument for method: " + eventMts.getName());
												}
												
												EventHandle.addEvent(lookup, eventMts, eventTypeClass);
											}else {
												throw new IllegalStateException("Method from class: " + eventClass + ", methodName: " + eventMts.getName() + " is not static or has more than 1 parameters");
											}
										}
									}
								}
								
								method.invoke(null);
								ranMain = true;
								break;
							}
							throw new IllegalStateException("Main method from plugin: " + pluginNames[k] + " is not static or has method arguments");
						}
					}
					
					if(!ranMain) {
						System.err.println("Can't find main method annotated with @FruttyPluginMain from plugin: " + pluginNames[k] + ", ignoring");
					}
				}
			} catch (SecurityException | IllegalArgumentException | ClassNotFoundException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}
}