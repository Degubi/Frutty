package frutty.tools;

import java.nio.file.*;

public final class GamePaths {
    public static final String WORK_DIR = Files.exists(Path.of("app")) ? "app/" : "";

    public static final String SAVES_DIR = WORK_DIR + "saves/";
    public static final String SOUNDS_DIR = WORK_DIR + "sounds/";
    public static final String TEXTURES_DIR = WORK_DIR + "textures/";
    public static final String WORLDS_DIR = WORK_DIR + "worlds/";

    public static final String WORLD_FILE_EXTENSION = ".fwrld";

    private GamePaths() {}
}