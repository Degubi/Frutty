package frutty.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import frutty.Main;
import frutty.gui.GuiSettings.Settings;
import frutty.gui.editor.GuiEditor;
import frutty.map.Map;
import frutty.map.base.MapZone;
import frutty.map.interfaces.ITransparentZone;

public final class GuiMenu extends JPanel implements ActionListener{
	private final JComboBox<String> mapList = new JComboBox<>();
	private final JTextField mapSizeField = GuiHelper.newTextField("8x8", 500, 20);
	private final JCheckBox coopBox = GuiHelper.newCheckBox("Coop mode", 445, 130, Color.WHITE, false);
	
	private final MapZone[] zones = new MapZone[140];
	private final int[] xCoords = new int[140], yCoords = new int[140], textureData = new int[140];
	
	public GuiMenu() {
		setLayout(null);
		
		Thread backgroundMapThread = new Thread(() -> Map.loadBackground(zones, xCoords, yCoords, textureData));
		backgroundMapThread.start();
		
		add(GuiHelper.newButton("New Game", 700, 20, this));
		add(GuiHelper.newButton("Exit", 370, 550, this));
		add(GuiHelper.newButton("Settings", 700, 250, this));
		add(GuiHelper.newButton("Load Save", 700, 100, this));
		add(GuiHelper.newButton("Editor", 20, 475, this));
		add(GuiHelper.newButton("Stats", 700, 330, this));
		
		mapList.addActionListener(this);
		mapList.setActionCommand("MapSelector");
		mapList.setBounds(330, 20, 100, 30);
		mapList.setBackground(Color.LIGHT_GRAY);
		mapList.setBorder(GuiHelper.menuBorder);
		
		String[] maps = new File("./maps/").list();
		for(String map : maps) {
			if(!map.startsWith("background") && map.endsWith(".deg")) {
				mapList.addItem(map.substring(0, map.length() - 4));
			}
		}
		
		mapList.addItem("Generate");  //Legeslegrosszabb esetben is tudunk generálni mapot
		add(mapSizeField);
		mapList.setSelectedItem(Settings.lastMap);
		add(mapList);
		add(coopBox);
		
		try {
			backgroundMapThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void showMenu(boolean checkUpdate) {
		GuiMenu menu = new GuiMenu();
		
		new Thread(() -> {
			if(checkUpdate) {
				try(BufferedReader download = new BufferedReader(new InputStreamReader(new URL("https://pastebin.com/raw/m5qJbnks").openStream()))){
					if(!Main.VERSION.equals(download.readLine())) {
						JButton updaterButton = new JButton("Click here to Update...");
						updaterButton.setActionCommand("Update");
						updaterButton.setBounds(660, 600, 240, 40);
						updaterButton.addActionListener(menu);
						updaterButton.setBackground(Color.GREEN);
						updaterButton.setForeground(Color.RED);
						menu.add(updaterButton);
						menu.repaint();
					}
				}catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
		GuiHelper.showNewFrame(menu, "Tutty Frutty", JFrame.EXIT_ON_CLOSE, 910, 675);
	}
	
	@Override
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		
		for(int k = 0; k < zones.length; ++k) {
			MapZone zone = zones[k];
			zone.render(xCoords[k], yCoords[k], textureData[k], graphics);
			if(zone instanceof ITransparentZone) {
				((ITransparentZone) zone).drawAfter(xCoords[k], yCoords[k], textureData[k], graphics);
			}
		}
		
		graphics.setColor(GuiHelper.color_84Black);
		graphics.fillRect(0, 0, 910, 675);
		graphics.setColor(Color.WHITE);
		graphics.setFont(GuiHelper.thiccFont);
		graphics.drawString(GuiHelper.recommendedMapSizeString, 330, 80);
		graphics.drawString("Size:", 455, 40);
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		switch(event.getActionCommand()) {
		
		case "New Game":
			String mapName = ((String)mapList.getSelectedItem());
			Settings.lastMap = mapName;
			if(mapName.equals("Generate")) {
				String[] mapSizeSplit = mapSizeField.getText().split("x");
				Map.generateMap(Integer.parseInt(mapSizeSplit[0]), Integer.parseInt(mapSizeSplit[1]), coopBox.isSelected());
			}else{
				Map.loadMap(mapName, coopBox.isSelected());
			}
			GuiIngame.showIngame();
			((JFrame)getTopLevelAncestor()).dispose(); break;
			
		case "Exit": System.exit(0); break;
		case "Settings": GuiSettings.showGuiSettings(this); break;
		case "MapSelector": 
			if(((String)mapList.getSelectedItem()).toLowerCase().equals("generate")) {
				mapSizeField.setEditable(true);
				mapSizeField.setText("8x8");
			}else{
				mapSizeField.setEditable(false);
				mapSizeField.setText(Map.loadMapSize((String) mapList.getSelectedItem()));
			}break;
			
		case "Editor": GuiEditor.openEditor(); ((JFrame)getTopLevelAncestor()).dispose(); break;
		case "Stats": GuiStats.openStatsGui(); break;
		case "Update": try {
				JOptionPane.showMessageDialog(null, "Exiting game to Updater", "Frutty Updater", 0);
				Runtime.getRuntime().exec("java -jar FruttyInstaller.jar");
				System.exit(0);
			} catch (IOException e) {
				e.printStackTrace();
			} break;
			
		default: //Load
			String[] allMapNames = new File("./saves/").list();
			if(allMapNames.length > 0) {
				if(Map.loadSave((String) JOptionPane.showInputDialog(this, "Chose map file!", "Saves", JOptionPane.QUESTION_MESSAGE, null, allMapNames, allMapNames[0]))) {
					GuiIngame.showIngame();
					((JFrame)getTopLevelAncestor()).dispose();
				}
			}else{
				JOptionPane.showMessageDialog(this, "No saves found in saves directory");
			}
		}
	}
}