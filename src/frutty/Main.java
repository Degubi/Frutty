package frutty;

import frutty.gui.*;
import frutty.plugin.*;
import frutty.plugin.event.entity.*;
import frutty.plugin.event.gui.*;
import frutty.plugin.event.stats.*;
import frutty.plugin.event.world.*;
import frutty.tools.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.invoke.*;
import java.lang.reflect.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import java.util.jar.*;
import java.util.stream.*;
import javax.swing.*;
import javax.swing.text.*;

@SuppressWarnings("resource")
public final class Main extends KeyAdapter {
    private static final EventHandle[] sharedEmptyEventArray = new EventHandle[0];
    public static final Random rand = new Random();
    public static Plugin[] loadedPlugins;
    
    public static EventHandle[] worldInitEvents;
    public static EventHandle[] menuInitEvents;
    public static EventHandle[] statInitEvents;
    public static EventHandle[] statSaveEvents;
    public static EventHandle[] zoneAddedEvents;
    public static EventHandle[] entityKilledEvents;
    
    private static final ArrayList<String> commandHistory = new ArrayList<>();
    private static final JTextField commandField = new JTextField();
    private static final JTextPane console = new JTextPane();
    
    public static final String pluginSystemLabel       = "          Plugin System                  ;   ";
    public static final String userConLabel            = "          User Console System    ;   ";
    public static final String guiSystemLabel          = "          GUI System                       ;   ";
    public static final String worldLoadingSystemLabel = "          World Loading System   ;   ";
    public static final String eventSystemLabel        = "          Event System                    ;   ";
    public static final String ioSystemLabel           = "          IO System                          ;   ";
    public static final String updateSystemLabel       = "          Update System                 ;   ";
    public static final String renderSystemLabel       = "          Render System                 ;   ";

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        
        GeneralFunctions.executionDir = containsArg("-dev", args) ? "" : System.getProperty("user.dir") + "/app/";
        
        if(containsArg("-console", args)) {
            var outPipe = new PipedOutputStream();
            var reader = new BufferedReader(new InputStreamReader(new ModifiedPipedInputStream(outPipe)));
            System.setOut(new PrintStream(outPipe, true));
                
            new Thread(() -> {
                try {
                    var threadOutputFormat = new SimpleAttributeSet();
                    var messageOutputFormat = new SimpleAttributeSet();
                    
                    threadOutputFormat.addAttribute(StyleConstants.Background, Color.BLACK);
                    threadOutputFormat.addAttribute(StyleConstants.Foreground, Color.WHITE);
                    messageOutputFormat.addAttribute(StyleConstants.Foreground, Color.WHITE);
                    
                    for(String line; (line = reader.readLine()) != null; ) {
                        var doc = console.getStyledDocument();
                        var separatorIndex = line.indexOf(';');
                        
                        if(separatorIndex != -1) {
                            doc.insertString(doc.getLength(), line.substring(0, separatorIndex), threadOutputFormat);
                            doc.insertString(doc.getLength(), line.substring(separatorIndex + 1) + '\n', messageOutputFormat);
                        }else{
                            doc.insertString(doc.getLength(), line, messageOutputFormat);
                        }
                    }
                } catch (IOException | BadLocationException e) {
                    e.printStackTrace();
                }
            }, "Console Thread").start();
            
            var consoleScrollPane = new JScrollPane(console);
            var scrollBar = consoleScrollPane.getVerticalScrollBar();
            scrollBar.addAdjustmentListener(new ScrollListener(scrollBar));
            
            consoleScrollPane.setBorder(null);
            commandField.setPreferredSize(new Dimension(0, 25));
            commandField.addKeyListener(new Main());
            
            var content = new JSplitPane(JSplitPane.VERTICAL_SPLIT, consoleScrollPane, commandField);
            content.setDividerSize(0);
            content.setResizeWeight(1D);
            console.setBackground(Color.DARK_GRAY);
            console.setEditable(false);
            
            var frame = new JFrame("Frutty Console");
            frame.setContentPane(content);
            frame.setSize(1024, 768);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }
        
        createDirectory("plugins");
        
        System.out.println(pluginSystemLabel + "Launching plugin system");
        var plugins = new ArrayList<Plugin>();
        plugins.add(new Plugin("Frutty", "Base module for the game.", "https://github.com/Degubi/Frutty", "1.0.0"));
        plugins.add(new Plugin("Frutty Plugin Loader", "Base module for the plugin loader", "", "1.0.0"));
        
        var loadedEvents = loadPlugins(plugins);
        var byPriority = Comparator.comparingInt((EventHandle event) -> event.priority);

        Main.loadedPlugins = plugins.toArray(Plugin[]::new);
        Main.worldInitEvents = initEventsByType(WorldInitEvent.class, loadedEvents, byPriority);
        Main.menuInitEvents = initEventsByType(GuiMenuEvent.class, loadedEvents, byPriority);
        Main.statInitEvents = initEventsByType(StatsInitEvent.class, loadedEvents, byPriority);
        Main.statSaveEvents = initEventsByType(StatsSavedEvent.class, loadedEvents, byPriority);
        Main.zoneAddedEvents = initEventsByType(ZoneAddedEvent.class, loadedEvents, byPriority);
        Main.entityKilledEvents = initEventsByType(EntityKilledEvent.class, loadedEvents, byPriority);
        System.out.println(pluginSystemLabel + "Finished loading, loaded " + (plugins.size() - 2) + " external plugins");
        
        GuiMenu.createMainFrame();
        createDirectory("saves");
        createDirectory("screenshots");
    }
    
    private static boolean containsArg(String arg, String[] args) {
        for(var arrrg : args) {
            if(arrrg.equals(arg)) {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public void keyPressed(KeyEvent event) {
        var pressedKey = event.getKeyCode();
        
        if(pressedKey == KeyEvent.VK_ENTER) {
            var command = commandField.getText();
            
            if(!command.isBlank()) {
                var args = command.split(" ");
                var maybeCommand = ConsoleCommands.commands.get(args[0]);
                        
                if(maybeCommand != null) {
                    maybeCommand.accept(args);
                }else{
                    System.out.println(userConLabel + "Unknown command: " + args[0]);
                }
                
                commandHistory.add(command);
                commandField.setText(null);
            }
        }else if(pressedKey == KeyEvent.VK_UP) {
            var textToSet = commandHistory.size() > 0 ? commandHistory.remove(commandHistory.size() - 1)
                                                      : null;
            commandField.setText(textToSet);
        }
    }
    
    private static void createDirectory(String path) {
        var filePath = Path.of(GeneralFunctions.executionDir + path);
        
        if(!Files.exists(filePath)) {
            try {
                System.out.println(ioSystemLabel + "Creating " + path + " directory");

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
                System.out.println(pluginSystemLabel + "Can't find manifest file from plugin: " + jarPath);
            }else{
                var pluginClass = mani.getMainAttributes().getValue("Plugin-Class");
                
                if(pluginClass == null) {
                    System.out.println(pluginSystemLabel + "Can't find \"Plugin-Class\" attribute from plugin: " + jarPath);
                }
                return pluginClass;
            }
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
    
    private static Map<Class<?>, List<EventHandle>> loadPlugins(ArrayList<Plugin> outPlugins) {
        System.out.println(pluginSystemLabel + "Started loading plugins");
        var eventsToReturn = new HashMap<Class<?>, List<EventHandle>>();
        
        try(var pluginFolder = Files.list(Path.of(GeneralFunctions.executionDir + "plugins"))){
            var pluginJars = pluginFolder.filter(Files::isRegularFile)
                                         .filter(file -> file.toString().endsWith(".jar"))
                                         .toArray(Path[]::new);
            
            System.out.println(pluginSystemLabel + "Found " + pluginJars.length + " potential plugins in plugins folder");
            
            if(pluginJars.length > 0) {
                var mainClasses = Arrays.stream(pluginJars).map(Main::getMainClassNameFromJar).toArray(String[]::new);
                var classLoader = new URLClassLoader(Arrays.stream(pluginJars).map(Main::convertToURL).toArray(URL[]::new), Main.class.getClassLoader()); //Dont close this or shit breaks
                var lookup = MethodHandles.publicLookup();

                for(var k = 0; k < mainClasses.length; ++k) {
                    if(mainClasses[k] == null) {
                        System.out.println(pluginSystemLabel + "Can't load main class from plugin: " + pluginJars[k]);
                        continue;
                    }

                    var loadedMainClass = classLoader.loadClass(mainClasses[k]);
                    if(!loadedMainClass.isAnnotationPresent(FruttyPlugin.class)) {
                        System.out.println(pluginSystemLabel + "Main class from plugin: " + pluginJars[k] + " is not annotated with @FruttyPlugin");
                        continue;
                    }

                    var pluginAnnotation = loadedMainClass.getDeclaredAnnotation(FruttyPlugin.class);
                    System.out.println(pluginSystemLabel + "Started loading plugin: '" + pluginAnnotation.name() + "'");
                    
                    outPlugins.add(new Plugin(pluginAnnotation.name(), pluginAnnotation.description(), pluginAnnotation.pluginSiteURL(), pluginAnnotation.version()));

                    var pluginMainFunctions = Arrays.stream(loadedMainClass.getDeclaredMethods())
                                                    .filter(method -> method.isAnnotationPresent(FruttyMain.class))
                                                    .toArray(Method[]::new);

                    if(pluginMainFunctions.length == 0) {
                        System.out.println(pluginSystemLabel + "Can't find main method annotated with @FruttyPluginMain from plugin: " + pluginJars[k] + ", ignoring");
                    }else if(pluginMainFunctions.length > 1) {
                        System.out.println(pluginSystemLabel + "Found more than one main methods from plugin: " + pluginJars[k]);
                    }else{
                        lookup.unreflect(pluginMainFunctions[0]).invokeExact();

                        var eventClass = pluginMainFunctions[0].getAnnotation(FruttyMain.class).eventClass();
                        if(eventClass != void.class) {
                            var events = Arrays.stream(eventClass.getDeclaredMethods())
                                               .filter(eventMethod -> eventMethod.isAnnotationPresent(FruttyEvent.class))
                                               .filter(eventMethod -> eventMethod.getParameterTypes()[0].isAnnotationPresent(FruttyEventMarker.class))
                                               .collect(Collectors.groupingBy(eventMethod -> eventMethod.getParameterTypes()[0],
                                                                              Collectors.mapping(eventMethod -> new EventHandle(lookup, eventMethod), Collectors.toList())));
                            
                            eventsToReturn.putAll(events);
                            
                            System.out.println(pluginSystemLabel + "Loaded plugin with " + events.size() + " registered events");
                        }else{
                            System.out.println(pluginSystemLabel + "Loaded plugin with 0 registered events");
                        }
                    }
                }
            }else {
                System.out.println(pluginSystemLabel + "No plugins to load");
            }
        } catch (Throwable e) {
            throw new IllegalStateException("Something is fucked in plugin loading again...");
        }
        
        return eventsToReturn;
    }
    
    private static EventHandle[] initEventsByType(Class<?> eventClass, Map<Class<?>, List<EventHandle>> loadedEvents, Comparator<EventHandle> byPriority) {
        var rawEvents = loadedEvents.get(eventClass);
        
        if(rawEvents != null) {
            rawEvents.sort(byPriority);
            return rawEvents.toArray(EventHandle[]::new);
        }
        
        return sharedEmptyEventArray;
    }
    
    private Main() {}
}