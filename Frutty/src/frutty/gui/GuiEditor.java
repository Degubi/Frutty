package frutty.gui;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
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

import frutty.FruttyMain;
import frutty.gui.GuiEditorProperties.GuiEditorInfo;
import frutty.gui.GuiTextureSelector.TextureSelectorButton;
import frutty.tools.GuiHelper;
import frutty.world.base.MapZoneTexturable;

public final class GuiEditor extends JPanel{
	public final ArrayList<EditorZoneButton> zoneButtons = new ArrayList<>();
	public final GuiEditorProperties mapProperties;
	public final TextureSelectorButton textureSelectorButton;
	public final JComboBox<String> zoneList = new JComboBox<>(FruttyMain.zoneRegistry.stream().map(zone -> zone.zoneName).toArray(String[]::new));
	
	private GuiEditor(String fileName, String skyName, int width, int height, String nextMap) {
		setLayout(null);
		
		if(fileName != null) {
			mapProperties = new GuiEditorProperties(fileName, skyName, width, height, nextMap);
			zoneList.setSelectedItem("normalZone");
			zoneList.setBounds(width * 64 + 20, 80, 120, 30);
			
			add(zoneList);
			add(textureSelectorButton = new TextureSelectorButton(width, this));
		}else{
			mapProperties = null;
			textureSelectorButton = null;
		}
	}
	
	@Override
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		
		if(!zoneButtons.isEmpty()) {
			graphics.setFont(GuiHelper.thiccFont);
			graphics.drawString("Current texture: " + textureSelectorButton.activeMaterial.name, getWidth() - 175, 190);
		}
	}

	public static void openEditor() {
		showEditorFrame(new GuiEditor(null, null, 0, 0, null), 800, 600);
	}
	
	private void saveMap() {
		try(var output = new ObjectOutputStream(new FileOutputStream("./maps/" + mapProperties.mapName))){
	 		var zoneIDCache = zoneButtons.stream().map(button -> button.zoneID).distinct().toArray(String[]::new);
	 		var textureCache = zoneButtons.stream().map(button -> button.zoneTexture).filter(texture -> texture != null).distinct().toArray(String[]::new);
	 		
	 		output.writeObject(zoneIDCache);
	 		output.writeObject(textureCache);
			output.writeUTF(mapProperties.skyName);
	 		output.writeShort(mapProperties.width);
	 		output.writeShort(mapProperties.height);
	 		output.writeUTF(mapProperties.nextMap);
	 		
	 		for(EditorZoneButton writeButton : zoneButtons) {
	 			output.writeByte(indexOf(zoneIDCache, writeButton.zoneID));
	 			
	 			if(FruttyMain.getZoneFromName(writeButton.zoneID) instanceof MapZoneTexturable) {
	 				output.writeByte(indexOf(textureCache, writeButton.zoneTexture));
	 			}
	 		}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	 	JOptionPane.showMessageDialog(null, "Map saved as: " + mapProperties.mapName);
	}
	
	private static void loadMap(String fileName) {
		if(fileName != null && !fileName.isEmpty()) {
			try(var input = new ObjectInputStream(new FileInputStream("./maps/" + fileName))){
				String[] zoneIDCache = (String[]) input.readObject();
				String[] textureCache = (String[]) input.readObject();
				String skyName = input.readUTF();
				int mapWidth = input.readShort(), mapHeight = input.readShort();
				
				GuiEditor editor = new GuiEditor(fileName, skyName, mapWidth, mapHeight, input.readUTF());
				
				for(int y = 0; y < mapHeight; ++y) {
					for(int x = 0; x < mapWidth; ++x) {
						String zoneID = zoneIDCache[input.readByte()];
						FruttyMain.getZoneFromName(zoneID).handleEditorReading(editor, zoneID, input, x, y, textureCache);
					}
				}
				showEditorFrame(editor, mapWidth * 64, mapHeight * 64);
				
			} catch (IOException | ClassNotFoundException e) {
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
							EditorZoneButton button = new EditorZoneButton(FruttyMain.normalZone.editorTexture.get(), newEditor);
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
			 		localButton.setIcon(FruttyMain.normalZone.editorTexture.get());
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
	
	private static<T> int indexOf(T[] array, T element) {
		for(int k = 0; k < array.length; ++k) {
			if(array[k].equals(element)) {
				return k;
			}
		}
		return -1;
	}
	
	public static final class EditorZoneButton extends JButton implements MouseListener{
		public String zoneTexture, zoneID;
		private final GuiEditor editorInstance;
		
		public EditorZoneButton(ImageIcon texture, GuiEditor editor) {
			super(texture);
			editorInstance = editor;
			
			addMouseListener(this);
		}
		
		@Override
		public void mousePressed(MouseEvent event) {
			EditorZoneButton button = (EditorZoneButton)event.getComponent();
			int pressedButton = event.getButton();
			
			if(pressedButton == MouseEvent.BUTTON1) {
				String activeZoneName = (String) editorInstance.zoneList.getSelectedItem();
				button.zoneID = activeZoneName; button.setIcon(FruttyMain.getZoneFromName(activeZoneName).editorTexture.get());
				
				if(FruttyMain.getZoneFromName(activeZoneName) instanceof MapZoneTexturable) {
					button.setIcon(((MapZoneTexturable)FruttyMain.getZoneFromName(button.zoneID)).textureVariants.get()[editorInstance.textureSelectorButton.activeMaterial.index]);
					button.zoneTexture = editorInstance.textureSelectorButton.activeMaterial.name;
				}else{
					button.zoneTexture = null;
				}
				
			}else if(pressedButton == MouseEvent.BUTTON3) {
				if(FruttyMain.getZoneFromName(button.zoneID) instanceof MapZoneTexturable) {
					button.setIcon(((MapZoneTexturable)FruttyMain.getZoneFromName(button.zoneID)).textureVariants.get()[editorInstance.textureSelectorButton.activeMaterial.index]);
					button.zoneTexture = editorInstance.textureSelectorButton.activeMaterial.name;
				}
			}
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {}
		@Override
		public void mouseReleased(MouseEvent e) {}
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
	}
}