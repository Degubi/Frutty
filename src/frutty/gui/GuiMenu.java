package frutty.gui;

import static frutty.tools.GuiHelper.*;

import frutty.*;
import frutty.gui.components.*;
import frutty.plugin.event.gui.*;
import frutty.tools.*;
import frutty.world.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import javax.swing.*;

public final class GuiMenu extends GuiMapBackground implements ActionListener{
	public static final Image frameIcon = Toolkit.getDefaultToolkit().createImage("./textures/player/side.png");
	public static JFrame mainFrame;
	
	public GuiMenu() {
		super("./maps/background" + Main.rand.nextInt(4) + ".fmap");
		setLayout(null);
		
		add(newButton("New Game", 700, 20, this));
		add(newButton("Exit", 370, 550, this));
		add(newButton("Settings", 700, 250, this));
		add(newButton("Load Save", 700, 100, this));
		add(newButton("Plugins", 20, 400, this));
		add(newButton("Stats", 700, 330, this));
		
		if(!EventHandle.menuInitEvents.isEmpty()) {
			var eventButtons = new ArrayList<JButton>(0);
			EventHandle.handleEvent(new GuiMenuEvent(eventButtons), EventHandle.menuInitEvents);
			for(var butt : eventButtons) add(butt);
		}
	}
	
	public static void createMainFrame(boolean checkUpdate) {
		var menu = new GuiMenu();
		
		if(checkUpdate) {
			new Thread(() -> {
				var netVersion = Version.fromURL(Plugin.plugins.get(0).versionURL);
				
				if(netVersion.isNewerThan(Plugin.plugins.get(0).version)) {
					var updaterButton = new JButton("New Update available: " + netVersion);
					updaterButton.setBounds(660, 600, 240, 40);
					updaterButton.addActionListener(menu);
					updaterButton.setBackground(Color.GREEN);
					updaterButton.setForeground(Color.RED);
					menu.add(updaterButton);
					menu.repaint();
				}
			}, "Menu Version Checker Thread").start();
		}
		
		EventQueue.invokeLater(() -> {
			mainFrame = new JFrame("Frutty");
			mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			mainFrame.setResizable(false);
			mainFrame.setIconImage(frameIcon);
			mainFrame.setBounds(0, 0, 910, 675);
			mainFrame.setLocationRelativeTo(null);
			mainFrame.setContentPane(menu);
			mainFrame.setFocusable(true);
			mainFrame.setVisible(true);
		});
	}
	
	@Override
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		
		graphics.setColor(GuiHelper.color_84Black);
		graphics.fillRect(0, 0, 910, 675);
		graphics.setColor(Color.WHITE);
		graphics.setFont(GuiHelper.thiccFont);
		graphics.drawString("Version: " + Plugin.plugins.get(0).version, 10, 625);
		graphics.setColor(Color.YELLOW);
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		var command = event.getActionCommand();
		
		if(command.equals("New Game")){
			GuiHelper.switchGui(new GuiMapSelection());
		}else if(command.equals("Exit")){
			System.exit(0);
		}else if(command.equals("Settings")){
			GuiSettings.showGuiSettings();
		}else if(command.equals("Plugins")) {
			GuiPlugins.showPlugins();
		}else if(command.equals("Stats")){
			GuiHelper.switchGui(new GuiStats());
		}else{ //Load
			try(var files = Files.list(Path.of("./saves"))){
				var saveFolderList = files.map(Path::getFileName).map(Path::toString).toArray(String[]::new);
				
				if(saveFolderList.length > 0) {
					var saveName = (String) JOptionPane.showInputDialog(this, "Chose map file!", "Saves", JOptionPane.QUESTION_MESSAGE, null, saveFolderList, saveFolderList[0]);
					
					if(World.loadSave(saveName)) {
						GuiIngame.showIngame();
						((JFrame)getTopLevelAncestor()).dispose();
					}
				}else{
					JOptionPane.showMessageDialog(this, "No saves found in saves directory");
				}
			} catch (IOException e) {}
		}
	}
}