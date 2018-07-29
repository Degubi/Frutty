package frutty.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import frutty.gui.components.GuiHelper;
import frutty.gui.components.SettingButton;
import frutty.gui.components.SettingButtonField;
import frutty.tools.PropertyFile;

public final class GuiSettings extends JPanel implements ActionListener{
	public static final PropertyFile settingProperties = new PropertyFile("settings.prop", 12);
	
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
	
	public GuiSettings(JComponent... components) {
		setLayout(null);
		
		for(JComponent comp : components) add(comp);
		add(GuiHelper.newButton("Save", 370, 525, this));
	}
	
	@Override
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		graphics.setColor(Color.GRAY);
		graphics.fillRect(0, 0, 910, 675);
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getActionCommand().equals("Save")) {
			settingProperties.setBoolean("enableMapDebug", enableMapDebug = showMapDebugInfo.optionIndex == 1);
			settingProperties.setBoolean("enableCollisionDebug", enableCollisionDebug = collisionBoxButton.optionIndex == 1);
			settingProperties.setBoolean("enableGod", enableGod = godModeButton.optionIndex == 1);
			settingProperties.setInt("renderDebugLevel", renderDebugLevel = renderDebugLevelButton.optionIndex);
			settingProperties.setBoolean("disableEnemies", disableEnemies = disableEnemiesButton.optionIndex == 1);
			settingProperties.setInt("graphics", graphicsLevel = graphicsLevelButton.optionIndex);
			settingProperties.setInt("fps", fpsOptions[fpsButton.optionIndex]);
			settingProperties.setInt("difficulty", difficultyButton.optionIndex);
			settingProperties.setInt("upKey", upKey = upKeyButtonField.dataField.getText().charAt(0));
			settingProperties.setInt("downKey", downKey = downKeyButtonField.dataField.getText().charAt(0));
			settingProperties.setInt("leftKey", leftKey = leftKeyButtonField.dataField.getText().charAt(0));
			settingProperties.setInt("rightKey", rightKey = rightKeyButtonField.dataField.getText().charAt(0));
			settingProperties.save();
			GuiHelper.switchMenuPanel(new GuiMenu());
		}
	}
	
	public static void showGuiSettings() {
		JTabbedPane tabbed = new JTabbedPane();
		tabbed.addTab("Gameplay", new GuiSettings(difficultyButton, upKeyButtonField, downKeyButtonField, leftKeyButtonField, rightKeyButtonField));
		tabbed.addTab("Graphics", new GuiSettings(graphicsLevelButton, fpsButton));
		tabbed.addTab("Debug", new GuiSettings(renderDebugLevelButton, godModeButton, disableEnemiesButton, collisionBoxButton, showMapDebugInfo));
		
		GuiHelper.switchMenuPanel(tabbed);
	}
	
	private static final SettingButton difficultyButton = new SettingButton(settingProperties.getInt("difficulty", 0), "Difficulty", 100, 20, "Easy", "Normal", "Hard");
	private static final SettingButtonField upKeyButtonField = new SettingButtonField(GuiSettings.upKey, "Second Player Upwards Key", 100, 100);
	private static final SettingButtonField downKeyButtonField = new SettingButtonField(GuiSettings.downKey, "Second Player Downwards Key", 100, 180);
	private static final SettingButtonField leftKeyButtonField = new SettingButtonField(GuiSettings.leftKey, "Second Player Left Key", 100, 260);
	private static final SettingButtonField rightKeyButtonField = new SettingButtonField(GuiSettings.rightKey, "Second Player Right Key", 100, 340);
	
	private static final SettingButton graphicsLevelButton = new SettingButton(GuiSettings.graphicsLevel, "Graphics Level", 100, 20, "Low", "Medium", "High");
	private static final int[] fpsOptions = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
	private static final SettingButton fpsButton = new SettingButton(SettingButton.indexOf(settingProperties.getInt("fps", 50), fpsOptions), "FPS", 100, 100, fpsOptions);
	
	private static final SettingButton renderDebugLevelButton = new SettingButton(renderDebugLevel, "Render Debug Level", 100, 20, "None", "FPS Debug", "Zone Bounds", "All");
	private static final SettingButton godModeButton = new SettingButton(enableGod, "Enable God Mode", 100, 100, SettingButton.ON_OFF);
	private static final SettingButton disableEnemiesButton = new SettingButton(disableEnemies, "Disable Enemies", 100, 180, SettingButton.ON_OFF);
	private static final SettingButton collisionBoxButton = new SettingButton(enableCollisionDebug, "Draw Collision Boxes", 100, 260, SettingButton.ON_OFF);
	private static final SettingButton showMapDebugInfo = new SettingButton(GuiSettings.enableMapDebug, "Show Map Debug Info", 100, 340, SettingButton.ON_OFF);
}