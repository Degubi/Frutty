package frutty.gui;

import frutty.gui.components.*;
import frutty.tools.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public final class GuiSettings extends GuiMapBackground implements ActionListener{
	private GuiSettings(JComponent... components) {
		super("./maps/dev_settings.fmap");
		setLayout(null);
		
		for(var comp : components) add(comp);
		add(GuiHelper.newButton("Save", 370, 525, this));
	}
	
	@Override
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		
		graphics.setColor(GuiHelper.color_84Black);
		graphics.fillRect(0, 0, 910, 675);
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getActionCommand().equals("Save")) {
			Settings.settingProperties.setBoolean("enableMapDebug", Settings.enableMapDebug = showMapDebugInfo.optionIndex == 1);
			Settings.settingProperties.setBoolean("enableCollisionDebug", Settings.enableCollisionDebug = collisionBoxButton.optionIndex == 1);
			Settings.settingProperties.setBoolean("enableGod", Settings.enableGod = godModeButton.optionIndex == 1);
			Settings.settingProperties.setInt("renderDebugLevel", Settings.renderDebugLevel = renderDebugLevelButton.optionIndex);
			Settings.settingProperties.setBoolean("disableEnemies", Settings.disableEnemies = disableEnemiesButton.optionIndex == 1);
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
			GuiHelper.switchMenuPanel(new GuiMenu());
		}
	}
	
	public static void showGuiSettings() {
		var tabbed = new JTabbedPane();
		tabbed.addTab("Gameplay", new GuiSettings(difficultyButton, upKeyButtonField, downKeyButtonField, leftKeyButtonField, rightKeyButtonField));
		tabbed.addTab("Graphics", new GuiSettings(graphicsLevelButton, fpsSlider, screenshotFormatButton));
		tabbed.addTab("Sound", new GuiSettings(enableSoundButton, volumeSlider));
		tabbed.addTab("Debug", new GuiSettings(renderDebugLevelButton, godModeButton, disableEnemiesButton, collisionBoxButton, showMapDebugInfo));
		
		var insets = UIManager.getInsets("TabbedPane.contentBorderInsets");
		insets.left = -1;
		insets.right = -1;
		insets.bottom = -1;
		UIManager.put("TabbedPane.contentBorderInsets", insets);
		
		GuiHelper.switchMenuPanel(tabbed);
	}
	
	private static final SettingButton difficultyButton = new SettingButton(Settings.difficulty, "Difficulty", 100, 20, "Easy", "Normal", "Hard");
	private static final SettingButtonField upKeyButtonField = new SettingButtonField(Settings.upKey, "Second Player Upwards Key", 100, 100);
	private static final SettingButtonField downKeyButtonField = new SettingButtonField(Settings.downKey, "Second Player Downwards Key", 100, 180);
	private static final SettingButtonField leftKeyButtonField = new SettingButtonField(Settings.leftKey, "Second Player Left Key", 100, 260);
	private static final SettingButtonField rightKeyButtonField = new SettingButtonField(Settings.rightKey, "Second Player Right Key", 100, 340);
	
	private static final SettingButton graphicsLevelButton = new SettingButton(Settings.graphicsLevel, "Graphics Level", 100, 20, "Low", "Medium", "High");
	private static final int[] tenToHundred = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
	private static final SettingButtonSlider fpsSlider = new SettingButtonSlider(SettingButton.indexOf(Settings.fps, tenToHundred) + 1, "FPS", 100, 100);
	private static final String[] screenshotFormats = {"JPG", "PNG"};
	private static final SettingButton screenshotFormatButton = new SettingButton(Settings.screenshotFormat, "Screenshot Format", 100, 180, screenshotFormats);
	
	private static final SettingButton renderDebugLevelButton = new SettingButton(Settings.renderDebugLevel, "Render Debug Level", 100, 20, "None", "FPS Debug", "Zone Bounds", "All");
	private static final SettingButton godModeButton = new SettingButton(Settings.enableGod, "Enable God Mode", 100, 100);
	private static final SettingButton disableEnemiesButton = new SettingButton(Settings.disableEnemies, "Disable Enemies", 100, 180);
	private static final SettingButton collisionBoxButton = new SettingButton(Settings.enableCollisionDebug, "Draw Collision Boxes", 100, 260);
	private static final SettingButton showMapDebugInfo = new SettingButton(Settings.enableMapDebug, "Show Map Debug Info", 100, 340);

	private static final SettingButton enableSoundButton = new SettingButton(Settings.enableSound, "Enable Sound", 100, 20);
	private static final SettingButtonSlider volumeSlider = new SettingButtonSlider(Settings.volume, "Volume", 100, 100);
	
	//Separate class to avoid loading settings background map and loads of Button objects
	public static final class Settings{
		static final PropertyFile settingProperties = new PropertyFile("settings.prop", 14);
		
		public static int fps = settingProperties.getInt("fps", 50);
		public static int difficulty = settingProperties.getInt("difficulty", 0);
		public static int graphicsLevel = settingProperties.getInt("graphics", 2);
		public static int renderDebugLevel = settingProperties.getInt("renderDebugLevel", 0);
		public static int upKey = settingProperties.getInt("upKey", 'W');
		public static int downKey = settingProperties.getInt("downKey", 'S');
		public static int leftKey = settingProperties.getInt("leftKey", 'A');
		public static int rightKey = settingProperties.getInt("rightKey", 'D');
		public static boolean enableGod = settingProperties.getBoolean("enableGod", false);
		public static boolean disableEnemies = settingProperties.getBoolean("disableEnemies", false);
		public static boolean enableCollisionDebug = settingProperties.getBoolean("enableCollisionDebug", false);
		public static boolean enableMapDebug = settingProperties.getBoolean("enableMapDebug", false);
		public static boolean enableSound = settingProperties.getBoolean("enableSound", true);
		public static int volume = settingProperties.getInt("volume", 6);
		public static String screenshotFormat = settingProperties.getString("screenshotFormat", "JPG");
	}
}