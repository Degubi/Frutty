package frutty.gui;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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

public class GuiEditor extends JPanel implements MouseListener{
	private final ArrayList<JButton> zoneButtons = new ArrayList<>();
	private final GuiProperties properties;
	
	private GuiEditor(String fileName, boolean isBackground, String textureName, int... data) {
		properties = new GuiProperties(fileName, textureName, isBackground, data);
		setLayout(null);
		
		add(GuiHelper.newButton("Save", data[0] * 64 + 12, data[1] * 64 - 100, this));
		add(GuiHelper.newButton("Exit", data[0] * 64 + 12, data[1] * 64 - 50, this));
		add(GuiHelper.newButton("Clear", data[0] * 64 + 12, 300, this));
		add(GuiHelper.newButton("Properties", data[0] * 64 + 12, 100, this));
	}
	
	public static void openEditor() {
		int editorMode = JOptionPane.showOptionDialog(null, "Create new map or import", "Frutty map Editor", JOptionPane.YES_NO_CANCEL_OPTION, 1, null, new String[] {"New", "Import", "Cancel"}, null);
		if(editorMode == 0) {
			String input = JOptionPane.showInputDialog("Enter map size!", "10x10");
			
			if(input != null) {
				String[] mapSizeString = input.split("x");
				int mapWidth = Integer.parseInt(mapSizeString[0]), bigWidth = mapWidth * 64;
				int mapHeight = Integer.parseInt(mapSizeString[1]), bigHeight = mapHeight * 64;
				
				if(mapHeight > GuiHelper.recommendedMaxMapHeight || mapWidth > GuiHelper.recommendedMaxMapWidth) {
					JOptionPane.showMessageDialog(null, "Map size is very big, things may go offscreen!");
				}
				
				GuiEditor editor = new GuiEditor("filename.deg", false, "normal", mapWidth, mapHeight, 0, 0, 0, 0);
				
				for(int yPos = 0; yPos < bigHeight; yPos += 64) {
					for(int xPos = 0; xPos < bigWidth; xPos += 64) {
						JButton button = new JButton(GuiEditor.normalTexture);
						button.setMnemonic(0);
						button.addMouseListener(editor);
						button.setBounds(xPos, yPos, 64, 64);
						editor.zoneButtons.add(button);
						editor.add(button);
					}
				}
				
				GuiHelper.showNewFrame(editor, "Frutty Map Editor", JFrame.DISPOSE_ON_CLOSE, bigWidth + 156, bigHeight + 32);
			}
		}else if(editorMode == 1) {
			String[] fileNames = new File("./maps/").list();
			String fileName = (String) JOptionPane.showInputDialog(null, "Select Map!", "Maps:", JOptionPane.QUESTION_MESSAGE, null, fileNames, fileNames[0]);
			
			if(fileName != null && !fileName.isEmpty()) {
				try(ObjectInputStream input = new ObjectInputStream(new FileInputStream("./maps/" + fileName))){
					String textureName = input.readUTF();
					int mapWidth = input.readShort(), mapHeight = input.readShort();
					GuiEditor editor;
					
					if(fileName.startsWith("background")) {
						editor = new GuiEditor(fileName, true, textureName, mapWidth, mapHeight, 0, 0, 0, 0);
					}else{
						editor = new GuiEditor(fileName, false, textureName, mapWidth, mapHeight, input.readShort(), input.readShort(), input.readShort(), input.readShort());
					}
					
					for(int y = 0; y < mapHeight; ++y) {
						for(int x = 0; x < mapWidth; ++x) {
							switch(input.readByte()) {
								case 1: editor.addNewButton(dugTexture, x, y, 1); break;
								case 2: editor.addNewButton(appleTexture, x, y, 2); break;
								case 3: editor.addNewButton(cherryTexture, x, y, 3); break;
								case 4: editor.addNewButton(spawnerTexture, x, y, 4); break;
								case 5: editor.addNewButton(player1Texture, x, y, 5); break;
								case 6: editor.addNewButton(player2Texture, x, y, 6); break;
								default: editor.addNewButton(normalTexture, x, y, 0);
							}
						}
					}
					GuiHelper.showNewFrame(editor, "Frutty Map Editor", JFrame.DISPOSE_ON_CLOSE, mapWidth * 64 + 156, mapHeight * 64 + 32);
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}else {
			GuiMenu.showMenu();
		}
	}
	
	@Override
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		
		graphics.drawString("Alt: Spawner", properties.getMapWidth() * 64 + 25, 400);
		graphics.drawString("CTRL: Player1", properties.getMapWidth() * 64 + 25, 440);
		graphics.drawString("Shift: Player2", properties.getMapWidth() * 64 + 25, 480);
	}

	@Override
	public void mousePressed(MouseEvent event) {
		if(event.getComponent() instanceof JButton) {
			JButton button = (JButton) event.getComponent();
			
			boolean isAnyDown = false;
			if(event.isShiftDown()) {
				button.setMnemonic(5); properties.setPlayer1Pos(button.getX(), button.getY()); button.setIcon(player1Texture);
				isAnyDown = true;
			}
			if(event.isControlDown()) {
				button.setMnemonic(6); properties.setPlayer2Pos(button.getX(), button.getY()); button.setIcon(player2Texture);
				isAnyDown = true;
			}
			if(event.isAltDown()) {
				button.setMnemonic(4); button.setIcon(spawnerTexture);
				isAnyDown = true;
			}
			
			if(event.getButton() == MouseEvent.BUTTON1) {
				String command = button.getActionCommand();
				
				if(command.isEmpty()) {
					switch(button.getMnemonic()) {
					 	case 0: button.setMnemonic(1); button.setIcon(dugTexture); break;
					 	case 1: button.setMnemonic(2); button.setIcon(appleTexture); break;
					 	case 2: button.setMnemonic(3); button.setIcon(cherryTexture); break;
					 	case 3: button.setMnemonic(0); button.setIcon(normalTexture); break;
					 	case 4: case 5: case 6: 
					 		if(!isAnyDown) {
					 			button.setMnemonic(0); button.setIcon(normalTexture);
					 		}
					 		break;
				 	}
					
				}else if(command.equals("Exit")) {
					((JFrame)getTopLevelAncestor()).dispose(); GuiMenu.showMenu();
				}else if(command.equals("Clear")) {
					for(JButton localButton : zoneButtons) {
				 		localButton.setMnemonic(0);
				 		localButton.setIcon(normalTexture);
				 	}
				}else if(command.equals("Properties")) {
					GuiHelper.showNewFrame(properties, "Map Properties", JFrame.DISPOSE_ON_CLOSE, 350, 250);
				}else if(command.equals("Save")) {
					try(ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream("./maps/" + properties.getMapName()))){
				 		output.writeUTF(properties.getMapTextureName());
				 		output.writeShort(properties.getMapWidth());
				 		output.writeShort(properties.getMapHeight());
				 		
					 	if(!properties.isBackgroundMap()) {
					 		output.writeShort(properties.getPlayer1PosX());
					 		output.writeShort(properties.getPlayer1PosY());
					 		output.writeShort(properties.getPlayer2PosX());
					 		output.writeShort(properties.getPlayer2PosY());
				 		}
				 		for(JButton writeButton : zoneButtons) {
				 			output.writeByte(writeButton.getMnemonic());
				 		}
					} catch (IOException e) {
						e.printStackTrace();
					}
				 	JOptionPane.showMessageDialog(null, "Map saved as: " + properties.getMapName());
				}
			}else if(event.getButton() == MouseEvent.BUTTON3) {    //Right click
				switch(button.getMnemonic()) {
					case 0: button.setMnemonic(3); button.setIcon(cherryTexture); break;
				 	case 1: button.setMnemonic(0); button.setIcon(normalTexture); break;
				 	case 2: button.setMnemonic(1); button.setIcon(dugTexture); break;
				 	case 3: button.setMnemonic(2); button.setIcon(appleTexture); break;
				 	case 4: case 5: case 6: 
				 		if(!isAnyDown) {
				 			button.setMnemonic(0); button.setIcon(normalTexture);
				 		}
				 		break;
				}
			}
		}
	}
	
	private void addNewButton(ImageIcon texture, int x, int y, int data) {
		JButton button = new JButton(texture);
		button.setBounds(x * 64, y * 64, 64, 64);
		button.setMnemonic(data);
		button.addMouseListener(this);
		zoneButtons.add(button);
		add(button);
	}
	
	@Override public void mouseClicked(MouseEvent event) {}
	@Override public void mouseReleased(MouseEvent event) {}
	@Override public void mouseEntered(MouseEvent event) {}
	@Override public void mouseExited(MouseEvent event) {}
	
	private static final ImageIcon normalTexture = new ImageIcon(GuiMenu.class.getResource("/textures/dev/normal.png"));
	private static final ImageIcon cherryTexture = new ImageIcon(GuiMenu.class.getResource("/textures/dev/cherry.png"));
	private static final ImageIcon appleTexture = new ImageIcon(GuiMenu.class.getResource("/textures/dev/apple.png"));
	private static final ImageIcon dugTexture = new ImageIcon(GuiMenu.class.getResource("/textures/dev/dug.png"));
	private static final ImageIcon spawnerTexture = new ImageIcon(GuiMenu.class.getResource("/textures/dev/spawner.png"));
	private static final ImageIcon player1Texture = new ImageIcon(GuiMenu.class.getResource("/textures/dev/player1.png"));
	private static final ImageIcon player2Texture = new ImageIcon(GuiMenu.class.getResource("/textures/dev/player2.png"));
}