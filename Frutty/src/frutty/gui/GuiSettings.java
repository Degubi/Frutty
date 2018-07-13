package frutty.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import frutty.tools.PropertyFile;

public final class GuiSettings extends JPanel implements ActionListener{
	public static final PropertyFile settingProperties = new PropertyFile("settings.prop");
	public static int graphicsLevel = settingProperties.getInt("graphics", 2);
	public static int renderDebugLevel = settingProperties.getInt("renderDebug", 0);
	public static int upKey = settingProperties.getInt("upKey", 'W');
	public static int downKey = settingProperties.getInt("downKey", 'S');
	public static int leftKey = settingProperties.getInt("leftKey", 'A');
	public static int rightKey = settingProperties.getInt("rightKey", 'D');
	public static boolean enableGod = settingProperties.getBoolean("enableGod", false);
	public static boolean disableEnemies = settingProperties.getBoolean("disableEnemies", false);
	public static boolean enableCollisionDebug = settingProperties.getBoolean("enableCollisionDebug", false);
	public static boolean enableMapDebug = settingProperties.getBoolean("enableMapDebug", false);
	
	public GuiSettings() {
		setLayout(null);
		add(GuiHelper.newButton("Save", 350, 500, this));
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
			settingProperties.setBoolean("enableMapDebug", enableMapDebug = showMapDebug.isSelected());
			settingProperties.setBoolean("enableCollisionDebug", enableCollisionDebug = showCollisionBoxes.isSelected());
			settingProperties.setBoolean("enableGod", enableGod = godMode.isSelected());
			settingProperties.setInt("renderDebugLevel", renderDebugLevel = renderDebugLevelSlider.getValue());
			settingProperties.setBoolean("disableEnemies", disableEnemies = enemiesDisabled.isSelected());
			settingProperties.setInt("graphics", graphicsLevel = graphicsSlider.getValue());
			settingProperties.setInt("fps", fpsSlider.getValue());
			settingProperties.setInt("difficulty", easyButton.isSelected() ? 0 : normalButton.isSelected() ? 1 : 2);
			settingProperties.setInt("upKey", upKey = upKeyField.getText().charAt(0));
			settingProperties.setInt("downKey", downKey = downKeyField.getText().charAt(0));
			settingProperties.setInt("leftKey", leftKey = leftKeyField.getText().charAt(0));
			settingProperties.setInt("rigthKey", rightKey = rightKeyField.getText().charAt(0));
			settingProperties.save();
			GuiHelper.switchMenuPanel(new GuiMenu());
		}
	}
	
	public static void showGuiSettings() {
		JTabbedPane tabbed = new JTabbedPane();
		tabbed.addTab("Gameplay", getGameSettings());
		tabbed.addTab("Graphics", getVideoSettings());
		tabbed.addTab("Debug", getDebugOptions());
		
		GuiHelper.switchMenuPanel(tabbed);
	}
	
	@SuppressWarnings("boxing")
	private static GuiSettings getDebugOptions() {
		GuiSettings settings = new GuiSettings();
		
		renderDebugLevelSlider.setBounds(20, 20, 300, 40);
		renderDebugLevelSlider.setSnapToTicks(true);
		renderDebugLevelSlider.setOpaque(false);
		renderDebugLevelSlider.setPaintLabels(true);
		renderDebugLevelSlider.setPaintTicks(true);
		
		var renderTable = new Hashtable<Integer, JLabel>(4);
		renderTable.put(0, new JLabel("None"));
		renderTable.put(1, new JLabel("FPS Debug"));
		renderTable.put(2, new JLabel("Zone Bounds"));
		renderTable.put(3, new JLabel("All"));
		renderDebugLevelSlider.setLabelTable(renderTable);
		
		settings.add(godMode);
		settings.add(enemiesDisabled);
		settings.add(showCollisionBoxes);
		settings.add(showMapDebug);
		settings.add(renderDebugLevelSlider);
		
		return settings;
	}
	
	private static GuiSettings getGameSettings() {
		GuiSettings settings = new GuiSettings();
		
		easyButton.setBounds(100, 20, 70, 30);
		normalButton.setBounds(100, 60, 80, 30);
		hardButton.setBounds(100, 100, 80, 30);
		
		var difficultyGroup = new ButtonGroup();
		difficultyGroup.add(easyButton);
		difficultyGroup.add(normalButton);
		difficultyGroup.add(hardButton);
		
		int difficulty = settingProperties.getInt("difficulty", 0);
		if(difficulty == 1) {
			normalButton.setSelected(true);
		}else if(difficulty == 2) {
			hardButton.setSelected(true);
		}else{
			easyButton.setSelected(true);
		}
		
		easyButton.setOpaque(false);
		hardButton.setOpaque(false);
		normalButton.setOpaque(false);
		
		settings.add(upKeyField);
		settings.add(downKeyField);
		settings.add(leftKeyField);
		settings.add(rightKeyField);
		settings.add(easyButton);
		settings.add(normalButton);
		settings.add(hardButton);
		
		return settings;
	}
	
	@SuppressWarnings("boxing")
	private static GuiSettings getVideoSettings() {
		GuiSettings settings = new GuiSettings();
		
		graphicsSlider.setPaintLabels(true);
		graphicsSlider.setBounds(30, 40, 150, 40);
		graphicsSlider.setOpaque(false);
		graphicsSlider.setSnapToTicks(true);
		
		var table = new Hashtable<Integer, JLabel>(3);
		table.put(0, new JLabel("Low"));
		table.put(1, new JLabel("Medium"));
		table.put(2, new JLabel("High"));
		graphicsSlider.setLabelTable(table);
		
		fpsSlider.setBounds(0, 150, 250, 40);
		fpsSlider.setOpaque(false);
		fpsSlider.setSnapToTicks(true);
		fpsSlider.setMajorTickSpacing(10);
		fpsSlider.setMinorTickSpacing(10);
		fpsSlider.setPaintTicks(true);
		fpsSlider.setPaintLabels(true);
		
		settings.add(newLabel("Graphics Level", 65, 15, 120, 40));
		settings.add(graphicsSlider);
		settings.add(fpsSlider);
		settings.add(newLabel("FPS", 100, 110, 60, 40));
		
		return settings;
	}
	
	private static final JCheckBox godMode = GuiHelper.newCheckBox("Enable God Mode", 20, 80, Color.BLACK, GuiSettings.enableGod);
	private static final JCheckBox enemiesDisabled = GuiHelper.newCheckBox("Disable Enemies", 20, 110, Color.BLACK, GuiSettings.disableEnemies);
	private static final JCheckBox showCollisionBoxes = GuiHelper.newCheckBox("Show Collision Boxes", 20, 140, Color.BLACK, GuiSettings.enableCollisionDebug);
	private static final JCheckBox showMapDebug = GuiHelper.newCheckBox("Enable Map Debug", 20, 170, Color.BLACK, GuiSettings.enableMapDebug);
	private static final JSlider renderDebugLevelSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 3, GuiSettings.renderDebugLevel);
	private static final JSlider graphicsSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 2, GuiSettings.graphicsLevel);
	private static final JSlider fpsSlider = new JSlider(SwingConstants.HORIZONTAL, 10, 100, settingProperties.getInt("fps", 50));
	private static final JRadioButton easyButton = new JRadioButton("Easy"), normalButton = new JRadioButton("Normal"), hardButton = new JRadioButton("Hard");
	private static final JTextField upKeyField = newTextField(GuiSettings.upKey, 100, 245), downKeyField = newTextField(GuiSettings.downKey, 100, 275);
	private static final JTextField leftKeyField = newTextField(GuiSettings.leftKey, 100, 305), rightKeyField = newTextField(GuiSettings.rightKey, 100, 335);
	
	private static JTextField newTextField(int code, int x, int y) {
		var field = new JTextField(Character.toString((char)code));
		field.setBounds(x, y, 20, 20);
		field.setHorizontalAlignment(SwingConstants.CENTER);
		((AbstractDocument) field.getDocument()).setDocumentFilter(TextFilter.filter);
		return field;
	}

	private static JLabel newLabel(String text, int x, int y, int width, int height) {
		JLabel label = new JLabel(text);
		label.setBounds(x, y, width, height);
		return label;
	}
	
	protected static final class TextFilter extends DocumentFilter{
		public static final TextFilter filter = new TextFilter();
			
		@Override
		public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
			super.replace(fb, offset, length, text.toUpperCase(), attrs);
			if(offset > 0) {
				super.remove(fb, 0, 1);
			}
		}
	}
}