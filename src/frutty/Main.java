package frutty;

import frutty.gui.*;
import frutty.plugin.*;
import frutty.tools.*;
import java.io.*;
import java.lang.invoke.*;
import java.lang.reflect.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.jar.*;
import javax.swing.*;

public final class Main {
	public static final Random rand = new Random();
	public static final List<Plugin> plugins = GeneralFunctions.toMutableList(new Plugin("Frutty", "Base module for the game.", "https://github.com/Degubi/Frutty", "1.5.0"), new Plugin("Frutty Plugin Loader", "Base module for the plugin loader", "", "1.0.0"));

	private Main() {}
	
	public static void main(String[] args) throws Exception {
		createDirectory("plugins");
		loadPlugins();
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		GuiMenu.createMainFrame();
		createDirectory("saves");
		createDirectory("screenshots");
	}
	
	private static void createDirectory(String path) throws IOException {
		var filePath = Path.of(path);
		
		if(!Files.exists(filePath)) {
		    Files.createDirectory(filePath);
		}
	}
	
	private static String getMainClassNameFromJar(Path jarPath) {
        try(var jar = new JarFile(jarPath.toFile())){
            var mani = jar.getManifest();
            
            if(mani == null) {
                throw new IllegalStateException("Can't find manifest file from plugin: " + jarPath);
            }
                
            var pluginClass = mani.getMainAttributes().getValue("Plugin-Class");
            if(pluginClass == null) {
                throw new IllegalStateException("Can't find \"Plugin-Class\" attribute from plugin: " + jarPath);
            }
            return pluginClass;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private static URL convertToURL(Path file) {
        try {
            return file.toUri().toURL();
        } catch (MalformedURLException e) {
            throw new IllegalStateException();
        }
    }
    
    @SuppressWarnings("resource")
    private static void loadPlugins() {
        try(var pluginFolder = Files.list(Path.of("plugins"))){
            var pluginJars = pluginFolder.filter(Files::isRegularFile)
                                         .filter(file -> file.toString().endsWith(".jar"))
                                         .toArray(Path[]::new);
            if(pluginJars.length > 0) {
                var mainClasses = Arrays.stream(pluginJars).map(Main::getMainClassNameFromJar).toArray(String[]::new);
                var classLoader = new URLClassLoader(Arrays.stream(pluginJars).map(Main::convertToURL).toArray(URL[]::new), Main.class.getClassLoader()); //Dont close this or shit breaks
                var lookup = MethodHandles.publicLookup();

                for(var k = 0; k < mainClasses.length; ++k) {
                    if(mainClasses[k] == null) {
                        throw new IllegalStateException("Can't load main class from plugin: " + pluginJars[k]);
                    }

                    var loaded = classLoader.loadClass(mainClasses[k]);
                    if(!loaded.isAnnotationPresent(FruttyPlugin.class)) {
                        throw new IllegalStateException("Main class from plugin: " + pluginJars[k] + " is not annotated with @FruttyPlugin");
                    }

                    var pluginAnnotation = loaded.getDeclaredAnnotation(FruttyPlugin.class);
                    plugins.add(new Plugin(pluginAnnotation.name(), pluginAnnotation.description(), pluginAnnotation.pluginSiteURL(), pluginAnnotation.version()));

                    var pluginMains = Arrays.stream(loaded.getDeclaredMethods())
                                            .filter(method -> method.isAnnotationPresent(FruttyMain.class))
                                            .toArray(Method[]::new);

                    if(pluginMains.length == 0) {
                        System.err.println("Can't find main method annotated with @FruttyPluginMain from plugin: " + pluginJars[k] + ", ignoring");
                    }else if(pluginMains.length > 1) {
                        throw new IllegalStateException("Found more than one main methods from plugin: " + pluginJars[k]);
                    }else{
                        lookup.unreflect(pluginMains[0]).invokeExact();

                        var eventClass = pluginMains[0].getAnnotation(FruttyMain.class).eventClass();
                        if(eventClass != void.class) {
                            Arrays.stream(eventClass.getDeclaredMethods())
                                  .filter(eventMethod -> eventMethod.isAnnotationPresent(FruttyEvent.class))
                                  .filter(eventMethod -> eventMethod.getParameterTypes()[0].isAnnotationPresent(FruttyEventMarker.class))
                                  .forEach(eventMethod -> EventHandle.addEvent(lookup, eventMethod, eventMethod.getParameterTypes()[0]));
                        }
                    }
                }
                
                EventHandle.sortEvents();
            }
        } catch (Throwable e1) {
            throw new IllegalStateException("Something is fucked in plugin loading again...");
        }
    }
}