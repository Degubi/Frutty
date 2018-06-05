package frutty.gui.editor;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
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
import frutty.gui.editor.ZoneSelectorButton.TextureSelectorButton;

public final class GuiEditor extends JPanel implements MouseListener{
	public final ArrayList<JButton> zoneButtons = new ArrayList<>();
	private final GuiProperties mapProperties;
	protected final ZoneSelectorButton zoneSelectorButton;
	protected final TextureSelectorButton textureSelectorButton;
	
	private GuiEditor(String fileName, boolean isBackground, String skyName, int... data) {
		mapProperties = new GuiProperties(fileName, skyName, isBackground, data);
		setLayout(null);
		
		add(zoneSelectorButton = new ZoneSelectorButton(data[0], this));
		add(textureSelectorButton = new TextureSelectorButton(data[0], this));
	}
	
	@Override
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		
		if(!zoneButtons.isEmpty()) {
			graphics.setFont(GuiHelper.thiccFont);
			graphics.drawString("Current texture: " + textureSelectorButton.activeTexture, getWidth() - 175, 190);
		}
	}
	
	@Override
	public void mousePressed(MouseEvent event) {
		if(event.getSource() instanceof JButton) {
			JButton button = (JButton) event.getSource();
			
			if(event.getButton() == MouseEvent.BUTTON1) {
				button.setMnemonic(zoneSelectorButton.activeZone); button.setIcon(zoneSelectorButton.getIcon());
						
				if(Main.hasTextureInfo(zoneSelectorButton.activeZone)) {
					button.setIcon(Main.getEditorTextureVariants(button.getMnemonic())[textureSelectorButton.activeTextureIndex]);
					button.setActionCommand(textureSelectorButton.activeTexture);
				}else if(zoneSelectorButton.activeZone == 5) {
					mapProperties.setPlayer1Pos(button.getX(), button.getY());
				}else if(zoneSelectorButton.activeZone == 6) {
					mapProperties.setPlayer2Pos(button.getX(), button.getY());
				}
			}else if(event.getButton() == MouseEvent.BUTTON3 && !button.getActionCommand().equals("Zone Selector") && !button.getActionCommand().equals("Texture Selector")) {
				if(Main.hasTextureInfo(button.getMnemonic())) {
					button.setIcon(Main.getEditorTextureVariants(button.getMnemonic())[textureSelectorButton.activeTextureIndex]);
					button.setActionCommand(textureSelectorButton.activeTexture);
				}
			}
		}
	}

	public static void openEditor() {
		showEditorFrame(new GuiEditor("filename.deg", false, "null", 800, 600, 0, 0, 0, 0), 800, 600);
	}
	
	private void saveMap() {
		String mapName = mapProperties.getProperty(EnumProperty.MapName);
		try(ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream("./maps/" + mapName))){
	 		ArrayList<String> textures = new ArrayList<>();
			
	 		for(JButton writeButton : zoneButtons) {
	 			if(Main.hasTextureInfo(writeButton.getMnemonic())) {
	 				String texture = writeButton.getActionCommand();
	 				if(!texture.isEmpty() && !textures.contains(texture)) {
	 					textures.add(texture);
	 				}
	 			}
	 		}
	 		output.writeByte(textures.size());
	 		
	 		for(int k = 0; k < textures.size(); ++k) {
	 			output.writeUTF(textures.get(k));
	 		}
	 		
			output.writeUTF(mapProperties.getProperty(EnumProperty.SkyTexture));
	 		output.writeShort(mapProperties.getIntProperty(EnumProperty.MapWidth));
	 		output.writeShort(mapProperties.getIntProperty(EnumProperty.MapHeight));
	 		
		 	if(!mapProperties.getBooleanProperty(EnumProperty.IsBackground)) {
		 		output.writeShort(mapProperties.getIntProperty(EnumProperty.Player1PosX));
		 		output.writeShort(mapProperties.getIntProperty(EnumProperty.Player1PosY));
		 		output.writeShort(mapProperties.getIntProperty(EnumProperty.Player2PosX));
		 		output.writeShort(mapProperties.getIntProperty(EnumProperty.Player2PosY));
	 		}
		 	
	 		for(JButton writeButton : zoneButtons) {
	 			int zoneIndex = writeButton.getMnemonic();
				output.writeByte(zoneIndex);
				
	 			if(Main.hasTextureInfo(zoneIndex)) {
	 				output.writeByte(textures.indexOf(writeButton.getActionCommand()));
	 			}
	 		}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Path file = Paths.get("editorhistory.txt");
		try {
			List<String> lines = Files.readAllLines(file);
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
			try(ObjectInputStream input = new ObjectInputStream(new FileInputStream("./maps/" + fileName))){
				int textureCount = input.readByte();
				String[] textures = new String[textureCount];
				
				for(int k = 0; k < textureCount; ++k) {
					textures[k] = input.readUTF();
				}
				
				String skyName = input.readUTF();
				int mapWidth = input.readShort(), mapHeight = input.readShort();
				GuiEditor editor = fileName.startsWith("background") 
								 ? new GuiEditor(fileName, true, skyName, mapWidth, mapHeight, 0, 0, 0, 0)
								 : new GuiEditor(fileName, false, skyName, mapWidth, mapHeight, input.readShort(), input.readShort(), input.readShort(), input.readShort());
				
				for(int y = 0; y < mapHeight; ++y) {
					for(int x = 0; x < mapWidth; ++x) {
						Main.handleEditorReading(editor, input, x, y, textures);
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
	    		String input = JOptionPane.showInputDialog("Enter map size!", "10x10");
				
				if(input != null) {
					String[] mapSizeString = input.split("x");
					int mapWidth = Integer.parseInt(mapSizeString[0]), bigWidth = mapWidth * 64;
					int mapHeight = Integer.parseInt(mapSizeString[1]), bigHeight = mapHeight * 64;
					GuiEditor newEditor = new GuiEditor("filename.deg", false, "null", mapWidth, mapHeight, 0, 0, 0, 0);
					
					for(int yPos = 0; yPos < bigHeight; yPos += 64) {
						for(int xPos = 0; xPos < bigWidth; xPos += 64) {
							JButton button = new JButton(Main.normalZone.editorTexture.get());
							button.setMnemonic(0);
							button.setActionCommand("normal");
							button.addMouseListener(newEditor);
							button.setBounds(xPos, yPos, 64, 64);
							newEditor.zoneButtons.add(button);
							newEditor.add(button);
						}
					}
					showEditorFrame(newEditor, bigWidth, bigHeight);
					((JFrame)editor.getTopLevelAncestor()).dispose();
				}
	    	}));
	    	
	    	fileMenu.add(newMenuItem("Load map", 'L', true, event -> {
	    		JFileChooser fileChooser = new JFileChooser("./maps/");
	    		
	    		if(fileChooser.showOpenDialog(null) == 0) {
	    			loadMap(fileChooser.getSelectedFile().getName());
	    			((JFrame)editor.getTopLevelAncestor()).dispose();
	    		}
	    	}));
	    	
	    	fileMenu.addSeparator();
	    	fileMenu.add(newMenuItem("Save map", 'S', !editor.zoneButtons.isEmpty(), event -> editor.saveMap()));
	    	fileMenu.add(newMenuItem("Reset map", '0', !editor.zoneButtons.isEmpty(), event -> {
	    		for(JButton localButton : editor.zoneButtons) {
			 		localButton.setMnemonic(0);
			 		localButton.setActionCommand("normal");
			 		localButton.setIcon(Main.normalZone.editorTexture.get());
	    	};}));
	    	
	    	fileMenu.add(newMenuItem("Delete History", '0', true, event -> new File("editorhistory.txt").delete()));
	    	fileMenu.addSeparator();
	    	fileMenu.add(newMenuItem("Exit to menu", '0', true, event -> {frame.dispose(); GuiMenu.showMenu(false);}));
	    	fileMenu.add(newMenuItem("Exit app", '0', true, event -> System.exit(0)));
	    	
	    	mapMenu.add(newMenuItem("Map Properties", 'P', true, event -> GuiHelper.showNewFrame(editor.mapProperties, "Map Properties", WindowConstants.DISPOSE_ON_CLOSE, 350, 350)));
	    	mapMenu.add(newMenuItem("Map Information", 'I', true, event -> GuiHelper.showNewFrame(new GuiInfo(editor), "Map Info", WindowConstants.DISPOSE_ON_CLOSE, 350, 350)));
	    	
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
	
	@Override public void mouseClicked(MouseEvent event) {} @Override public void mouseReleased(MouseEvent e) {} @Override public void mouseEntered(MouseEvent e) {} @Override public void mouseExited(MouseEvent e) {}
}