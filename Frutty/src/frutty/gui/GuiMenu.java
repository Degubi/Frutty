package frutty.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import frutty.Main;
import frutty.gui.editor.GuiEditor;
import frutty.map.Map;
import frutty.map.interfaces.IInternalZone;
import frutty.map.interfaces.ITexturable;
import frutty.map.interfaces.ITransparentZone;
import frutty.map.interfaces.MapZoneBase;
import frutty.tools.Version;

public final class GuiMenu extends JPanel implements ActionListener{
	private final JComboBox<String> mapList = new JComboBox<>();
	private final JTextField mapSizeField = GuiHelper.newTextField("8x8", 500, 20);
	private final JCheckBox coopBox = GuiHelper.newCheckBox("Coop mode", 445, 130, Color.WHITE, false);
	
	private final MapZoneBase[] zones = new MapZoneBase[140];
	private final int[] xCoords = new int[140], yCoords = new int[140], textureData = new int[140];
	
	public GuiMenu() {
		setLayout(null);
		
		loadBackground();
		
		add(GuiHelper.newButton("New Game", 700, 20, this));
		add(GuiHelper.newButton("Exit", 370, 550, this));
		add(GuiHelper.newButton("Settings", 700, 250, this));
		add(GuiHelper.newButton("Load Save", 700, 100, this));
		add(GuiHelper.newButton("Editor", 20, 475, this));
		add(GuiHelper.newButton("Plugins", 20, 400, this));
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
		
		mapList.addItem("Generate");
		add(mapSizeField);
		mapList.setSelectedItem(Settings.lastMap);
		add(mapList);
		add(coopBox);
	}
	
	public static void showMenu(boolean checkUpdate) {
		GuiMenu menu = new GuiMenu();
		
		if(checkUpdate) {
			new Thread(() -> {
				if(Version.fromURL(Main.plugins[0].versionURL).isNewerThan(Main.plugins[0].version)) {
					JButton updaterButton = new JButton("Click here to Update...");
					updaterButton.setActionCommand("Update");
					updaterButton.setBounds(660, 600, 240, 40);
					updaterButton.addActionListener(menu);
					updaterButton.setBackground(Color.GREEN);
					updaterButton.setForeground(Color.RED);
					menu.add(updaterButton);
					menu.repaint();
				}
			}, "Menu Version Checker Thread").start();
		}
		GuiHelper.showNewFrame(menu, "Frutty", WindowConstants.EXIT_ON_CLOSE, 910, 675);
	}
	
	@Override
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		
		for(int k = 0; k < zones.length; ++k) {
			MapZoneBase zone = zones[k];
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
		graphics.drawString("Version: " + Main.plugins[0].version, 10, 625);
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand();
		
		if(command.equals("New Game")){
			String mapName = ((String)mapList.getSelectedItem());
			Settings.lastMap = mapName;
			if(mapName.equals("Generate")) {
				String[] mapSizeSplit = mapSizeField.getText().split("x");
				Map.generateMap(Integer.parseInt(mapSizeSplit[0]), Integer.parseInt(mapSizeSplit[1]), coopBox.isSelected());
			}else{
				Map.loadMap(mapName, coopBox.isSelected());
			}
			GuiIngame.showIngame();
			((JFrame)getTopLevelAncestor()).dispose();
			
		}else if(command.equals("Exit")){
			System.exit(0);
		}else if(command.equals("Settings")){
			Settings.showGuiSettings(this);
		}else if(command.equals("MapSelector")){ 
			if(((String)mapList.getSelectedItem()).toLowerCase().equals("generate")) {
				mapSizeField.setEditable(true);
				mapSizeField.setText("8x8");
			}else{
				mapSizeField.setEditable(false);
				mapSizeField.setText(loadMapSize((String) mapList.getSelectedItem()));
			}
		}else if(command.equals("Plugins")) {
			GuiPlugins.showPlugins();
		}else if(command.equals("Editor")){
			GuiEditor.openEditor(); ((JFrame)getTopLevelAncestor()).dispose();
		}else if(command.equals("Stats")){
			GuiStats.openStatsGui();
		}else if(command.equals("Update")){
			try {
				if(JOptionPane.showConfirmDialog(this, "Exiting game to Updater. Game will restart.", "Frutty Updater", JOptionPane.OK_CANCEL_OPTION) == 0) {
					Runtime.getRuntime().exec(System.getProperty("java.version").startsWith("10.") ? "javaw bin/FruttyUpdater"
							  																	   : "runtime\\bin\\javaw bin/FruttyUpdater");
					System.exit(0);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} 
			
		}else{ //Load
			var allMapNames = new File("./saves/").list();
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
	
	private void loadBackground() {
		try(var input = new ObjectInputStream(new FileInputStream("./maps/background" + Main.rand.nextInt(4) + ".deg"))){
			String[] textures = new String[input.readByte()];
			
			for(int k = 0; k < textures.length; ++k) {
				textures[k] = input.readUTF();
			}
			
			int zoneIDCount = input.readByte();
			String[] zoneIDS = new String[zoneIDCount];
			
			for(int k = 0; k < zoneIDCount; ++k) {
				zoneIDS[k] = input.readUTF();
			}
			
			Main.loadTextures(textures);
			Main.loadSkyTexture(input.readUTF());
			
			input.readShort(); input.readShort();
			
			int zoneIndex = 0;
			
			for(int y = 0; y < 640; y += 64) {
				for(int x = 0; x < 896; x += 64) {
					MapZoneBase zone = Main.getZoneFromName(zoneIDS[input.readByte()]);
					
					if(zone instanceof IInternalZone) {
						zone = ((IInternalZone) zone).getReplacementZone();
					}
					
					if(zone instanceof ITexturable) {
						textureData[zoneIndex] = input.readByte();
					}
					xCoords[zoneIndex] = x;
					yCoords[zoneIndex] = y;
					zones[zoneIndex++] = zone;
				}
			}
		}catch(IOException e){
		}
	}
	
	public static String loadMapSize(String fileName) {
		try(var input = new ObjectInputStream(Files.newInputStream(Paths.get("./maps/" + fileName + ".deg")))){
			int textureCount = input.readByte();
			for(int k = 0; k < textureCount; ++k) {
				input.readUTF();
			}
			
			int idCount = input.readByte();
			for(int k = 0; k < idCount; ++k) {
				input.readUTF();
			}
			
			input.readUTF();
			return input.readShort() + "x" + input.readShort();
		} catch (IOException e) {
			System.err.println("Can't load map size for menu: " + fileName + ".deg");
		}
		return "";
	}
}