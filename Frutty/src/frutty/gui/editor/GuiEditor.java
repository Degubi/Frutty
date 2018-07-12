package frutty.gui.editor;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import frutty.Main;
import frutty.gui.GuiHelper;
import frutty.gui.GuiMenu;
import frutty.gui.editor.GuiProperties.EnumProperty;
import frutty.world.interfaces.ITexturable;

public final class GuiEditor extends JPanel{
	public final ArrayList<EditorZoneButton> zoneButtons = new ArrayList<>();
	public final GuiProperties mapProperties;
	protected final TextureSelectorButton textureSelectorButton;
	protected final JComboBox<String> zoneList;
	
	private GuiEditor(String fileName, String skyName, int... data) {
		setLayout(null);
		
		if(fileName != null) {
			mapProperties = new GuiProperties(fileName, skyName, data);
			zoneList = new JComboBox<>(zoneNames());
			zoneList.setSelectedItem("normalZone");
			zoneList.setBounds(data[0] * 64 + 20, 80, 120, 30);
			
			add(zoneList);
			add(textureSelectorButton = new TextureSelectorButton(data[0], this));
		}else{
			mapProperties = null;
			zoneList = null;
			textureSelectorButton = null;
		}
	}
	
	public static String[] zoneNames() {
		String[] names = new String[Main.zoneIndex / 2];
		for(int k = 0, localIndex = 0; k < Main.zoneIndex; k += 2) {
			names[localIndex++] = (String) Main.zoneStorage[k];
		}
		
		return names;
	}
	
	@Override
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		
		if(!zoneButtons.isEmpty()) {
			graphics.setFont(GuiHelper.thiccFont);
			graphics.drawString("Current texture: " + textureSelectorButton.activeTexture, getWidth() - 175, 190);
		}
	}

	public static void openEditor() {
		showEditorFrame(new GuiEditor(null, null, 0, 0, 0, 0, 0, 0), 800, 600);
	}
	
	private void saveMap() {
		var mapName = mapProperties.getProperty(EnumProperty.MapName);
		try(var output = new ObjectOutputStream(new FileOutputStream("./maps/" + mapName))){
	 		var textures = new ArrayList<String>();
	 		var zoneIDs = new ArrayList<String>();
			
	 		for(var writeButton : zoneButtons) {
	 			if(!zoneIDs.contains(writeButton.zoneID)) {
	 				zoneIDs.add(writeButton.zoneID);
	 			}
	 			if(Main.getZoneFromName(writeButton.zoneID) instanceof ITexturable) {
	 				if(!writeButton.zoneTexture.isEmpty() && !textures.contains(writeButton.zoneTexture)) {
	 					textures.add(writeButton.zoneTexture);
	 				}
	 			}
	 		}
	 		output.writeByte(textures.size());
	 		
	 		for(int k = 0; k < textures.size(); ++k) {
	 			output.writeUTF(textures.get(k));
	 		}
	 		
	 		output.writeByte(zoneIDs.size());
	 		
	 		for(int k = 0; k < zoneIDs.size(); ++k) {
	 			output.writeUTF(zoneIDs.get(k));
	 		}
	 		
			output.writeUTF(mapProperties.getProperty(EnumProperty.SkyTexture));
	 		output.writeShort(mapProperties.getIntProperty(EnumProperty.MapWidth));
	 		output.writeShort(mapProperties.getIntProperty(EnumProperty.MapHeight));
	 		output.writeShort(mapProperties.getIntProperty(EnumProperty.Player1PosX));
	 		output.writeShort(mapProperties.getIntProperty(EnumProperty.Player1PosY));
	 		output.writeShort(mapProperties.getIntProperty(EnumProperty.Player2PosX));
	 		output.writeShort(mapProperties.getIntProperty(EnumProperty.Player2PosY));
		 	
	 		for(var writeButton : zoneButtons) {
	 			output.writeByte(zoneIDs.indexOf(writeButton.zoneID));
	 			
	 			if(Main.getZoneFromName(writeButton.zoneID) instanceof ITexturable) {
	 				output.writeByte(textures.indexOf(writeButton.zoneTexture));
	 			}
	 		}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		var file = Paths.get("editorhistory.txt");
		try {
			var lines = Files.readAllLines(file);
			if(!lines.contains(mapName)) {
				lines.add(mapName);
				
				if(lines.size() > 5) {
					lines.remove(0);
				}
				Files.write(file, lines);
			}
		} catch (IOException e) {
			try {
				Files.write(file, List.of(mapName));
			} catch (IOException e1) {
				//Can't rly happen
			}
		}
		
	 	JOptionPane.showMessageDialog(null, "Map saved as: " + mapName);
	}
	
	private static void loadMap(String fileName) {
		if(fileName != null && !fileName.isEmpty()) {
			try(var input = new ObjectInputStream(new FileInputStream("./maps/" + fileName))){
				int textureCount = input.readByte();
				String[] textures = new String[textureCount];
				
				for(int k = 0; k < textureCount; ++k) {
					textures[k] = input.readUTF();
				}
				
				int zoneIDCount = input.readByte();
				String[] zoneIDS = new String[zoneIDCount];
				
				for(int k = 0; k < zoneIDCount; ++k) {
					zoneIDS[k] = input.readUTF();
				}
				
				String skyName = input.readUTF();
				int mapWidth = input.readShort(), mapHeight = input.readShort();
				GuiEditor editor = new GuiEditor(fileName, skyName, mapWidth, mapHeight, input.readShort(), input.readShort(), input.readShort(), input.readShort());
				
				for(int y = 0; y < mapHeight; ++y) {
					for(int x = 0; x < mapWidth; ++x) {
						String zoneID = zoneIDS[input.readByte()];
						Main.getZoneFromName(zoneID).handleEditorReading(editor, zoneID, input, x, y, textures);
					}
				}
				showEditorFrame(editor, mapWidth * 64, mapHeight * 64);
				
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Map file deleted...");
			}
		}
	}
	
	private static void showEditorFrame(GuiEditor editor, int width, int height) {
		EventQueue.invokeLater(() -> {
			JFrame frame = new JFrame("Frutty Map Editor");
			frame.setContentPane(editor);
			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			frame.setResizable(false);
			frame.setBounds(0, 0, width + 200, height + 63);
			frame.setLocationRelativeTo(null);
			
			JMenuBar menuBar = new JMenuBar();
	    	JMenu fileMenu = new JMenu("File");
	    	JMenu mapMenu = new JMenu("Map");
	    	JMenu history = new JMenu("History");
	    	
	    	mapMenu.setEnabled(!editor.zoneButtons.isEmpty());
	    	
	    	try {
	    		var lines = Files.readAllLines(Paths.get("editorhistory.txt"));
	    		for(int k = lines.size() - 1; k > -1; --k) {
	    			String line = lines.get(k);
	    			JMenuItem item = new JMenuItem(line);
					item.addActionListener(event -> {
						loadMap(line);
			    		((JFrame)editor.getTopLevelAncestor()).dispose();
					});
					history.add(item);
	    		}
			} catch (IOException e1) {
				JMenuItem offItem = new JMenuItem("No History");
				offItem.setEnabled(false);
				history.add(offItem);
			}
	    	
	    	fileMenu.add(newMenuItem("New map", 'N', true, event -> {
	    		String[] types = {"Normal", "Background"};
	    		int input = JOptionPane.showOptionDialog(null, "Make Normal or Background map?", "Map Type Chooser", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, types, types[0]);
	    		
    			var mapSizeString = (input == 0 ? JOptionPane.showInputDialog("Enter map size!", "10x10") : "14x10").split("x");
				int mapWidth = Integer.parseInt(mapSizeString[0]), bigWidth = mapWidth * 64;
				int mapHeight = Integer.parseInt(mapSizeString[1]), bigHeight = mapHeight * 64;
				var newEditor = new GuiEditor(JOptionPane.showInputDialog("Enter map name!", "mapname.deg"), "null", mapWidth, mapHeight, 0, 0, 0, 0);
				
				for(int yPos = 0; yPos < bigHeight; yPos += 64) {
					for(int xPos = 0; xPos < bigWidth; xPos += 64) {
						EditorZoneButton button = new EditorZoneButton(Main.normalZone.editorTexture.get(), newEditor);
						button.zoneID = "normalZone";
						button.zoneTexture = "normal";
						button.setBounds(xPos, yPos, 64, 64);
						newEditor.zoneButtons.add(button);
						newEditor.add(button);
					}
				}
				showEditorFrame(newEditor, bigWidth, bigHeight);
				((JFrame)editor.getTopLevelAncestor()).dispose();
	    	}));
	    	
	    	fileMenu.add(newMenuItem("Load map", 'L', true, event -> {
	    		var fileChooser = new JFileChooser("./maps/");
	    		
	    		if(fileChooser.showOpenDialog(null) == 0) {
	    			loadMap(fileChooser.getSelectedFile().getName());
	    			((JFrame)editor.getTopLevelAncestor()).dispose();
	    		}
	    	}));
	    	
	    	fileMenu.addSeparator();
	    	fileMenu.add(newMenuItem("Save map", 'S', !editor.zoneButtons.isEmpty(), event -> editor.saveMap()));
	    	fileMenu.add(newMenuItem("Reset map", '0', !editor.zoneButtons.isEmpty(), event -> {
	    		for(var localButton : editor.zoneButtons) {
			 		localButton.zoneID = "normalZone";
			 		localButton.zoneTexture = "normal";
			 		localButton.setIcon(Main.normalZone.editorTexture.get());
	    	};}));
	    	
	    	fileMenu.add(newMenuItem("Delete History", '0', true, event -> new File("editorhistory.txt").delete()));
	    	fileMenu.addSeparator();
	    	fileMenu.add(newMenuItem("Close map", '0', true, event -> {openEditor(); ((JFrame)editor.getTopLevelAncestor()).dispose();}));
	    	fileMenu.add(newMenuItem("Exit to menu", '0', true, event -> {frame.dispose(); GuiMenu.createMainFrame(false);}));
	    	fileMenu.add(newMenuItem("Exit app", '0', true, event -> System.exit(0)));
	    	
	    	mapMenu.add(newMenuItem("Map Properties", 'P', true, event -> GuiHelper.showNewGui(editor.mapProperties, "Map Properties", 350, 350)));
	    	mapMenu.add(newMenuItem("Map Information", 'I', true, event -> GuiHelper.showNewGui(new GuiInfo(editor), "Map Info", 350, 350)));
	    	
	    	menuBar.add(fileMenu);
	    	menuBar.add(history);
	    	menuBar.add(mapMenu);
	    	frame.setJMenuBar(menuBar);
			frame.setFocusable(true);
			frame.setVisible(true);
		});
	}
	
	private static JMenuItem newMenuItem(String text, char shortcut, boolean setEnabled, ActionListener listener) {
		JMenuItem item = new JMenuItem(text);
		if(shortcut != '0') {
			item.setAccelerator(KeyStroke.getKeyStroke(shortcut, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		}
		item.setEnabled(setEnabled);
		item.addActionListener(listener);
		return item;
	}
}