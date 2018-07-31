package frutty.plugin.internal;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import frutty.Main;
import frutty.plugin.FruttyEvent;
import frutty.plugin.FruttyPlugin;
import frutty.plugin.FruttyPluginMain;
import frutty.plugin.event.MapInitEvent;
import frutty.plugin.event.gui.GuiMenuEvent;
import frutty.plugin.event.gui.GuiStatInitEvent;
import frutty.plugin.event.gui.GuiStatSavedEvent;
import frutty.tools.Version;

public final class Plugin{
	public static ArrayList<Plugin> plugins = Main.toList(new Plugin("Frutty", "Base module for the game.", "", Version.from(1, 4, 0), "https://pastebin.com/raw/m5qJbnks"),
			 new Plugin("Frutty Plugin Loader", "Base module for the plugin loader", "", Version.from(1, 0, 0), ""));
	
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
	
	public static void handleEventTypes(Lookup lookup, Method eventMts, Class<?> eventTypeClass) throws IllegalAccessException {
		var handle = new EventHandle(lookup.unreflect(eventMts), eventMts.getAnnotation(FruttyEvent.class).priority());
		
		if(eventTypeClass == MapInitEvent.class) {
			EventHandle.mapLoadEvents.add(handle);
		}else if(eventTypeClass == GuiMenuEvent.class) {
			EventHandle.menuInitEvents.add(handle);
		}else if(eventTypeClass == GuiStatInitEvent.class) {
			EventHandle.statInitEvents.add(handle);
		}else if(eventTypeClass == GuiStatSavedEvent.class) {
			EventHandle.statSaveEvents.add(handle);
		}
	}
	
	
	public static void handlePluginInit() {
		var pluginPath = Paths.get("plugins");
		if(!Files.exists(pluginPath)) {
			try {
				Files.createDirectory(pluginPath);
			} catch (IOException e) {}
		}
		
		if(Main.hasPlugins) {
			loadPlugins();
			
			if(!EventHandle.mapLoadEvents.isEmpty()) {
				EventHandle.mapLoadEvents.sort(EventHandle.byPriority);
			}
			
			if(!EventHandle.menuInitEvents.isEmpty()) {
				EventHandle.menuInitEvents.sort(EventHandle.byPriority);
			}
			
			if(!EventHandle.statInitEvents.isEmpty()) {
				EventHandle.statInitEvents.sort(EventHandle.byPriority);
			}
		}
	}
	
	public static void loadPlugins() {
		File[] pluginNames = new File("./plugins/").listFiles((dir, name) -> name.endsWith(".jar"));
		
		if(pluginNames.length > 0) {
			String[] mainClassNames = new String[pluginNames.length];
			URL[] classLoaderNames = new URL[pluginNames.length];
			
			for(int k = 0; k < pluginNames.length; ++k) {
				try {
					classLoaderNames[k] = pluginNames[k].toURI().toURL();
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				}
				
				try(JarFile jar = new JarFile(pluginNames[k])){
					Manifest mani = jar.getManifest();
					if(mani == null) {
						throw new IllegalStateException("Can't find manifest file from plugin: " + pluginNames[k]);
					}
					
					String pluginClass = mani.getMainAttributes().getValue("Plugin-Class");
					if(pluginClass == null) {
						throw new IllegalStateException("Can't find \"Plugin-Class\" attribute from plugin: " + pluginNames[k]);
					}
					mainClassNames[k] = pluginClass;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			try(URLClassLoader urlClass = new URLClassLoader(classLoaderNames, Main.class.getClassLoader())){
				for(int k = 0; k < mainClassNames.length; ++k) {
					if(mainClassNames[k] == null) {
						throw new IllegalStateException("Can't load main class from plugin: " + pluginNames[k]);
					}
					Class<?> loaded = urlClass.loadClass(mainClassNames[k]);
					if(!loaded.isAnnotationPresent(FruttyPlugin.class)) {
						throw new IllegalStateException("Main class from plugin: " + pluginNames[k] + " is not annotated with @FruttyPlugin");
					}
					
					FruttyPlugin pluginAnnotation = loaded.getDeclaredAnnotation(FruttyPlugin.class);
					plugins.add(new Plugin(pluginAnnotation.name(), pluginAnnotation.description(), pluginAnnotation.updateURL(), Version.fromString(pluginAnnotation.version()), pluginAnnotation.versionURL()));
					plugins.trimToSize();
					
					Method[] methods = loaded.getDeclaredMethods();
					boolean ranMain = false;
					for(Method method : methods) {
						if(ranMain) {
							throw new IllegalStateException("Found more than one main methods from plugin: " + pluginNames[k]);
						}
						if(method.isAnnotationPresent(FruttyPluginMain.class)) {
							if((method.getModifiers() & Modifier.STATIC) != 0 || method.getParameterCount() > 0) {
								Class<?> eventClass = method.getAnnotation(FruttyPluginMain.class).eventClass();
										
								if(eventClass != void.class) {
									Method[] eventMethods = eventClass.getDeclaredMethods();
									
									Lookup lookup = MethodHandles.publicLookup();
									
									for(Method eventMts : eventMethods) {
										if(eventMts.isAnnotationPresent(FruttyEvent.class)) {
											if((eventMts.getModifiers() & Modifier.STATIC) != 0 && eventMts.getParameterCount() == 1) {
												Class<?> eventTypeClass = eventMts.getParameterTypes()[0];
												
												if(eventTypeClass.getSuperclass() != EventBase.class) {
													throw new IllegalArgumentException("Illegal type of argument for method: " + eventMts.getName());
												}
												
												Plugin.handleEventTypes(lookup, eventMts, eventTypeClass);
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
			} catch (IOException | SecurityException | IllegalArgumentException | ClassNotFoundException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
}