package frutty.gui;

import frutty.*;
import frutty.gui.components.*;
import frutty.tools.*;
import frutty.world.*;
import javax.swing.*;

public final class GuiSettings {
    private GuiSettings() {}

    private static GuiWorldBackground createPanel(WorldZone[] zones, int[] xCoords, int[] yCoords, Material[] materials, JComponent...components) {
        var panel = new GuiWorldBackground(zones, xCoords, yCoords, materials);
        panel.setLayout(null);

        for(var comp : components) panel.add(comp);

        panel.add(GuiHelper.newButton("Save", 371, 525, e -> saveSettings()));

        return panel;
    }

    private static void saveSettings() {
        System.out.println(Main.ioSystemLabel + "Saving settings");

        Settings.settingProperties.setBoolean("enableCollisionDebug", Settings.enableCollisionDebug = collisionBoxButton.optionIndex == 1);
        Settings.settingProperties.setInt("graphics", Settings.graphicsLevel = graphicsLevelButton.optionIndex);
        Settings.settingProperties.setInt("fps", Settings.fps = tenToHundred[fpsSlider.counter - 1]);
        Settings.settingProperties.setInt("difficulty", Settings.difficulty = difficultyButton.optionIndex);
        Settings.settingProperties.setInt("upKey", Settings.upKey = upKeyButtonField.dataField.getText().charAt(0));
        Settings.settingProperties.setInt("downKey", Settings.downKey = downKeyButtonField.dataField.getText().charAt(0));
        Settings.settingProperties.setInt("leftKey", Settings.leftKey = leftKeyButtonField.dataField.getText().charAt(0));
        Settings.settingProperties.setInt("rightKey", Settings.rightKey = rightKeyButtonField.dataField.getText().charAt(0));
        Settings.settingProperties.setBoolean("enableSound", Settings.enableSound = enableSoundButton.optionIndex == 1);
        Settings.settingProperties.setInt("volume", Settings.volume = volumeSlider.counter);
        Settings.settingProperties.setString("screenshotFormat", Settings.screenshotFormat = screenshotFormats[screenshotFormatButton.optionIndex]);
        Settings.settingProperties.save();
        GuiMenu.switchMenuGui(GuiMenu.createMenuPanel());
    }

    public static void showGuiSettings() {
        System.out.println(Main.guiSystemLabel + "Switching to settings frame");

        var zones = new WorldZone[140];
        var xCoords = new int[140];
        var yCoords = new int[140];
        var materials = new Material[140];
        GuiWorldBackground.loadBackgroundWorld("dev_settings" + GamePaths.WORLD_FILE_EXTENSION, zones, xCoords, yCoords, materials);

        var tabbed = new JTabbedPane();
        tabbed.addTab("Gameplay", createPanel(zones, xCoords, yCoords, materials, difficultyButton, upKeyButtonField, downKeyButtonField, leftKeyButtonField, rightKeyButtonField));
        tabbed.addTab("Graphics", createPanel(zones, xCoords, yCoords, materials, graphicsLevelButton, fpsSlider, screenshotFormatButton));
        tabbed.addTab("Sound", createPanel(zones, xCoords, yCoords, materials, enableSoundButton, volumeSlider));

        var insets = UIManager.getInsets("TabbedPane.contentBorderInsets");
        insets.left = -1;
        insets.right = -1;
        insets.bottom = -1;
        UIManager.put("TabbedPane.contentBorderInsets", insets);

        GuiMenu.switchMenuGui(tabbed);
    }

    private static int indexOfInt(int value, int[] values) {
        for(var k = 0; k < values.length; ++k) {
            if(values[k] == value) {
                return k;
            }
        }
        throw new IllegalArgumentException("Should not get there...");
    }

    private static final SettingButton difficultyButton = new SettingButton(Settings.difficulty, "Difficulty", 100, 20, "Easy", "Normal", "Hard");
    private static final SettingButtonField upKeyButtonField = new SettingButtonField(Settings.upKey, "Second Player Upwards Key", 100, 100);
    private static final SettingButtonField downKeyButtonField = new SettingButtonField(Settings.downKey, "Second Player Downwards Key", 100, 180);
    private static final SettingButtonField leftKeyButtonField = new SettingButtonField(Settings.leftKey, "Second Player Left Key", 100, 260);
    private static final SettingButtonField rightKeyButtonField = new SettingButtonField(Settings.rightKey, "Second Player Right Key", 100, 340);

    private static final SettingButton graphicsLevelButton = new SettingButton(Settings.graphicsLevel, "Graphics Level", 100, 20, "Low", "Medium", "High");
    private static final int[] tenToHundred = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
    private static final SettingButtonSlider fpsSlider = new SettingButtonSlider(indexOfInt(Settings.fps, tenToHundred) + 1, "FPS", 100, 100);
    private static final String[] screenshotFormats = { "JPG", "PNG" };
    private static final SettingButton screenshotFormatButton = new SettingButton(Settings.screenshotFormat, "Screenshot Format", 100, 180, screenshotFormats);

    private static final SettingButton collisionBoxButton = new SettingButton(Settings.enableCollisionDebug, "Draw Collision Boxes", 100, 260);

    private static final SettingButton enableSoundButton = new SettingButton(Settings.enableSound, "Enable Sound", 100, 20);
    private static final SettingButtonSlider volumeSlider = new SettingButtonSlider(Settings.volume, "Volume", 100, 100);

    //Separate class to avoid loading settings background world and loads of Button objects
    public static final class Settings{
        static final PropertyFile settingProperties = new PropertyFile("settings.prop", 14);

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
    }
}