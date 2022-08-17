package frutty;

import static frutty.Main.*;
import static java.util.Map.*;

import frutty.gui.*;
import frutty.tools.*;
import frutty.world.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;

public final class ConsoleCommands {

    public static final Map<String, Consumer<String[]>> commands = Map.ofEntries(entry("god", ConsoleCommands::handleGodMode),
                                                                                 entry("set_debug_render_level", ConsoleCommands::handleDebugRenderLevel),
                                                                                 entry("toggle_debug_world_info", ConsoleCommands::handleDebugWorldInfo),
                                                                                 entry("toggle_debug_entity_collision", ConsoleCommands::handleDebugEntityCollision),
                                                                                 entry("toggle_debug_entity_pathfinding", ConsoleCommands::handleDebugEntityPathfinding),
                                                                                 entry("commands", ConsoleCommands::handleCommandListing),
                                                                                 entry("clear", ConsoleCommands::handleClearConsole),
                                                                                 entry("quit", e -> System.exit(0)),
                                                                                 entry("world", ConsoleCommands::handleWorldSwitch),
                                                                                 entry("worlds", ConsoleCommands::handleWorldListing));
    private static void handleDebugRenderLevel(String[] args) {
        if(args.length == 1) {
            System.out.println(userConLabel + "No render level was given as an argument (0-3)");
            return;
        }

        var newLevel = Integer.parseInt(args[1]);

        if(rangeCheck(newLevel, 0, 3)) {
            Settings.renderDebugLevel = newLevel;
            System.out.println(userConLabel + "Render debug level set to " + renderDebugLevelToString(newLevel));
        }
    }

    private static void handleDebugWorldInfo(@SuppressWarnings("unused") String[] args) {
        Settings.enableWorldDebug = !Settings.enableWorldDebug;
        System.out.println(userConLabel + "World info debug " + flagToString(Settings.enableWorldDebug));
    }

    private static void handleDebugEntityCollision(@SuppressWarnings("unused") String[] args) {
        Settings.enableCollisionDebug = !Settings.enableCollisionDebug;
        System.out.println(userConLabel + "Entity collision debug " + flagToString(Settings.enableCollisionDebug));
    }

    private static void handleDebugEntityPathfinding(@SuppressWarnings("unused") String[] args) {
        Settings.enablePathfindingDebug = !Settings.enablePathfindingDebug;
        System.out.println(userConLabel + "Entity pathfinding debug " + flagToString(Settings.enablePathfindingDebug));
    }



    private static void handleGodMode(@SuppressWarnings("unused") String[] args) {
        Settings.godMode = !Settings.godMode;
        System.out.println(userConLabel + "God mode " + flagToString(Settings.godMode));
    }


    private static void handleClearConsole(@SuppressWarnings("unused") String[] args) {
        Main.console.setText(null);
    }

    private static void handleCommandListing(@SuppressWarnings("unused") String[] args) {
        commands.keySet().stream()
                .sorted()
                .forEach(k -> System.out.println(userConLabel + "    " + k));
    }

    private static void handleWorldSwitch(String[] args) {
        if(args.length == 1) {
            System.out.println(userConLabel + "No world name was given as an argument");
            return;
        }

        var worldName = args[1];

        if(!Files.exists(Path.of(GamePaths.WORLDS_DIR + worldName + GamePaths.WORLD_FILE_EXTENSION))) {
            System.out.println(userConLabel + "Unable to find world named '" + worldName + "'");
            return;
        }

        if(GuiIngame.ingameFrame == null) {
            GuiMainMenu.closeMainFrame();
        }else{
            GuiIngame.shutdown();
            GuiIngame.ingameFrame.dispose();
            World.cleanUp();
        }

        World.load(worldName, false);
        GuiIngame.showIngame();
    }

    private static void handleWorldListing(@SuppressWarnings("unused") String[] args) {
        try(var worldFiles = Files.list(Path.of(GamePaths.WORLDS_DIR))) {
            worldFiles.map(k -> k.toString())
                      .filter(k -> k.endsWith(GamePaths.WORLD_FILE_EXTENSION))
                      .forEach(k -> System.out.println(userConLabel + "    " + k.replace('\\', '/')));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private static boolean rangeCheck(int value, int min, int max) {
        var success = value >= min && value <= max;

        if(!success) {
            System.out.println(userConLabel + "Value out of range, expected: " + min + "-" + max);
        }

        return success;
    }

    private static String flagToString(boolean flag) {
        return flag ? "enabled" : "disabled";
    }

    private static String renderDebugLevelToString(int level) {
        return level == 0 ? "None" : level == 1 ? "FPS Debug" : level == 2 ? "Zone Bounds" : "All";
    }

    private ConsoleCommands() {}
}