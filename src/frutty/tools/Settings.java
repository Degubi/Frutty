package frutty.tools;

public final class Settings {
    public static final PropertyFile settingProperties = new PropertyFile("settings.prop", 14);

    public static int fps = settingProperties.getInt("fps", 50);
    public static int difficulty = settingProperties.getInt("difficulty", 0);
    public static int graphicsLevel = settingProperties.getInt("graphics", 2);
    public static int renderDebugLevel = 0;
    public static int upKey = settingProperties.getInt("upKey", 'W');
    public static int downKey = settingProperties.getInt("downKey", 'S');
    public static int leftKey = settingProperties.getInt("leftKey", 'A');
    public static int rightKey = settingProperties.getInt("rightKey", 'D');
    public static boolean godMode = false;
    public static boolean enableCollisionDebug = false;
    public static boolean enablePathfindingDebug = false;
    public static boolean enableWorldDebug = false;
    public static boolean enableSound = settingProperties.getBoolean("enableSound", true);
    public static int volume = settingProperties.getInt("volume", 6);
    public static String screenshotFormat = settingProperties.getString("screenshotFormat", "JPG");

    private Settings() {}
}