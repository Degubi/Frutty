package frutty.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import frutty.map.Map;

public final class GuiMapSelection extends JPanel implements ListSelectionListener, ActionListener{
	private final JCheckBox devMode = GuiHelper.newCheckBox("Enable Dev Maps", 40, 600, Color.BLACK, false);
	private final JList<String> mapList = new JList<>();
	private final JLabel mapImage = new JLabel();
	private final JCheckBox coopBox = GuiHelper.newCheckBox("Coop mode", 40, 550, Color.BLACK, false);
	
	public GuiMapSelection() {
		setLayout(null);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mapList, mapImage);
		splitPane.setDividerLocation(200);
		splitPane.setEnabled(false);
		splitPane.setBounds(20, 20, 690, 480);
		splitPane.setBorder(GuiHelper.menuBorder);
		
		mapList.setForeground(Color.BLACK);
		mapList.setBackground(Color.GRAY);
		mapList.addListSelectionListener(this);
		devMode.addActionListener(this);
		
		setModel();
		
		add(devMode);
		add(coopBox);
		add(splitPane);
		add(GuiHelper.newButton("Menu", 550, 550, this));
		add(GuiHelper.newButton("Play", 350, 550, this));
	}
	
	@Override
	public void valueChanged(ListSelectionEvent event) {
		String path = "./maps/" + mapList.getSelectedValue() + ".jpg";
		
		if(new File(path).exists()) {
			mapImage.setIcon(new ImageIcon(path));
		}else{
			mapImage.setIcon(new ImageIcon("./maps/dev.jpg"));
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
			Map.loadMap(mapList.getSelectedValue(), coopBox.isSelected());
			//}
			GuiIngame.showIngame();
			((JFrame)getTopLevelAncestor()).dispose();
		}else if(actionCommand.equals("Menu")) {
			GuiMenu.showMenu(false);
			((JFrame)getTopLevelAncestor()).dispose();
		}else if(actionCommand.equals("Enable Dev Maps")) {
			setModel();
		}
	}
	
	
	public static String loadMapSize(String fileName) {
		try(var input = new ObjectInputStream(Files.newInputStream(Paths.get("./maps/" + fileName + ".deg")))){
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
			System.err.println("Can't load map size for menu: " + fileName + ".deg");
		}
		return "";
	}
}