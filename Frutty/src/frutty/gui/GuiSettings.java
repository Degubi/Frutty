package frutty.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public final class GuiSettings extends JPanel implements ActionListener{
	private static final Properties settings = new Properties();
	
	private final JRadioButton easyButton = new JRadioButton("Easy"), normalButton = new JRadioButton("Normal"), hardButton = new JRadioButton("Hard");
	private final JCheckBox debugBox = new JCheckBox("Enable debug");
	
	public GuiSettings() {
		setLayout(null);
		
		debugBox.setBounds(200, 200, 120, 30);
		debugBox.setSelected(isDebugEnabled());
		
		easyButton.setBounds(200, 20, 70, 30);
		normalButton.setBounds(200, 60, 80, 30);
		hardButton.setBounds(200, 100, 80, 30);
		
		ButtonGroup mapSelectorGroup = new ButtonGroup();
		easyButton.addActionListener(this);
		normalButton.addActionListener(this);
		hardButton.addActionListener(this);
		mapSelectorGroup.add(easyButton);
		mapSelectorGroup.add(normalButton);
		mapSelectorGroup.add(hardButton);
		
		int difficulty = getDifficulty();
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
		debugBox.setOpaque(false);
		
		add(GuiHelper.newButton("Save", 180, 300, this));
		add(easyButton);
		add(normalButton);
		add(hardButton);
		add(debugBox);
	}
	
	@Override
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		graphics.setColor(Color.LIGHT_GRAY);
		graphics.fillRect(0, 0, 480, 360);
		
		graphics.setColor(Color.DARK_GRAY);
		graphics.setFont(GuiHelper.thiccFont);
		graphics.drawString("Difficulty:", 120, 80);
	}
	
	public static boolean isDebugEnabled() {
		return Boolean.parseBoolean(settings.getProperty("enableDebug"));
	}
	
	public static int getDifficulty() {
		return Integer.parseInt(settings.getProperty("difficulty"));
	}
	
	public static void loadSettings() {
		try(FileInputStream fis = new FileInputStream("settings.cfg")){
			settings.load(fis);
		} catch (IOException e) {
			try(FileOutputStream fos = new FileOutputStream("settings.cfg")){
				settings.put("difficulty", "0");
				settings.put("enableDebug", "false");
				settings.store(fos, "");
			} catch (IOException e1) {}
		}
	}
	
	public static void showGuiSettings() {
		loadSettings();
		GuiHelper.showNewFrame(new GuiSettings(), "Tutty Frutty Options", JFrame.DISPOSE_ON_CLOSE, 480, 360);
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		if(easyButton.isSelected()) {
			settings.setProperty("difficulty", "0");
		}
		if(normalButton.isSelected()) {
			settings.setProperty("difficulty", "1");
		}
		if(hardButton.isSelected()) {
			settings.setProperty("difficulty", "2");
		}
		
		settings.setProperty("enableDebug", String.valueOf(debugBox.isSelected()));
		
		if(event.getActionCommand().equals("Save")) {
			try(FileOutputStream output = new FileOutputStream("settings.cfg")){
				settings.store(output, "");
			} catch (IOException e) {}
			((JFrame)getTopLevelAncestor()).dispose();
		}
	}
}