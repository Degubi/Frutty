package frutty.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Hashtable;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public final class Settings{
	public static int difficulty, upKey, downKey, leftKey, rightKey, graphicsLevel = 2, fps = 50, renderDebugLevel;
	public static boolean godEnabled, disableEnemies, debugCollisions, mapDebug, debugLevels;
	
	private static DebugOptions debugInstance;
	private static VideoOptions videoInstance;
	private static GameOptions gameInstance;
	
	public static void loadSettings() {
		try(BufferedReader input = Files.newBufferedReader(Paths.get("settings.cfg"))){
			String[] data = input.readLine().split(" ");
			difficulty = Integer.parseInt(data[0]);
			godEnabled = Boolean.parseBoolean(data[1]);
			upKey = Integer.parseInt(data[2]);
			downKey = Integer.parseInt(data[3]);
			leftKey = Integer.parseInt(data[4]);
			rightKey = Integer.parseInt(data[5]);
			disableEnemies = Boolean.parseBoolean(data[6]);
			debugCollisions = Boolean.parseBoolean(data[7]);
			debugLevels = Boolean.parseBoolean(data[8]);
			mapDebug = Boolean.parseBoolean(data[9]);
			graphicsLevel = Integer.parseInt(data[10]);
			renderDebugLevel = Integer.parseInt(data[11]);
			fps = Integer.parseInt(data[12]);
			
		} catch (IOException e) {
			try(PrintWriter output = new PrintWriter("settings.cfg")){
				output.print(0);
				output.print(' ');
				output.print(false);
				output.print(' ');
				output.print((int)'W');
				output.print(' ');
				output.print((int)'S');
				output.print(' ');
				output.print((int)'A');
				output.print(' ');
				output.print((int)'D');
				output.print(' ');
				output.print(false);
				output.print(' ');
				output.print(false);
				output.print(' ');
				output.print("Creepy");
				output.print(' ');
				output.print(false);
				output.print(' ');
				output.print(2);
				output.print(' ');
				output.print(0);
				output.print(' ');
				output.print(50);
			} catch (FileNotFoundException ex) {
				//Can't rly happen
			}
		}
	}
	
	public static void showGuiSettings(GuiMenu menu) {
		Settings.loadSettings();
		var tabbed = new JTabbedPane();
		tabbed.addTab("Gameplay", gameInstance = new GameOptions());
		tabbed.addTab("Graphics", videoInstance = new VideoOptions(menu));
		tabbed.addTab("Debug", debugInstance = new DebugOptions(menu));
		
		EventQueue.invokeLater(() -> {
			JFrame returnFrame = new JFrame("Frutty Options");
			returnFrame.setContentPane(tabbed);
			returnFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			returnFrame.setResizable(false);
			returnFrame.setBounds(0, 0, 576, 480);
			returnFrame.setLocationRelativeTo(null);
			returnFrame.setFocusable(true);
			returnFrame.setVisible(true);
		});
	}
	
	public static void saveSettings() {
		Settings.mapDebug = debugInstance.showMapDebug.isSelected();
		Settings.debugCollisions = debugInstance.showCollisionBoxes.isSelected();
		Settings.godEnabled = debugInstance.godMode.isSelected();
		Settings.renderDebugLevel = debugInstance.renderDebugLevelSlider.getValue();
		Settings.disableEnemies = debugInstance.enemiesDisabled.isSelected();
		Settings.graphicsLevel = videoInstance.graphicsSlider.getValue();
		Settings.fps = videoInstance.fpsSlider.getValue();
		if(gameInstance.easyButton.isSelected()) {
			Settings.difficulty = 0;
		}
		if(gameInstance.normalButton.isSelected()) {
			Settings.difficulty = 1;
		}
		if(gameInstance.hardButton.isSelected()) {
			Settings.difficulty = 2;
		}
		
		Settings.upKey = gameInstance.upKeyField.getText().charAt(0);
		Settings.downKey = gameInstance.downKeyField.getText().charAt(0);
		Settings.leftKey = gameInstance.leftKeyField.getText().charAt(0);
		Settings.rightKey = gameInstance.rightKeyField.getText().charAt(0);
		
		try(var output = new PrintWriter("settings.cfg")){
			output.print(difficulty);
			output.print(' ');
			output.print(godEnabled);
			output.print(' ');
			output.print(upKey);
			output.print(' ');
			output.print(downKey);
			output.print(' ');
			output.print(leftKey);
			output.print(' ');
			output.print(rightKey);
			output.print(' ');
			output.print(disableEnemies);
			output.print(' ');
			output.print(debugCollisions);
			output.print(' ');
			output.print(debugLevels);
			output.print(' ');
			output.print(mapDebug);
			output.print(' ');
			output.print(graphicsLevel);
			output.print(' ');
			output.print(renderDebugLevel);
			output.print(' ');
			output.print(fps);
		} catch (FileNotFoundException e) {
			//Can't rly happen
		}
	}
	
	static void addSaveButton(JPanel panel) {
		JButton saveButton = new JButton("Save");
		saveButton.setBounds(230, 370, 120, 30);
		saveButton.addActionListener((ActionListener) panel);
		panel.add(saveButton);
	}
	
	@SuppressWarnings("boxing")
	protected static final class DebugOptions extends JPanel implements ActionListener{
		protected final JCheckBox godMode = GuiHelper.newCheckBox("Enable God Mode", 20, 80, Color.BLACK, Settings.godEnabled);
		protected final JCheckBox enemiesDisabled = GuiHelper.newCheckBox("Disable Enemies", 20, 110, Color.BLACK, Settings.disableEnemies);
		protected final JCheckBox showCollisionBoxes = GuiHelper.newCheckBox("Show Collision Boxes", 20, 140, Color.BLACK, Settings.debugCollisions);
		protected final JCheckBox showMapDebug = GuiHelper.newCheckBox("Enable Map Debug", 20, 170, Color.BLACK, Settings.mapDebug);
		
		protected final JSlider renderDebugLevelSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 3, Settings.renderDebugLevel);
		private final GuiMenu menuInstance;
		
		protected DebugOptions(GuiMenu menu) {
			setLayout(null);
			menuInstance = menu;
			
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
			
			addSaveButton(this);
			add(godMode);
			add(enemiesDisabled);
			add(showCollisionBoxes);
			add(showMapDebug);
			add(renderDebugLevelSlider);
		}
		
		@Override
		protected void paintComponent(Graphics graphics) {
			super.paintComponent(graphics);
			graphics.setColor(Color.LIGHT_GRAY);
			graphics.fillRect(0, 0, 576, 480);
		}
		
		@Override
		public void actionPerformed(ActionEvent event) {
			if(event.getActionCommand().equals("Save")) {
				Settings.saveSettings();
				((JFrame)getTopLevelAncestor()).dispose();
			}
			menuInstance.repaint();
		}
	}
	
	@SuppressWarnings("boxing")
	protected static final class VideoOptions extends JPanel implements ActionListener{
		private final GuiMenu menuInstance;
		protected final JSlider graphicsSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 2, Settings.graphicsLevel);
		protected final JSlider fpsSlider = new JSlider(SwingConstants.HORIZONTAL, 10, 100, Settings.fps);
		
		protected VideoOptions(GuiMenu menu) {
			setLayout(null);
			menuInstance = menu;
			
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
			
			addSaveButton(this);
			add(graphicsSlider);
			add(fpsSlider);
		}
		
		@Override
		protected void paintComponent(Graphics graphics) {
			super.paintComponent(graphics);
			graphics.setColor(Color.LIGHT_GRAY);
			graphics.fillRect(0, 0, 576, 480);
			
			graphics.setColor(Color.DARK_GRAY);
			graphics.setFont(GuiHelper.thiccFont);
			graphics.drawString("Graphics level:", 50, 40);
			graphics.drawString("Render Framerate:", 50, 140);
		}
		
		@Override
		public void actionPerformed(ActionEvent event) {
			if(event.getActionCommand().equals("Save")) {
				Settings.saveSettings();
				((JFrame)getTopLevelAncestor()).dispose();
			}
			menuInstance.repaint();
		}
	}
	
	protected static final class GameOptions extends JPanel implements ActionListener{
		protected final JRadioButton easyButton = new JRadioButton("Easy"), normalButton = new JRadioButton("Normal"), hardButton = new JRadioButton("Hard");
		protected final JTextField upKeyField = newTextField(Settings.upKey, 100, 245), downKeyField = newTextField(Settings.downKey, 100, 275);
		protected final JTextField leftKeyField = newTextField(Settings.leftKey, 100, 305), rightKeyField = newTextField(Settings.rightKey, 100, 335);
		
		protected GameOptions() {
			setLayout(null);
			
			easyButton.setBounds(100, 20, 70, 30);
			normalButton.setBounds(100, 60, 80, 30);
			hardButton.setBounds(100, 100, 80, 30);
			
			var difficultyGroup = new ButtonGroup();
			easyButton.addActionListener(this);
			normalButton.addActionListener(this);
			hardButton.addActionListener(this);
			difficultyGroup.add(easyButton);
			difficultyGroup.add(normalButton);
			difficultyGroup.add(hardButton);
			
			if(Settings.difficulty == 1) {
				normalButton.setSelected(true);
			}else if(Settings.difficulty == 2) {
				hardButton.setSelected(true);
			}else{
				easyButton.setSelected(true);
			}
			
			easyButton.setOpaque(false);
			hardButton.setOpaque(false);
			normalButton.setOpaque(false);
			
			addSaveButton(this);
			add(upKeyField);
			add(downKeyField);
			add(leftKeyField);
			add(rightKeyField);
			add(easyButton);
			add(normalButton);
			add(hardButton);
		}

		private static JTextField newTextField(int code, int x, int y) {
			var field = new JTextField(Character.toString((char)code));
			field.setBounds(x, y, 20, 20);
			field.setHorizontalAlignment(SwingConstants.CENTER);
			((AbstractDocument)field.getDocument()).setDocumentFilter(TextFilter.filter);
			return field;
		}
		
		@Override
		protected void paintComponent(Graphics graphics) {
			super.paintComponent(graphics);
			graphics.setColor(Color.LIGHT_GRAY);
			graphics.fillRect(0, 0, 576, 480);
			
			graphics.setColor(Color.DARK_GRAY);
			graphics.setFont(GuiHelper.thiccFont);
			graphics.drawString("Difficulty:", 20, 80);
			
			graphics.drawString("Player 2 Controls:", 20, 220);
			graphics.drawString("Up:", 40, 260);
			graphics.drawString("Down:", 40, 290);
			graphics.drawString("Left:", 40, 320);
			graphics.drawString("Right:", 40, 350);
		}
		
		@Override
		public void actionPerformed(ActionEvent event) {
			if(event.getActionCommand().equals("Save")) {
				Settings.saveSettings();
				((JFrame)getTopLevelAncestor()).dispose();
			}
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
	
	protected static final class WorldOptions extends JPanel implements ActionListener{
		protected WorldOptions() {
			setLayout(null);
			addSaveButton(this);
		}
		
		@Override
		public void actionPerformed(ActionEvent event) {
			if(event.getActionCommand().equals("Save")) {
				Settings.saveSettings();
				((JFrame)getTopLevelAncestor()).dispose();
			}
		}
	}
}