package frutty.gui.editor;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
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
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import frutty.Main;
import frutty.gui.GuiHelper;
import frutty.gui.GuiMenu;
import frutty.gui.editor.GuiProperties.EnumProperty;

public final class GuiEditor extends JPanel implements MouseListener{
	final ArrayList<JButton> zoneButtons = new ArrayList<>();
	private final GuiProperties mapProperties;
	
	private final JButton zoneSelector, textureSelector;
	
	private int activeToolIndex = 1, activeTextureIndex = TextureSelector.indexOf("normal.png");
	private String activeTexture = "normal";
	
	private final int toolSelectorX;
	
	private GuiEditor(String fileName, boolean isBackground, String skyName, int... data) {
		mapProperties = new GuiProperties(fileName, skyName, isBackground, data);
		setLayout(null);
		
		toolSelectorX = data[0] * 64 + 80;
		
		zoneSelector = new JButton(ToolSelector.emptyTexture);
		zoneSelector.setToolTipText("Zone Selector Tool");
		zoneSelector.setMnemonic(1);
		zoneSelector.setActionCommand("Zone Selector");
		zoneSelector.addMouseListener(this);
		zoneSelector.setBounds(data[0] * 64 + 20, 80, 64, 64);
		
		textureSelector = new JButton(TextureSelector.bigTextures[TextureSelector.indexOf("normal.png")]);
		textureSelector.setToolTipText("Texture Selector Tool");
		textureSelector.setActionCommand("Texture Selector");
		textureSelector.addMouseListener(this);
		textureSelector.setBounds(data[0] * 64 + 20, 200, 128, 128);
		
		add(zoneSelector);
		add(textureSelector);
		
		add(GuiHelper.newButton("Save", data[0] * 64 + 12, data[1] * 64 - 100, this));
		add(GuiHelper.newButton("Exit", data[0] * 64 + 12, data[1] * 64 - 50, this));
		add(GuiHelper.newButton("Clear", data[0] * 64 + 12, data[1] * 64 - 150, this));
		add(GuiHelper.newButton("Properties", data[0] * 64 + 30, 30, this));
		
		GuiHelper.mapSizeCheck(data[0], data[1]);
	}
	
	@Override
	public void mousePressed(MouseEvent event) {
		if(event.getSource() instanceof JButton) {
			JButton button = (JButton) event.getSource();
			
			if(event.getButton() == MouseEvent.BUTTON1) {
			
				switch(button.getActionCommand()) {
					case "Zone Selector":
						EventQueue.invokeLater(() -> {
							JFrame frame = new JFrame("Tool Selector");
							frame.setContentPane(new ToolSelector(this));
							frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
							frame.setResizable(false);
							frame.setBounds(toolSelectorX + 500, 300, 128, 320);
							frame.setFocusable(true);
						    frame.setUndecorated(true);
							frame.setVisible(true);
						}); break;
						
					case "Exit": ((JFrame)getTopLevelAncestor()).dispose(); GuiMenu.showMenu(); break;
					case "Clear": 
					for(JButton localButton : zoneButtons) {
				 		localButton.setMnemonic(0);
				 		localButton.setIcon(ToolSelector.normalTexture);
				 	}break;
				 	
					case "Properties": GuiHelper.showNewFrame(mapProperties, "Map Properties", JFrame.DISPOSE_ON_CLOSE, 350, 350); break;
					case "Save": 
						try(ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream("./maps/" + mapProperties.getProperty(EnumProperty.MapName)))){
					 		ArrayList<String> textures = new ArrayList<>();
							
					 		for(JButton writeButton : zoneButtons) {
					 			if(EnumEditorZone.getFromIndex(writeButton.getMnemonic()).hasTextureInfo) {
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
								
					 			if(EnumEditorZone.getFromIndex(zoneIndex).hasTextureInfo) {
					 				output.writeByte(textures.indexOf(writeButton.getActionCommand()));
					 			}
					 		}
						} catch (IOException e) {
							e.printStackTrace();
						}
					 	JOptionPane.showMessageDialog(null, "Map saved as: " + mapProperties.getProperty(EnumProperty.MapName)); break;
				 	
					case "Texture Selector": 
						EventQueue.invokeLater(() -> {
							JFrame frame = new JFrame("Texture Selector");
							frame.setContentPane(new TextureSelector(this));
							frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
							frame.setResizable(false);
							frame.setBounds(0, 0, (TextureSelector.textureNames.length + 1) * 128, (TextureSelector.textureNames.length + 1) * 64);
							frame.setLocationRelativeTo(null);
							frame.setFocusable(true);
							frame.setVisible(true);
						}); break;
						
					default: button.setMnemonic(activeToolIndex); button.setIcon(zoneSelector.getIcon());
						if(EnumEditorZone.getFromIndex(activeToolIndex).hasTextureInfo) {
							button.setActionCommand(activeTexture);
						}else if(activeToolIndex == 5) {
							mapProperties.setPlayer1Pos(button.getX(), button.getY());
						}else if(activeToolIndex == 6) {
							mapProperties.setPlayer2Pos(button.getX(), button.getY());
						}
				}
			}else if(event.getButton() == MouseEvent.BUTTON3) {
				EnumEditorZone zone = EnumEditorZone.getFromIndex(button.getMnemonic());
				if(zone.hasTextureInfo) {
					button.setIcon(zone.editorTextures[activeTextureIndex]);
					button.setActionCommand(activeTexture);
				}
			}
		}
	}

	public static void openEditor() {
		int editorMode = JOptionPane.showOptionDialog(null, "Create new map or import", "Frutty map Editor", JOptionPane.YES_NO_CANCEL_OPTION, 1, null, new String[] {"New", "Import", "Cancel"}, null);
		if(editorMode == 0) {
			String input = JOptionPane.showInputDialog("Enter map size!", "10x10");
			
			if(input != null) {
				String[] mapSizeString = input.split("x");
				int mapWidth = Integer.parseInt(mapSizeString[0]), bigWidth = mapWidth * 64;
				int mapHeight = Integer.parseInt(mapSizeString[1]), bigHeight = mapHeight * 64;
				GuiEditor editor = new GuiEditor("filename.deg", false, "null", mapWidth, mapHeight, 0, 0, 0, 0);
				
				for(int yPos = 0; yPos < bigHeight; yPos += 64) {
					for(int xPos = 0; xPos < bigWidth; xPos += 64) {
						JButton button = new JButton(ToolSelector.normalTexture);
						button.setMnemonic(0);
						button.setActionCommand("normal");
						button.addMouseListener(editor);
						button.setBounds(xPos, yPos, 64, 64);
						editor.zoneButtons.add(button);
						editor.add(button);
					}
				}
				
				GuiHelper.showNewFrame(editor, "Frutty Map Editor", JFrame.DISPOSE_ON_CLOSE, bigWidth + 200, bigHeight + 32);
			}
		}else if(editorMode == 1) {
			String[] fileNames = new File("./maps/").list();
			String fileName = (String) JOptionPane.showInputDialog(null, "Select Map!", "Maps:", JOptionPane.QUESTION_MESSAGE, null, fileNames, fileNames[0]);
			
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
							EnumEditorZone.getFromIndex(input.readByte()).handleReading(editor, input, x, y, textures);
						}
					}
					GuiHelper.showNewFrame(editor, "Frutty Map Editor", JFrame.DISPOSE_ON_CLOSE, mapWidth * 64 + 200, mapHeight * 64 + 32);
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}else{   //Dont't close everything :(
			GuiMenu.showMenu();
		}
	}
	
	private static final class ToolSelector extends JPanel implements ActionListener{
		private final GuiEditor editor;
		
		private static final ImageIcon normalTexture = new ImageIcon("./textures/dev/normal.png");
		private static final ImageIcon emptyTexture = new ImageIcon("./textures/dev/empty.png");
		private static final ImageIcon appleTexture = new ImageIcon("./textures/dev/apple.png");
		private static final ImageIcon cherryTexture = new ImageIcon("./textures/dev/cherry.png");
		private static final ImageIcon spawnerTexture = new ImageIcon("./textures/dev/spawner.png");
		private static final ImageIcon chestTexture = new ImageIcon("./textures/dev/chest.png");
		private static final ImageIcon player1Texture = new ImageIcon("./textures/dev/player1.png");
		private static final ImageIcon player2Texture = new ImageIcon("./textures/dev/player2.png");
		private static final ImageIcon waterTexture = new ImageIcon("./textures/dev/water.png");
		private static final ImageIcon skyTexture = new ImageIcon("./textures/dev/sky.png");
		
		public ToolSelector(GuiEditor editor) {
			setLayout(null);
			this.editor = editor;
			
			add(GuiHelper.newEditorButton(0, normalTexture, "normal", 0, 0, this));
			add(GuiHelper.newEditorButton(1, emptyTexture, "empty", 64, 0, this));
			add(GuiHelper.newEditorButton(2, appleTexture, "apple", 0, 64, this));
			add(GuiHelper.newEditorButton(3, cherryTexture, "cherry", 64, 64, this));
			add(GuiHelper.newEditorButton(4, spawnerTexture, "spawner", 0, 128, this));
			add(GuiHelper.newEditorButton(7, chestTexture, "chest", 64, 128, this));
			add(GuiHelper.newEditorButton(5, player1Texture, "player1", 0, 192, this));
			add(GuiHelper.newEditorButton(6, player2Texture, "player2", 64, 192, this));
			add(GuiHelper.newEditorButton(8, waterTexture, "water", 0, 256, this));
			add(GuiHelper.newEditorButton(9, skyTexture, "sky", 64, 256, this));
		}
		
		@Override
		public void actionPerformed(ActionEvent event) {
			if(event.getSource() instanceof JButton) {
				JButton button = (JButton) event.getSource();
				editor.activeToolIndex = button.getMnemonic();
				editor.zoneSelector.setIcon(button.getIcon());
				((JFrame)getTopLevelAncestor()).dispose();
			}
		}
	}
	
	public static final class TextureSelector extends JPanel implements ActionListener{
		private final GuiEditor editor;
		
		public static final ImageIcon[] bigTextures, normalTextures, appleTextures, cherryTextures, chestTextures;
		private static final String[] textureNames;
		
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
			
			for(int k = 0; k < count; ++k) {
				if(textureNames[k].endsWith(".png")) {
					ImageIcon nrm = new ImageIcon("./textures/map/" + textureNames[k]);
					normalTextures[k] = new ImageIcon((nrm).getImage().getScaledInstance(64, 64, Image.SCALE_DEFAULT));
					bigTextures[k] = new ImageIcon((nrm).getImage().getScaledInstance(128, 128, Image.SCALE_DEFAULT));
					appleTextures[k] = combineTextures(nrm, appleTexture);
					cherryTextures[k] = combineTextures(nrm, cherryTexture);
					chestTextures[k] = combineTextures(nrm, chestTexture);
				}
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
			
			int xPosition = 10, yPosition = 20, index = 0;
			JButton[] buttons = new JButton[textureNames.length];
			
			for(String texture : textureNames) {
				if(texture.endsWith(".png")) {
					JButton button = new JButton();
					button.setActionCommand(texture.substring(0, texture.length() - 4));
					button.setBounds(xPosition, yPosition, 128, 128);
					button.setIcon(bigTextures[indexOf(texture)]);
					button.addActionListener(this);
					xPosition += 138;
					
					if(xPosition > 600) {
						xPosition = 10;
						yPosition += 138;
					}
					buttons[index++] = button;
					add(button);
				}
			}
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			if(event.getSource() instanceof JButton) {
				JButton button = (JButton) event.getSource();
				
				editor.activeTexture = button.getActionCommand();
				editor.textureSelector.setIcon(button.getIcon());
				editor.activeTextureIndex = indexOf(button.getActionCommand() + ".png");
				editor.repaint();
				((JFrame)getTopLevelAncestor()).dispose();
			}
		}
	}
	
	@Override public void mouseClicked(MouseEvent event) {} @Override public void mouseReleased(MouseEvent e) {} @Override public void mouseEntered(MouseEvent e) {} @Override public void mouseExited(MouseEvent e) {}
}