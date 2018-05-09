package frutty.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import frutty.map.Map;
import frutty.map.MapZone;

public final class GuiMenu extends JPanel implements ActionListener{
	private final JComboBox<String> mapList = new JComboBox<>();
	private final JTextField mapSizeField = new JTextField("8x8");
	private final JCheckBox coopBox = GuiHelper.newCheckBox("Coop mode", 445, 130, false);
	private static final Color grayened = new Color(0, 0, 0, 128);
	private final MapZone[] background;
	
	public GuiMenu() {
		setLayout(null);
		
		background = Map.loadBackground();
		
		mapSizeField.setBounds(500, 20, 60, 30);
		mapSizeField.setHorizontalAlignment(JTextField.CENTER);
		
		coopBox.setForeground(Color.WHITE);
		
		add(GuiHelper.newMenuButton("New Game", 700, 20, this));
		add(GuiHelper.newMenuButton("Exit", 370, 550, this));
		add(GuiHelper.newMenuButton("Settings", 700, 250, this));
		add(GuiHelper.newMenuButton("Load Save", 700, 100, this));
		add(GuiHelper.newMenuButton("Editor", 20, 475, this));
		add(GuiHelper.newMenuButton("Stats", 700, 330, this));
		
		mapList.addActionListener(this);
		mapList.setActionCommand("MapSelector");
		mapList.setBounds(330, 20, 100, 30);
		mapList.setBackground(Color.LIGHT_GRAY);
		mapList.setBorder(GuiHelper.menuBorder);
		
		String[] maps = new File("./maps/").list();
		for(String map : maps) {
			if(!map.startsWith("background")) {
				mapList.addItem(map.substring(0, map.length() - 4));
			}
		}
		
		mapList.addItem("Generate");  //Legeslegrosszabb esetben is tudunk gener�lni mapot
		
		add(mapSizeField);
		add(mapList);
		add(coopBox);
	}
	
	public static void showMenu() {
		GuiHelper.showNewFrame(new GuiMenu(), "Tutty Frutty", JFrame.EXIT_ON_CLOSE, 910, 675);
	}
	
	@Override
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		
		for(MapZone zone : background)
			zone.draw(graphics);
		
		graphics.setColor(grayened);
		graphics.fillRect(0, 0, 910, 675);
		
		graphics.setColor(Color.WHITE);
		graphics.setFont(GuiHelper.thiccFont);
		graphics.drawString(GuiHelper.recommendedMapSizeString, 330, 80);
		graphics.drawString("Size:", 455, 40);
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		switch(event.getActionCommand()) {
		
		case "New Game":
			String mapName = ((String)mapList.getSelectedItem()).toLowerCase();
			if(mapName.equals("generate")) {
				String[] mapSizeSplit = mapSizeField.getText().split("x");
				Map.generateMap(Integer.parseInt(mapSizeSplit[0]), Integer.parseInt(mapSizeSplit[1]), coopBox.isSelected());
			}else{
				Map.loadMap(mapName, coopBox.isSelected());
			}
			GuiIngame.showIngame();
			((JFrame)getTopLevelAncestor()).dispose(); break;
			
		case "Exit": System.exit(0); break;
		case "Settings": GuiSettings.showGuiSettings(); break;
		case "MapSelector": 
			if(((String)mapList.getSelectedItem()).toLowerCase().equals("generate")) {
				mapSizeField.setEditable(true);
				mapSizeField.setText("8x8");
			}else{
				mapSizeField.setEditable(false);
				mapSizeField.setText(Map.loadMapSize((String) mapList.getSelectedItem()));
			}break;
			
		case "Editor": GuiEditor.openEditor(); ((JFrame)getTopLevelAncestor()).dispose(); break;
		case "Stats": GuiStats.openStatsGui(); break;
			
		default: //Load
			String[] allMapNames = new File("./saves/").list();
			if(allMapNames.length > 0) {
				String inputMapName = (String) JOptionPane.showInputDialog(this, "Chose map file!", "Saves", JOptionPane.QUESTION_MESSAGE, null, allMapNames, allMapNames[0]);
				if(inputMapName != null) {
					Map.loadSave(inputMapName);
					GuiIngame.showIngame();
					((JFrame)getTopLevelAncestor()).dispose();
				}
			}else{
				JOptionPane.showMessageDialog(this, "No saves found in saves directory");
			}
		}
	}
}