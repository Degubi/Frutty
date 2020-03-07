package frutty;

import frutty.gui.*;
import frutty.plugin.*;
import frutty.plugin.event.entity.*;
import frutty.plugin.event.gui.*;
import frutty.plugin.event.stats.*;
import frutty.plugin.event.world.*;
import java.io.*;
import java.lang.invoke.*;
import java.lang.invoke.MethodHandles.*;
import java.lang.reflect.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.jar.*;
import java.util.stream.*;
import javax.swing.*;

public final class Main {
    private static final EventHandle[] sharedEmptyEventArray = new EventHandle[0];
    public static final Random rand = new Random();
    public static final Plugin[] loadedPlugins;
    
    public static final EventHandle[] worldInitEvents;
    public static final EventHandle[] menuInitEvents;
    public static final EventHandle[] statInitEvents;
    public static final EventHandle[] statSaveEvents;
    public static final EventHandle[] zoneAddedEvents;
    public static final EventHandle[] entityKilledEvents;
    
    static {
        createDirectory("plugins");
        
        var plugins = new ArrayList<Plugin>();
        plugins.add(new Plugin("Frutty", "Base module for the game.", "https://github.com/Degubi/Frutty", "1.5.0"));
        plugins.add(new Plugin("Frutty Plugin Loader", "Base module for the plugin loader", "", "1.0.0"));
        
        var loadedEvents = loadPlugins(plugins);
        var byPriority = Comparator.comparingInt((EventHandle event) -> event.priority);

        loadedPlugins = plugins.toArray(Plugin[]::new);
        worldInitEvents = initEventsByType(WorldInitEvent.class, loadedEvents, byPriority);
        menuInitEvents = initEventsByType(GuiMenuEvent.class, loadedEvents, byPriority);
        statInitEvents = initEventsByType(StatsInitEvent.class, loadedEvents, byPriority);
        statSaveEvents = initEventsByType(StatsSavedEvent.class, loadedEvents, byPriority);
        zoneAddedEvents = initEventsByType(ZoneAddedEvent.class, loadedEvents, byPriority);
        entityKilledEvents = initEventsByType(EntityKilledEvent.class, loadedEvents, byPriority);
    }
    
    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        GuiMenu.createMainFrame();
        createDirectory("saves");
        createDirectory("screenshots");
    }
    
    private static void createDirectory(String path) {
        var filePath = Path.of(path);
        
        if(!Files.exists(filePath)) {
            try {
                Files.createDirectory(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void invokeEvent(Object event, EventHandle[] methods) {
        for(var handles : methods) {
            try {
                handles.handle.bindTo(event).invokeExact();
            } catch (Throwable e) {
                e.printStackTrace();
            }
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
    private static Map<Class<?>, List<EventHandle>> loadPlugins(ArrayList<Plugin> outPlugins) {
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

                    var loadedMainClass = classLoader.loadClass(mainClasses[k]);
                    if(!loadedMainClass.isAnnotationPresent(FruttyPlugin.class)) {
                        throw new IllegalStateException("Main class from plugin: " + pluginJars[k] + " is not annotated with @FruttyPlugin");
                    }

                    var pluginAnnotation = loadedMainClass.getDeclaredAnnotation(FruttyPlugin.class);
                    outPlugins.add(new Plugin(pluginAnnotation.name(), pluginAnnotation.description(), pluginAnnotation.pluginSiteURL(), pluginAnnotation.version()));

                    var pluginMainFunctions = Arrays.stream(loadedMainClass.getDeclaredMethods())
                                                    .filter(method -> method.isAnnotationPresent(FruttyMain.class))
                                                    .toArray(Method[]::new);

                    if(pluginMainFunctions.length == 0) {
                        System.err.println("Can't find main method annotated with @FruttyPluginMain from plugin: " + pluginJars[k] + ", ignoring");
                    }else if(pluginMainFunctions.length > 1) {
                        throw new IllegalStateException("Found more than one main methods from plugin: " + pluginJars[k]);
                    }else{
                        lookup.unreflect(pluginMainFunctions[0]).invokeExact();

                        var eventClass = pluginMainFunctions[0].getAnnotation(FruttyMain.class).eventClass();
                        if(eventClass != void.class) {
                            return Arrays.stream(eventClass.getDeclaredMethods())
                                         .filter(eventMethod -> eventMethod.isAnnotationPresent(FruttyEvent.class))
                                         .filter(eventMethod -> eventMethod.getParameterTypes()[0].isAnnotationPresent(FruttyEventMarker.class))
                                         .collect(Collectors.groupingBy(eventMethod -> eventMethod.getParameterTypes()[0],
                                                  Collectors.mapping(eventMethod -> new EventHandle(unreflectEventMethod(lookup, eventMethod), eventMethod.getAnnotation(FruttyEvent.class).priority()), Collectors.toList())));
                        }
                    }
                }
            }
        } catch (Throwable e) {
            throw new IllegalStateException("Something is fucked in plugin loading again...");
        }
        
        return Map.of();
    }
    
    private static EventHandle[] initEventsByType(Class<?> eventClass, Map<Class<?>, List<EventHandle>> loadedEvents, Comparator<EventHandle> byPriority) {
        var rawEvents = loadedEvents.get(eventClass);
        
        if(rawEvents != null) {
            rawEvents.sort(byPriority);
            return rawEvents.toArray(EventHandle[]::new);
        }
        
        return sharedEmptyEventArray;
    }
    
    private static MethodHandle unreflectEventMethod(Lookup lookup, Method eventMethod) {
        try {
            return lookup.unreflect(eventMethod);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Unable to access event method: " + eventMethod.getName() + " in class: " + eventMethod.getDeclaringClass());
        }
    }
    
    private Main() {}
}