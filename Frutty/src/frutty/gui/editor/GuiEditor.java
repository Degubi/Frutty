package frutty.gui.editor;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
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

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
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

public final class GuiEditor extends JPanel implements MouseListener{
	public final ArrayList<JButton> zoneButtons = new ArrayList<>();
	private final GuiProperties mapProperties;
	protected final JButton zoneSelector = new JButton(Main.emptyZone.editorTexture.get()), textureSelector = new JButton(TextureSelector.bigTextures[TextureSelector.indexOf("normal.png")]);
	protected String activeTexture = "normal";
	private final int toolSelectorX;
	
	private static JFrame toolSelectorFrame;
	
	private GuiEditor(String fileName, boolean isBackground, String skyName, int... data) {
		mapProperties = new GuiProperties(fileName, skyName, isBackground, data);
		setLayout(null);
		
		toolSelectorX = data[0] * 64 + 80;
		
		zoneSelector.setToolTipText("Zone Selector Tool");
		zoneSelector.setMnemonic(1);
		zoneSelector.setActionCommand("Zone Selector");
		zoneSelector.addMouseListener(this);
		zoneSelector.setBounds(data[0] * 64 + 20, 80, 64, 64);
		
		textureSelector.setToolTipText("Texture Selector Tool");
		textureSelector.setActionCommand("Texture Selector");
		textureSelector.setMnemonic(TextureSelector.indexOf("normal.png"));
		textureSelector.addMouseListener(this);
		textureSelector.setBounds(data[0] * 64 + 20, 200, 128, 128);
		
		add(zoneSelector);
		add(textureSelector);
	}
	
	@Override
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		
		graphics.setFont(GuiHelper.thiccFont);
		graphics.drawString("Current texture: " + activeTexture, toolSelectorX - 70, 190);
	}
	
	@Override
	public void mousePressed(MouseEvent event) {
		if(event.getSource() instanceof JButton) {
			JButton button = (JButton) event.getSource();
			
			if(event.getButton() == MouseEvent.BUTTON1) {
				switch(button.getActionCommand()) {
					case "Zone Selector":
						EventQueue.invokeLater(() -> {
							if(toolSelectorFrame == null) {
								toolSelectorFrame = new JFrame("Tool Selector");
								toolSelectorFrame.setContentPane(new ToolSelector(this));
								toolSelectorFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
								toolSelectorFrame.setResizable(false);
								toolSelectorFrame.setBounds(toolSelectorX + 500, 300, 128, 500);
								toolSelectorFrame.setFocusable(true);
								toolSelectorFrame.setUndecorated(true);
							}
							toolSelectorFrame.setVisible(true);
						}); break;
					case "Texture Selector": 
						EventQueue.invokeLater(() -> {
							JFrame frame = new JFrame("Texture Selector");
							frame.setContentPane(new TextureSelector(this));
							frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
							frame.setResizable(false);
							frame.setBounds(0, 0, (TextureSelector.textureNames.length + 1) * 128, (TextureSelector.textureNames.length + 1) * 64);
							frame.setLocationRelativeTo(null);
							frame.setFocusable(true);
							frame.setVisible(true);
						}); break;
						
					default: if(toolSelectorFrame != null) {
						toolSelectorFrame.dispose();
						toolSelectorFrame = null;
					}
						int activeToolIndex = zoneSelector.getMnemonic();
						button.setMnemonic(activeToolIndex); button.setIcon(zoneSelector.getIcon());
						
						if(Main.hasTextureInfo(activeToolIndex)) {
							button.setActionCommand(activeTexture);
						}else if(activeToolIndex == 5) {
							mapProperties.setPlayer1Pos(button.getX(), button.getY());
						}else if(activeToolIndex == 6) {
							mapProperties.setPlayer2Pos(button.getX(), button.getY());
						}
				}
			}else if(event.getButton() == MouseEvent.BUTTON3 && !button.getActionCommand().equals("Zone Selector") && !button.getActionCommand().equals("Texture Selector")) {
				if(Main.hasTextureInfo(button.getMnemonic())) {
					button.setIcon(Main.getEditorTextureVariants(button.getMnemonic())[textureSelector.getMnemonic()]);
					button.setActionCommand(activeTexture);
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
	    	mapMenu.add(newMenuItem("Map Information", 'I', true, event -> GuiHelper.showNewFrame(new InformationMenu(editor), "Map Info", WindowConstants.DISPOSE_ON_CLOSE, 350, 350)));
	    	
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
	
	public static final class ToolSelector extends JPanel implements ActionListener{
		private final GuiEditor editor;
		
		public static final ImageIcon player1Texture = new ImageIcon("./textures/dev/player1.png");
		public static final ImageIcon player2Texture =  new ImageIcon("./textures/dev/player2.png");
		
		public ToolSelector(GuiEditor editor) {
			setLayout(null);
			this.editor = editor;
			
			add(newEditorButton(0, Main.normalZone.editorTexture.get(), 0, 0, this));
			add(newEditorButton(1, Main.emptyZone.editorTexture.get(), 64, 0, this));
			add(newEditorButton(2, Main.appleZone.editorTexture.get(), 0, 64, this));
			add(newEditorButton(3, Main.cherryZone.editorTexture.get(), 64, 64, this));
			add(newEditorButton(4, Main.spawnerZone.editorTexture.get(), 0, 128, this));
			add(newEditorButton(7, Main.chestZone.editorTexture.get(), 64, 128, this));
			add(newEditorButton(5, player1Texture, 0, 192, this));
			add(newEditorButton(6, player2Texture, 64, 192, this));
			add(newEditorButton(8, Main.waterZone.editorTexture.get(), 0, 256, this));
			add(newEditorButton(9, Main.skyZone.editorTexture.get(), 64, 256, this));
			
			var entries = Main.zoneRegistry.entrySet();
			int xPos = 0, yPos = 320, counter = 0;
			for(var entry : entries) {
				@SuppressWarnings("boxing")
				int id = entry.getKey();
				if(id > 20) {
					add(newEditorButton(id, entry.getValue().editorTexture.get(), xPos, yPos, this));
				}
				++counter;
				
				if(counter % 2 == 0) {
					xPos = 0;
					yPos += 64;
				}else{
					xPos = 64;
				}
			}
		}
		
		private static JButton newEditorButton(int index, ImageIcon icon, int x, int y, ActionListener listener) {
			JButton butt = new JButton(icon);
			butt.setBounds(x, y, 64, 64);
			butt.setMnemonic(index);
			butt.addActionListener(listener);
			return butt;
		}
		
		@Override
		public void actionPerformed(ActionEvent event) {
			if(event.getSource() instanceof JButton) {
				JButton button = (JButton) event.getSource();
				editor.zoneSelector.setMnemonic(button.getMnemonic());
				editor.zoneSelector.setIcon(button.getIcon());
				((JFrame)getTopLevelAncestor()).dispose();
			}
		}
	}
	
	public static final class TextureSelector extends JPanel implements ActionListener{
		private final GuiEditor editor;
		
		public static final ImageIcon[] bigTextures, normalTextures, appleTextures, cherryTextures, chestTextures;
		static final String[] textureNames;
		
		static {
			String[] nop1 = new File("./textures/map").list(), nop2 = new String[nop1.length];
			
			int count = 0;
			for(int k = 0; k < nop1.length; ++k) {
				if(nop1[k].endsWith(".png")) {
					nop2[count++] = nop1[k];
				}
			}
			
			textureNames = new String[count];
			System.arraycopy(nop2, 0, textureNames, 0, count);
			
			bigTextures = new ImageIcon[count];
			normalTextures = new ImageIcon[count];
			appleTextures = new ImageIcon[count];
			cherryTextures = new ImageIcon[count];
			chestTextures = new ImageIcon[count];
			
			BufferedImage appleTexture = Main.loadTexture("fruit", "apple.png");
			BufferedImage cherryTexture = Main.loadTexture("fruit", "cherry.png");
			BufferedImage chestTexture = Main.loadTexture("map/special", "chest.png");
				
			for(int k = 0; k < textureNames.length; ++k) {
				ImageIcon nrm = new ImageIcon("./textures/map/" + textureNames[k]);
				normalTextures[k] = new ImageIcon((nrm).getImage().getScaledInstance(64, 64, Image.SCALE_DEFAULT));
				bigTextures[k] = new ImageIcon((nrm).getImage().getScaledInstance(128, 128, Image.SCALE_DEFAULT));
				appleTextures[k] = combineTextures(nrm, appleTexture);
				cherryTextures[k] = combineTextures(nrm, cherryTexture);
				chestTextures[k] = combineTextures(nrm, chestTexture);
			}
		}
		
		private static ImageIcon combineTextures(ImageIcon normalTexture, BufferedImage overlay) {
			BufferedImage toReturn = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
			Graphics graph = toReturn.createGraphics();
			graph.drawImage(normalTexture.getImage(), 0, 0, 64, 64, null);
			graph.drawImage(overlay, 0, 0, 64, 64, null);
			return new ImageIcon(toReturn);
		}
		
		public static int indexOf(String name) {
			for(int k = 0; k < textureNames.length; ++k) {
				if(textureNames[k].equals(name)) {
					return k;
				}
			}
			return -1;
		}
		
		public TextureSelector(GuiEditor ed) {
			editor = ed;
			setLayout(null);
			
			for(int index = 0, xPosition = 10, yPosition = 20; index < bigTextures.length; ++index) {
				JButton button = new JButton(bigTextures[index]);
				button.setActionCommand(textureNames[index].substring(0, textureNames[index].length() - 4));
				button.setBounds(xPosition, yPosition, 128, 128);
				button.addActionListener(this);
				xPosition += 138;
				
				if(xPosition > 600) {
					xPosition = 10;
					yPosition += 138;
				}
				add(button);
			}
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			if(event.getSource() instanceof JButton) {
				JButton button = (JButton) event.getSource();
				
				editor.activeTexture = button.getActionCommand();
				editor.textureSelector.setIcon(button.getIcon());
				editor.textureSelector.setMnemonic(indexOf(button.getActionCommand() + ".png"));
				editor.repaint();
				((JFrame)getTopLevelAncestor()).dispose();
			}
		}
	}
	
	private static final class InformationMenu extends JPanel{
		private final GuiEditor editor;
		private final String textureCount, textureSize;
		@SuppressWarnings("rawtypes")
		private final JList textureList;
		
		public InformationMenu(GuiEditor edit) {
			setLayout(null);
			editor = edit;
			
			ArrayList<String> textures = new ArrayList<>();
			int size = 0;
			
	 		for(JButton writeButton : editor.zoneButtons) {
	 			if(Main.hasTextureInfo(writeButton.getMnemonic())) {
	 				String texture = "textures/map/" + writeButton.getActionCommand() + ".png";
	 				if(!textures.contains(texture)) {
	 					size += new File("./" + texture).length();
	 					textures.add(texture);
	 				}
	 			}
	 		}
	 		textureSize = "Texture size: " + size + " bytes";
	 		textureCount = "Texture Count: " + textures.size();
	 		
	 		textureList = new JList<>(textures.toArray());
			textureList.setBounds(60, 150, 200, 120);
			textureList.setBorder(GuiHelper.menuBorder);
	 		
	 		add(textureList);
		}
		
		@Override
		protected void paintComponent(Graphics graphics) {
			super.paintComponent(graphics);
			
			graphics.setFont(GuiHelper.thiccFont);
			graphics.drawString(textureCount, 20, 20);
			graphics.drawString(textureSize, 20, 40);
			graphics.drawString("Textures Used:", 20, 145);
		}
	}
	
	@Override public void mouseClicked(MouseEvent event) {} @Override public void mouseReleased(MouseEvent e) {} @Override public void mouseEntered(MouseEvent e) {} @Override public void mouseExited(MouseEvent e) {}
}