package frutty.gui;

import frutty.*;
import frutty.gui.components.*;
import frutty.tools.*;
import frutty.world.*;
import javax.swing.*;

public final class GuiSettings {
    private GuiSettings() {}

    private static GuiWorldBackground createSettingPanel(WorldData settingsWorldData, JComponent...components) {
        var panel = new GuiWorldBackground(settingsWorldData);
        panel.setLayout(null);

        for(var comp : components) panel.add(comp);

        panel.add(GuiHelper.newButton("Save", 371, 528, e -> saveSettings()));

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

        var settingsWorldData = WorldData.load("dev_settings", false, false);

        var settingsTabs = new JTabbedPane();
        settingsTabs.addTab("Gameplay", createSettingPanel(settingsWorldData, difficultyButton, upKeyButtonField, downKeyButtonField, leftKeyButtonField, rightKeyButtonField));
        settingsTabs.addTab("Graphics", createSettingPanel(settingsWorldData, graphicsLevelButton, fpsSlider, screenshotFormatButton));
        settingsTabs.addTab("Sound", createSettingPanel(settingsWorldData, enableSoundButton, volumeSlider));

        var insets = UIManager.getInsets("TabbedPane.contentBorderInsets");
        insets.left = -1;
        insets.right = -1;
        insets.bottom = -1;
        insets.top = -1;
        UIManager.put("TabbedPane.contentBorderInsets", insets);

        GuiMenu.switchMenuGui(settingsTabs);
    }

    private static int indexOfInt(int value, int[] values) {
        for(var k = 0; k < values.length; ++k) {
            if(values[k] == value) {
                return k;
            }
        }
        throw new IllegalArgumentException("Should not get there...");
    }

    private static final SettingOptionInput difficultyButton = new SettingOptionInput(Settings.difficulty, "Difficulty", 100, 20, "Easy", "Normal", "Hard");
    private static final SettingFieldInput upKeyButtonField = new SettingFieldInput(Settings.upKey, "Second Player Upwards Key", 100, 100);
    private static final SettingFieldInput downKeyButtonField = new SettingFieldInput(Settings.downKey, "Second Player Downwards Key", 100, 180);
    private static final SettingFieldInput leftKeyButtonField = new SettingFieldInput(Settings.leftKey, "Second Player Left Key", 100, 260);
    private static final SettingFieldInput rightKeyButtonField = new SettingFieldInput(Settings.rightKey, "Second Player Right Key", 100, 340);

    private static final SettingOptionInput graphicsLevelButton = new SettingOptionInput(Settings.graphicsLevel, "Graphics Level", 100, 20, "Low", "Medium", "High");
    private static final int[] tenToHundred = { 10, 20, 30, 40, 50, 60, 70, 80, 90, 100 };
    private static final SettingSliderInput fpsSlider = new SettingSliderInput(indexOfInt(Settings.fps, tenToHundred) + 1, "FPS", 100, 100);
    private static final String[] screenshotFormats = { "JPG", "PNG" };
    private static final SettingOptionInput screenshotFormatButton = new SettingOptionInput(Settings.screenshotFormat, "Screenshot Format", 100, 180, screenshotFormats);

    private static final SettingOptionInput collisionBoxButton = new SettingOptionInput(Settings.enableCollisionDebug, "Draw Collision Boxes", 100, 260);

    private static final SettingOptionInput enableSoundButton = new SettingOptionInput(Settings.enableSound, "Enable Sound", 100, 20);
    private static final SettingSliderInput volumeSlider = new SettingSliderInput(Settings.volume, "Volume", 100, 100);
}