package frutty.gui;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

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
import frutty.gui.components.EditorZoneButton;
import frutty.gui.components.TextureSelectorButton;
import frutty.tools.GuiHelper;
import frutty.world.interfaces.ITexturable;
import frutty.world.interfaces.MapZoneBase;

public final class GuiEditor extends JPanel{
	public final ArrayList<EditorZoneButton> zoneButtons = new ArrayList<>();
	public final GuiEditorProperties mapProperties;
	public final TextureSelectorButton textureSelectorButton;
	public final JComboBox<String> zoneList;
	
	private GuiEditor(String fileName, String skyName, int width, int height, String nextMap) {
		setLayout(null);
		
		if(fileName != null) {
			mapProperties = new GuiEditorProperties(fileName, skyName, width, height, nextMap);
			zoneList = new JComboBox<>(zoneNames());
			zoneList.setSelectedItem("normalZone");
			zoneList.setBounds(width * 64 + 20, 80, 120, 30);
			
			add(zoneList);
			add(textureSelectorButton = new TextureSelectorButton(width, this));
		}else{
			mapProperties = null;
			zoneList = null;
			textureSelectorButton = null;
		}
	}
	
	public static String[] zoneNames() {
		String[] names = new String[Main.zoneRegistry.size()];
		
		int index = 0;
		for(MapZoneBase zones : Main.zoneRegistry) {
			names[index++] = zones.zoneName;
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
		showEditorFrame(new GuiEditor(null, null, 0, 0, null), 800, 600);
	}
	
	private void saveMap() {
		var mapName = mapProperties.mapName;
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
	 		
			output.writeUTF(mapProperties.skyName);
	 		output.writeShort(mapProperties.width);
	 		output.writeShort(mapProperties.height);
	 		output.writeUTF(mapProperties.nextMap);
	 		
	 		for(var writeButton : zoneButtons) {
	 			output.writeByte(zoneIDs.indexOf(writeButton.zoneID));
	 			
	 			if(Main.getZoneFromName(writeButton.zoneID) instanceof ITexturable) {
	 				output.writeByte(textures.indexOf(writeButton.zoneTexture));
	 			}
	 		}
		} catch (IOException e) {
			e.printStackTrace();
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
				GuiEditor editor = new GuiEditor(fileName, skyName, mapWidth, mapHeight, input.readUTF());
				
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
	    	
	    	mapMenu.setEnabled(!editor.zoneButtons.isEmpty());
	    	
	    	fileMenu.add(newMenuItem("New map", 'N', true, event -> {
	    		String[] types = {"Normal", "Background"};
	    		int input = JOptionPane.showOptionDialog(null, "Make Normal or Background map?", "Map Type Chooser", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, types, types[0]);
	    		
    			var mapSizeString = (input == 0 ? JOptionPane.showInputDialog("Enter map size!", "10x10") : "14x10").split("x");
				int mapWidth = Integer.parseInt(mapSizeString[0]), bigWidth = mapWidth * 64;
				int mapHeight = Integer.parseInt(mapSizeString[1]), bigHeight = mapHeight * 64;
				
				String mapName = JOptionPane.showInputDialog("Enter map name!", "mapname.deg");
				
				var newEditor = new GuiEditor(mapName, "null", mapWidth, mapHeight, "null");
				
				if(mapName != null) {
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
	    	
	    	fileMenu.addSeparator();
	    	fileMenu.add(newMenuItem("Close map", '0', !editor.zoneButtons.isEmpty(), event -> {openEditor(); ((JFrame)editor.getTopLevelAncestor()).dispose();}));
	    	fileMenu.add(newMenuItem("Exit to menu", '0', true, event -> {frame.dispose(); GuiMenu.createMainFrame(false);}));
	    	fileMenu.add(newMenuItem("Exit app", '0', true, event -> System.exit(0)));
	    	
	    	mapMenu.add(newMenuItem("Map Properties", 'P', true, event -> GuiHelper.showNewGui(editor.mapProperties, "Map Properties", 350, 350)));
	    	mapMenu.add(newMenuItem("Map Information", 'I', true, event -> GuiHelper.showNewGui(new GuiEditorInfo(editor), "Map Info", 350, 350)));
	    	
	    	menuBar.add(fileMenu);
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