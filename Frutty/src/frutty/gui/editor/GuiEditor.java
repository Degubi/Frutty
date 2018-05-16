package frutty.gui.editor;

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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import frutty.gui.GuiHelper;
import frutty.gui.GuiMenu;
import frutty.gui.editor.GuiProperties.EnumProperty;

public final class GuiEditor extends JPanel implements MouseListener{
	final ArrayList<JButton> zoneButtons = new ArrayList<>();
	private final GuiProperties mapProperties;
	
	private GuiEditor(String fileName, boolean isBackground, String textureName, String skyName, int... data) {
		mapProperties = new GuiProperties(fileName, textureName, skyName, isBackground, data);
		setLayout(null);
		
		add(GuiHelper.newButton("Save", data[0] * 64 + 12, data[1] * 64 - 100, this));
		add(GuiHelper.newButton("Exit", data[0] * 64 + 12, data[1] * 64 - 50, this));
		add(GuiHelper.newButton("Clear", data[0] * 64 + 12, 300, this));
		add(GuiHelper.newButton("Properties", data[0] * 64 + 12, 100, this));
		
		GuiHelper.mapSizeCheck(data[0], data[1]);
	}
	
	public static void openEditor() {
		int editorMode = JOptionPane.showOptionDialog(null, "Create new map or import", "Frutty map Editor", JOptionPane.YES_NO_CANCEL_OPTION, 1, null, new String[] {"New", "Import", "Cancel"}, null);
		if(editorMode == 0) {
			String input = JOptionPane.showInputDialog("Enter map size!", "10x10");
			
			if(input != null) {
				String[] mapSizeString = input.split("x");
				int mapWidth = Integer.parseInt(mapSizeString[0]), bigWidth = mapWidth * 64;
				int mapHeight = Integer.parseInt(mapSizeString[1]), bigHeight = mapHeight * 64;
				GuiEditor editor = new GuiEditor("filename.deg", false, "normal", "null", mapWidth, mapHeight, 0, 0, 0, 0);
				
				for(int yPos = 0; yPos < bigHeight; yPos += 64) {
					for(int xPos = 0; xPos < bigWidth; xPos += 64) {
						JButton button = new JButton(EnumEditorZone.Normal.icon);
						button.setMnemonic(EnumEditorZone.Normal.zoneIndex);
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
					String textureName = input.readUTF(), skyName = input.readUTF();
					int mapWidth = input.readShort(), mapHeight = input.readShort();
					GuiEditor editor = fileName.startsWith("background") 
									 ? new GuiEditor(fileName, true, textureName, skyName, mapWidth, mapHeight, 0, 0, 0, 0)
									 : new GuiEditor(fileName, false, textureName, skyName, mapWidth, mapHeight, input.readShort(), input.readShort(), input.readShort(), input.readShort());
					
					for(int y = 0; y < mapHeight; ++y) {
						for(int x = 0; x < mapWidth; ++x) {
							EnumEditorZone.getFromIndex(input.readByte()).handleReading(editor, x, y);
						}
					}
					GuiHelper.showNewFrame(editor, "Frutty Map Editor", JFrame.DISPOSE_ON_CLOSE, mapWidth * 64 + 156, mapHeight * 64 + 32);
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}else{   //Dont't close everything :(
			GuiMenu.showMenu();
		}
	}
	
	@Override
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		
		int width = mapProperties.getIntProperty(EnumProperty.MapWidth);
		graphics.drawString("Alt: Spawner", width * 64 + 25, 400);
		graphics.drawString("Shift: Player1", width * 64 + 25, 440);
		graphics.drawString("Ctrl: Player2", width * 64 + 25, 480);
	}

	@Override
	public void mousePressed(MouseEvent event) {
		if(event.getComponent() instanceof JButton) {
			JButton button = (JButton) event.getComponent();
			
			if(event.getButton() == MouseEvent.BUTTON1) { //Left click
				String command = button.getActionCommand();
				
				if(command.isEmpty()) {
					EnumEditorZone.getFromIndex(button.getMnemonic()).handleNext(button, mapProperties, event);
				}else if(command.equals("Exit")) {
					((JFrame)getTopLevelAncestor()).dispose(); GuiMenu.showMenu();
				}else if(command.equals("Clear")) {
					for(JButton localButton : zoneButtons) {
				 		localButton.setMnemonic(EnumEditorZone.Normal.zoneIndex);
				 		localButton.setIcon(EnumEditorZone.Normal.icon);
				 	}
				}else if(command.equals("Properties")) {
					GuiHelper.showNewFrame(mapProperties, "Map Properties", JFrame.DISPOSE_ON_CLOSE, 350, 350);
				}else if(command.equals("Save")) {
					try(ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream("./maps/" + mapProperties.getProperty(EnumProperty.MapName)))){
				 		output.writeUTF(mapProperties.getProperty(EnumProperty.Texture));
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
				 			output.writeByte(writeButton.getMnemonic());
				 		}
					} catch (IOException e) {
						e.printStackTrace();
					}
				 	JOptionPane.showMessageDialog(null, "Map saved as: " + mapProperties.getProperty(EnumProperty.MapName));
				}
			
			}else if(event.getButton() == MouseEvent.BUTTON3) {    //Right click
				EnumEditorZone.getFromIndex(button.getMnemonic()).handlePrevious(button, mapProperties, event);
			}
		}
	}
	
	@Override public void mouseClicked(MouseEvent event) {}
	@Override public void mouseReleased(MouseEvent event) {}
	@Override public void mouseEntered(MouseEvent event) {}
	@Override public void mouseExited(MouseEvent event) {}
}