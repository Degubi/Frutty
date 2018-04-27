package frutty.gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class GuiEditor extends JPanel implements MouseListener{
	private final JButton[] zoneButtons;
	private final GuiProperties properties;
	
	private static final ImageIcon normalTexture = new ImageIcon(GuiMenu.class.getResource("/textures/dev/normal.png"));
	private static final ImageIcon cherryTexture = new ImageIcon(GuiMenu.class.getResource("/textures/dev/cherry.png"));
	private static final ImageIcon appleTexture = new ImageIcon(GuiMenu.class.getResource("/textures/dev/apple.png"));
	private static final ImageIcon dugTexture = new ImageIcon(GuiMenu.class.getResource("/textures/dev/dug.png"));
	private static final ImageIcon spawnerTexture = new ImageIcon(GuiMenu.class.getResource("/textures/dev/spawner.png"));
	private static final ImageIcon player1Texture = new ImageIcon(GuiMenu.class.getResource("/textures/dev/player1.png"));
	private static final ImageIcon player2Texture = new ImageIcon(GuiMenu.class.getResource("/textures/dev/player2.png"));
	
	private GuiEditor(List<JButton> butt, String fileName, String textureName, int... data) {
		properties = new GuiProperties(fileName, textureName, data);
		zoneButtons = new JButton[data[0] * data[1]];
		setLayout(null);
		
		int posX = data[0] * 64 + 12, posY = data[1] * 64, buttonIndexer = 0;
		add(GuiHelper.newButton("Save", posX, posY - 100, this));
		add(GuiHelper.newButton("Exit", posX, posY - 50, this));
		add(GuiHelper.newButton("Clear", posX, 300, this));
		add(GuiHelper.newButton("Properties", posX, 100, this));
		
		if(butt == null) {
			for(int y = 0, yPos = 0; y < data[0]; ++y, yPos += 64) {
				for(int x = 0, xPos = 0; x < data[1]; ++x, xPos += 64) {
					JButton button = new JButton(normalTexture);
					button.setActionCommand("n");
					button.addMouseListener(this);
					button.setBounds(xPos, yPos, 64, 64);
					zoneButtons[buttonIndexer++] = button;
					add(button);
				}
			}
		}else{
			for(JButton button : butt) {
				button.addMouseListener(this);
				zoneButtons[buttonIndexer++] = button;
				add(button);
			}
		}
	}
	
	public static void openEditor() {
		//0: New map, 1: Import
		int test = JOptionPane.showOptionDialog(null, "Create new map or import", "Frutty map Editor", JOptionPane.YES_NO_CANCEL_OPTION, 1, null, new String[] {"New", "Import", "Cancel"}, null);
		if(test == 0) {
			String input = JOptionPane.showInputDialog("Enter map size!", "10x10");
			
			if(input != null) {
				String[] mapSizeString = input.split("x");
				int mapWidth = Integer.parseInt(mapSizeString[0]), bigWidth = mapWidth * 64;
				int mapHeight = Integer.parseInt(mapSizeString[1]), bigHeight = mapHeight * 64;
				
				if(bigHeight > GuiHelper.screen.height || bigWidth > GuiHelper.screen.width) {
					JOptionPane.showMessageDialog(null, "Map size is very big, things may go offscreen!");
				}
				
				GuiHelper.showNewFrame(new GuiEditor(null, "filename.deg", "normal", mapWidth, mapHeight, 0, 0, 0, 0), 
						"Frutty Map Editor", JFrame.DISPOSE_ON_CLOSE, bigWidth + 156, bigHeight + 32);
			}
		}else if(test == 1) {
			String[] fileNames = new File("./maps/").list();
			String fileName = (String) JOptionPane.showInputDialog(null, "Select Map!", "Maps:", JOptionPane.QUESTION_MESSAGE, null, fileNames, fileNames[0]);
			
			if(fileName != null && !fileName.isEmpty()) {
				try(ObjectInputStream input = new ObjectInputStream(new FileInputStream("./maps/" + fileName))){
					String textureName = input.readUTF();
					int mapWidth = input.readInt(), mapHeight = input.readInt();
					int player1PosX = input.readInt(), player1PosY = input.readInt();
					int player2PosX = input.readInt(), player2PosY = input.readInt();
					ArrayList<JButton> butt = new ArrayList<>();
					
					for(int y = 0; y < mapHeight; ++y) {
						for(int x = 0; x < mapWidth; ++x) {
							JButton button = new JButton();
							char read = input.readChar();
							
							switch(read) {
								case 'c': button.setIcon(cherryTexture); break;
								case 'd': button.setIcon(dugTexture); break;
								case 'a': button.setIcon(appleTexture); break;
								case 's': button.setIcon(spawnerTexture); break;
								default: button.setIcon(normalTexture);
							}
							button.setActionCommand(String.valueOf(read));
							button.setBounds(x * 64, y * 64, 64, 64);
							
							if(button.getX() == player1PosX && button.getY() == player1PosY) {
								button.setActionCommand("p");
								button.setIcon(player1Texture);
							}
							
							if(button.getX() == player2PosX && button.getY() == player2PosY) {
								button.setActionCommand("x");
								button.setIcon(player2Texture);
							}
							butt.add(button);
						}
					}
					
					GuiHelper.showNewFrame(new GuiEditor(butt, fileName, textureName, mapWidth, mapHeight, player1PosX, player1PosY, player2PosX, player2PosY), 
							"Frutty Map Editor", JFrame.DISPOSE_ON_CLOSE, mapWidth * 64 + 156, mapHeight * 64 + 32);
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent event) {
		if(event.getComponent() instanceof JButton) {
			JButton button = (JButton) event.getComponent();
			if(event.getButton() == MouseEvent.BUTTON1) {   //Left click
				switch(button.getActionCommand()) {
				 	case "n": button.setActionCommand("d"); button.setIcon(dugTexture); break;
				 	case "d": button.setActionCommand("a"); button.setIcon(appleTexture); break;
				 	case "a": button.setActionCommand("c"); button.setIcon(cherryTexture); break;
				 	case "c": button.setActionCommand("s"); button.setIcon(spawnerTexture); break;
				 	case "s": 
				 	if(event.isControlDown()) {
				 		button.setActionCommand("p"); properties.setPlayer1Pos(button); button.setIcon(player1Texture);
				 	}else if(event.isShiftDown()) {
				 		button.setActionCommand("x"); properties.setPlayer2Pos(button); button.setIcon(player2Texture);
				 	}else{
				 		button.setActionCommand("n"); button.setIcon(normalTexture);
				 	}
				 	break;
				 	case "p": button.setActionCommand("n"); button.setIcon(normalTexture); break;
				 	case "Exit": ((JFrame)getTopLevelAncestor()).dispose(); GuiMenu.showMenu(); break;
				 	case "Clear":
				 		
				 	for(JButton localButton : zoneButtons) {
				 		localButton.setActionCommand("n");
				 		localButton.setIcon(normalTexture);
				 	} break;
				 	
				 	case "Properties": 
				 		GuiHelper.showNewFrame(properties, "Map Properties", JFrame.DISPOSE_ON_CLOSE, 350, 250); break;
				 	
				 	default:
				 	try(ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream("./maps/" + properties.getMapName()))){
				 		output.writeUTF(properties.getMapTextureName());
				 		output.writeInt(properties.getMapWidth());
				 		output.writeInt(properties.getMapHeight());
				 		output.writeInt(properties.getPlayer1PosX());
				 		output.writeInt(properties.getPlayer1PosY());
				 		output.writeInt(properties.getPlayer2PosX());
				 		output.writeInt(properties.getPlayer2PosY());

				 		for(JButton writeButton : zoneButtons) {
				 			char toWrite = writeButton.getActionCommand().charAt(0);
				 			if(toWrite == 'p' || toWrite == 'x') {
				 				output.writeChar('d');
				 			}else{
				 				output.writeChar(toWrite);
				 			}
				 		}
					} catch (IOException e) {
						e.printStackTrace();
					}
				 	
				 	JOptionPane.showMessageDialog(null, "Map saved as: " + properties.getMapName());
				}
			}else if(event.getButton() == MouseEvent.BUTTON3) {    //Right click
				switch(button.getActionCommand().charAt(0)) {
					case 'n': 
					if(event.isControlDown()) {
						button.setActionCommand("p"); properties.setPlayer1Pos(button); button.setIcon(player1Texture);
					}else if(event.isShiftDown()) {
						button.setActionCommand("x"); properties.setPlayer2Pos(button); button.setIcon(player2Texture);
					}else {
						button.setActionCommand("s"); button.setIcon(spawnerTexture);
					}
					break;
				 	case 'd': button.setActionCommand("n"); button.setIcon(normalTexture); break;
				 	case 'a': button.setActionCommand("d"); button.setIcon(dugTexture); break;
				 	case 'c': button.setActionCommand("a"); button.setIcon(appleTexture); break;
				 	case 's': button.setActionCommand("c"); button.setIcon(cherryTexture); break;
				 	case 'p': button.setActionCommand("s"); button.setIcon(spawnerTexture); break;
				 	case 'x': button.setActionCommand("s"); button.setIcon(spawnerTexture); break;
				 	default: ;
				}
			}
		}
	}
	
	@Override public void mouseClicked(MouseEvent event) {}
	@Override public void mouseReleased(MouseEvent event) {}
	@Override public void mouseEntered(MouseEvent event) {}
	@Override public void mouseExited(MouseEvent event) {}
}