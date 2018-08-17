package frutty.gui.editor;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import frutty.gui.GuiMenu;
import frutty.gui.editor.GuiEditorProperties.GuiEditorInfo;
import frutty.gui.editor.GuiTextureSelector.TextureSelectorButton;
import frutty.tools.GuiHelper;
import frutty.tools.IOHelper;
import frutty.tools.Material;
import frutty.world.base.MapZoneBase;
import frutty.world.base.MapZoneTexturable;

public final class GuiEditor extends JPanel{
	public final List<EditorZoneButton> zoneButtons = new ArrayList<>();
	public final GuiEditorProperties mapProperties;
	public final TextureSelectorButton textureSelectorButton;
	public final JComboBox<String> zoneList = new JComboBox<>(FruttyMain.zoneRegistry.stream().map(zone -> zone.zoneName).toArray(String[]::new));
	
	private GuiEditor(String fileName, int width, int height, String skyName, String nextMap) {
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

	public static void openEmptyEditor() {
		showEditorFrame(new GuiEditor(null, 0, 0, null, null), "", 800, 600);
	}
	
	private void renderMap() {
		try(var output = IOHelper.newObjectOS("./maps/" + mapProperties.mapName + ".fmap")){
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
		
	 	JOptionPane.showMessageDialog(null, "Map rendered as: " + mapProperties.mapName + ".fmap");
	}
	
	private void saveMap() {
		try(var output = IOHelper.newBufferedWriter("./mapsrc/" + mapProperties.mapName + ".fmf")){
			output.write(Integer.toString(mapProperties.width) + '\n');
			output.write(Integer.toString(mapProperties.height) + '\n');
			output.write(mapProperties.skyName + '\n');
			output.write(mapProperties.nextMap + '\n');
			
			String[] textures = zoneButtons.stream().map(button -> button.zoneTexture).filter(texture -> texture != null).distinct().toArray(String[]::new);
			
			for(String texture : textures) {
				output.write(texture + ' ');
			}
			output.write("\n");
			
			for(var button : zoneButtons) {
				output.write(button.zoneID + (button.zoneTexture != null ? (" " + indexOf(textures, button.zoneTexture)) : "") + '\n');
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		JOptionPane.showMessageDialog(null, "Map saved as: " + mapProperties.mapName + ".fmf");
	}
	
	private static void loadMap(String fileName) {
		if(fileName != null && !fileName.isEmpty()) {
			try(var input = IOHelper.newBufferedReader("./mapsrc/" + fileName)){
				int mapWidth = Integer.parseInt(input.readLine());
				int mapHeight = Integer.parseInt(input.readLine());
				GuiEditor editor = new GuiEditor(fileName.substring(0, fileName.indexOf('.')), mapWidth, mapHeight, input.readLine(), input.readLine());
				String[] textureCache = input.readLine().split(" ");
				
				for(int y = 0; y < mapHeight; ++y) {
					for(int x = 0; x < mapWidth; ++x) {
						String[] zoneData = input.readLine().split(" ");
						MapZoneBase zoneFromName = FruttyMain.getZoneFromName(zoneData[0]);
						EditorZoneButton button = new EditorZoneButton(zoneFromName.editorTexture.get(), zoneData[0], x * 64, y * 64, editor);
						
						if(zoneData.length == 2) {
							int textureIndexFromCache = Integer.parseInt(zoneData[1]);
							button.zoneTexture = textureCache[textureIndexFromCache];
							button.setIcon(((MapZoneTexturable)zoneFromName).textureVariants.get()[Material.materialRegistry.get(textureCache[textureIndexFromCache]).index]);
						}
						
						editor.zoneButtons.add(button);
						editor.add(button);
					}
				}
				
				showEditorFrame(editor, fileName, mapWidth * 64, mapHeight * 64);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void newMap(GuiEditor editor) {
		String[] types = {"Normal", "Background"};
		int input = JOptionPane.showOptionDialog(null, "Make Normal or Background map?", "Map Type Chooser", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, types, types[0]);
		var mapSizeString = (input == 0 ? JOptionPane.showInputDialog("Enter map size!", "10x10") : "14x10").split("x");
		int mapWidth = Integer.parseInt(mapSizeString[0]), bigWidth = mapWidth * 64;
		int mapHeight = Integer.parseInt(mapSizeString[1]), bigHeight = mapHeight * 64;
		String mapName = JOptionPane.showInputDialog("Enter map name!", "mapname.deg");
		GuiEditor newEditor = new GuiEditor(mapName, mapWidth, mapHeight, "null", "null");
		
		if(mapName != null) {
			for(int yPos = 0; yPos < bigHeight; yPos += 64) {
				for(int xPos = 0; xPos < bigWidth; xPos += 64) {
					EditorZoneButton button = new EditorZoneButton(FruttyMain.normalZone.editorTexture.get(), "normalZone", xPos, yPos, newEditor);
					button.zoneTexture = "normal";
					newEditor.zoneButtons.add(button);
					newEditor.add(button);
				}
			}
		}
		showEditorFrame(newEditor, mapName, bigWidth, bigHeight);
		((JFrame)editor.getTopLevelAncestor()).dispose();
	}
	
	private static void showEditorFrame(GuiEditor editor, String titleMapName, int width, int height) {
		EventQueue.invokeLater(() -> {
			JFrame frame = new JFrame("Frutty Map Editor -- " + titleMapName);
			frame.setContentPane(editor);
			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			frame.setResizable(false);
			frame.setBounds(0, 0, width + 200, height + 63);
			frame.setLocationRelativeTo(null);
			
			JMenuBar menuBar = new JMenuBar();
	    	JMenu fileMenu = new JMenu("File");
	    	JMenu mapMenu = new JMenu("Map");
	    	
	    	mapMenu.setEnabled(!editor.zoneButtons.isEmpty());
	    	
	    	fileMenu.add(newMenuItem("New map", 'N', true, event -> newMap(editor)));
	    	fileMenu.add(newMenuItem("Load map", 'L', true, event -> {
	    		var fileChooser = new JFileChooser("./mapsrc/");
	    		
	    		if(fileChooser.showOpenDialog(null) == 0) {
	    			loadMap(fileChooser.getSelectedFile().getName());
	    			((JFrame)editor.getTopLevelAncestor()).dispose();
	    		}
	    	}));
	    	
	    	fileMenu.addSeparator();
	    	fileMenu.add(newMenuItem("Save map", 'S', !editor.zoneButtons.isEmpty(), event -> editor.saveMap()));
	    	fileMenu.add(newMenuItem("Render map", 'R', !editor.zoneButtons.isEmpty(), event -> editor.renderMap()));
	    	
	    	fileMenu.addSeparator();
	    	fileMenu.add(newMenuItem("Close map", '0', !editor.zoneButtons.isEmpty(), event -> {openEmptyEditor(); ((JFrame)editor.getTopLevelAncestor()).dispose();}));
	    	fileMenu.add(newMenuItem("Exit to menu", '0', true, event -> {frame.dispose(); GuiMenu.createMainFrame(false);}));
	    	fileMenu.add(newMenuItem("Exit app", '0', true, event -> System.exit(0)));
	    	
	    	mapMenu.add(newMenuItem("Map Properties", 'P', true, event -> GuiHelper.showNewGui(editor.mapProperties, "Map Properties", 350, 350)));
	    	mapMenu.add(newMenuItem("Map Information", 'I', true, event -> GuiHelper.showNewGui(new GuiEditorInfo(editor.zoneButtons), "Map Info", 350, 350)));
	    	
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
	
	static final class EditorZoneButton extends JButton implements MouseListener{
		public String zoneTexture, zoneID;
		private final GuiEditor editorInstance;
		
		public EditorZoneButton(ImageIcon texture, String zoneID, int x, int y, GuiEditor editor) {
			super(texture);
			editorInstance = editor;
			this.zoneID = zoneID;
			setBounds(x, y, 64, 64);
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