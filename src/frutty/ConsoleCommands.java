package frutty;

import static frutty.Main.*;

import frutty.tools.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public final class ConsoleCommands {

    public static final Map<String, Consumer<String[]>> commands = Map.of("god", ConsoleCommands::handleGodMode,
                                                                          "set_debug_render_level", ConsoleCommands::handleDebugRenderLevel,
                                                                          "toggle_debug_world_info", ConsoleCommands::handleDebugWorldInfo,
                                                                          "toggle_debug_entity_collision", ConsoleCommands::handleDebugEntityCollision,
                                                                          "toggle_debug_entity_pathfinding", ConsoleCommands::handleDebugEntityPathfinding,
                                                                          "list", ConsoleCommands::handleCommandListing,
                                                                          "commands", ConsoleCommands::handleCommandListing,
                                                                          "clear", ConsoleCommands::handleClearConsole,
                                                                          "quit", e -> System.exit(0));
    private static void handleDebugRenderLevel(String[] args) {
        if(args.length == 1) {
            System.out.println(userConLabel + "No render level was given as an argument (0-3)");
        }else {
            var newLevel = Integer.parseInt(args[1]);

            if(rangeCheck(newLevel, 0, 3)){
                Settings.renderDebugLevel = newLevel;
                System.out.println(userConLabel + "Render debug level set to " + renderLevelToString(newLevel));
            }
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
        var commandsFormatted = commands.keySet().stream()
                                        .collect(chunking(10))
                                        .stream()
                                        .map(k -> String.join(", ", k) + ",\n")
                                        .collect(Collectors.joining());

        System.out.println(userConLabel + "Commands: " + commandsFormatted);
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

    private static String renderLevelToString(int level) {
        return level == 0 ? "None" : level == 1 ? "FPS Debug" : level == 2 ? "Zone Bounds" : "All";
    }

    private static<T> Collector<T, ?, ArrayList<ArrayList<T>>> chunking(int chunkSize) {
        return Collector.of(() -> {
            var empty = new ArrayList<ArrayList<T>>();
            empty.add(new ArrayList<>(chunkSize));
            return empty;
        }, (global, next) -> {
            var lastList = global.get(global.size() - 1);

            if(lastList.size() == chunkSize) {
                var newList = new ArrayList<T>(chunkSize);

                newList.add(next);
                global.add(newList);
            }else{
                lastList.add(next);
            }
        }, (l, r) -> {
            l.addAll(r);
            return l;
        },  Collector.Characteristics.UNORDERED);
    }

    private ConsoleCommands() {}
}