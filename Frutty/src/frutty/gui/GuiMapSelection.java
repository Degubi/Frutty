package frutty.gui;

import static frutty.tools.GuiHelper.newButton;
import static frutty.tools.GuiHelper.newCheckBox;
import static frutty.tools.GuiHelper.switchMenuPanel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import frutty.gui.components.SettingButton;
import frutty.tools.GuiHelper;
import frutty.tools.IOHelper;
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

			if(IOHelper.fileExists(path)) {
				BufferedImage image = IOHelper.loadTexture("gui", mapList.getSelectedValue() + ".jpg");
				var graph = image.createGraphics();
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
		DefaultListModel<String> model = new DefaultListModel<>();
		var files = Arrays.stream(new File("./maps").list()).filter(name -> name.endsWith(".fmap")).map(name -> name.substring(0, name.indexOf('.')));
		
		if(devMode.isSelected()) {
			files.forEach(map -> model.addElement(map));
		}else{
			files.filter(name -> !name.startsWith("background")).filter(name -> !name.startsWith("dev_")).forEach(map -> model.addElement(map));
		}
		
		model.addElement("Generate Map");
		
		mapList.setModel(model);
		mapList.setSelectedValue("Creepy", false);
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		String actionCommand = event.getActionCommand();
		
		if(actionCommand.equals("Play")) {
			if(mapList.getSelectedValue().equals("Generate Map")) {
				GuiHelper.switchMenuPanel(new GuiGenerateMap());
			}else{
				World.loadMap(mapList.getSelectedValue(), coopBox.isSelected());
				GuiIngame.showIngame();
				GuiMenu.mainFrame.dispose();
			}
		}else if(actionCommand.equals("Menu")) {
			switchMenuPanel(new GuiMenu());
		}else if(actionCommand.equals("Enable Dev Maps")) {
			setModel();
		}
	}
	
	
	public static String loadMapSize(String fileName) {
		try(var input = IOHelper.newObjectIS("./maps/" + fileName + ".fmap")){
			input.readObject(); input.readObject();
			input.readUTF();
			return input.readShort() + "x" + input.readShort();
			
		} catch (IOException | ClassNotFoundException e) {
			return "Can't determine map size!";
		}
	}
	
	static class GuiGenerateMap extends JPanel implements ActionListener{
		private final JTextField sizeField = new JTextField("10x10");
		
		public GuiGenerateMap() {
			setLayout(null);
			
			sizeField.setBounds(50, 50, 120, 40);
			add(sizeField);
			
			add(new SettingButton(false, "Enable Water", 50, 80));
			add(newButton("Menu", 725, 475, this));
			add(newButton("Play", 725, 550, this));
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			String actionCommand = event.getActionCommand();
			
			if(actionCommand.equals("Play")) {
				var mapSize = sizeField.getText().split("x");
				World.generateMap(Integer.parseInt(mapSize[0]), Integer.parseInt(mapSize[1]), false);
				GuiIngame.showIngame();
				GuiMenu.mainFrame.dispose();
			}else if(actionCommand.equals("Menu")) {
				switchMenuPanel(new GuiMenu());
			}
		}
	}
}