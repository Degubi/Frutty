package frutty.gui;

import static frutty.gui.components.GuiHelper.newButton;
import static frutty.gui.components.GuiHelper.newCheckBox;
import static frutty.gui.components.GuiHelper.switchMenuPanel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import frutty.Main;
import frutty.gui.components.GuiHelper;
import frutty.world.World;

public final class GuiMapSelection extends JPanel implements ListSelectionListener, ActionListener{
	private final JCheckBox devMode = newCheckBox("Enable Dev Maps", 180, 510, Color.BLACK, false);
	private final JList<String> mapList = new JList<>();
	private final JLabel mapImage = new JLabel();
	private final JCheckBox coopBox = newCheckBox("Coop mode", 40, 510, Color.BLACK, false);
	
	public GuiMapSelection() {
		setLayout(null);
		
		mapList.setForeground(Color.BLACK);
		mapList.setBackground(Color.GRAY);
		mapList.addListSelectionListener(this);
		devMode.addActionListener(this);
		
		setModel();
		
		add(devMode);
		add(coopBox);
		
		JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mapList, mapImage);
		pane.setEnabled(false);
		pane.setBounds(20, 20, 690, 480);
		pane.setBorder(GuiHelper.menuBorder);
		pane.setDividerLocation(200);
		
		add(pane);
		add(newButton("Menu", 725, 475, this));
		add(newButton("Play", 725, 550, this));
	}
	
	@Override
	public void valueChanged(ListSelectionEvent event) {
		if(!event.getValueIsAdjusting()){
			String path = "./textures/gui/" + mapList.getSelectedValue() + ".jpg";

			if(Files.exists(Paths.get(path))) {
				BufferedImage image = Main.loadTexture("gui", mapList.getSelectedValue() + ".jpg");
				Graphics graph = image.getGraphics();
				graph.setColor(Color.BLACK);
				graph.fillRect(380, 420, 100, 60);
				graph.setColor(Color.WHITE);
				graph.setFont(GuiHelper.bigFont);
				graph.drawString(loadMapSize(mapList.getSelectedValue()), 390, 460);
				
				mapImage.setIcon(new ImageIcon(image));
			}else{
				mapImage.setIcon(new ImageIcon("./textures/gui/dev.jpg"));
			}
		}
	}
	
	@Override
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		
		graphics.setColor(Color.GRAY);
		graphics.fillRect(0, 0, 910, 675);
	}
	
	private void setModel() {
		var model = new DefaultListModel<String>();
		var files = Arrays.stream(new File("./maps").list()).filter(name -> name.endsWith(".deg")).map(name -> name.substring(0, name.length() - 4));
		
		if(devMode.isSelected()) {
			files.forEach(map -> model.addElement(map));
		}else{
			files.filter(name -> !name.startsWith("background")).filter(name -> !name.startsWith("dev_")).forEach(map -> model.addElement(map));
		}
		
		mapList.setModel(model);
		mapList.setSelectedValue("Creepy", false);
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		String actionCommand = event.getActionCommand();
		
		if(actionCommand.equals("Play")) {
			/*if(mapName.equals("Generate")) {
				String[] mapSizeSplit = mapSizeField.getText().split("x");
				Map.generateMap(Integer.parseInt(mapSizeSplit[0]), Integer.parseInt(mapSizeSplit[1]), coopBox.isSelected());
			}else{
			*/
			World.loadMap(mapList.getSelectedValue(), coopBox.isSelected());
			//}
			GuiIngame.showIngame();
			GuiMenu.mainFrame.dispose();
		}else if(actionCommand.equals("Menu")) {
			switchMenuPanel(new GuiMenu());
		}else if(actionCommand.equals("Enable Dev Maps")) {
			setModel();
		}
	}
	
	
	public static String loadMapSize(String fileName) {
		String path = "./maps/" + fileName + ".deg";
		try(var input = new ObjectInputStream(Files.newInputStream(Paths.get(path)))){
			int textureCount = input.readByte();
			for(int k = 0; k < textureCount; ++k) {
				input.readUTF();
			}
			
			int idCount = input.readByte();
			for(int k = 0; k < idCount; ++k) {
				input.readUTF();
			}
			
			input.readUTF();
			return input.readShort() + "x" + input.readShort();
		} catch (IOException e) {
			System.err.println("Can't load map size for map: " + path);
		}
		return "";
	}
}