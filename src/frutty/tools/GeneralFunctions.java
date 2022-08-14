package frutty.tools;

import java.nio.file.*;

public final class GeneralFunctions {
    public static final String WORK_DIR = Files.exists(Path.of("app")) ? "app/" : "";

    private GeneralFunctions() {}
}