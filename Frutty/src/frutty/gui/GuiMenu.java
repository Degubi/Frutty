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

import frutty.Main;
import frutty.map.Map;

public final class GuiMenu extends JPanel implements ActionListener{
	private final JComboBox<String> mapList = new JComboBox<>();
	private final JTextField mapSizeField = new JTextField("8x8");
	private final JCheckBox multiBox = new JCheckBox("Coop mode");
	private boolean loading = false;
	
	private int loadCount;
	private static GuiMenu instance;
	
	public GuiMenu(int load) {
		setLayout(null);
		
		loadCount = load;
		
		mapSizeField.setBounds(400, 20, 40, 30);
		multiBox.setBounds(225, 100, 90, 30);
		
		add(GuiHelper.newButton("Play", 20, 20, this));
		add(GuiHelper.newButton("Exit", 180, 280, this));
		add(GuiHelper.newButton("Settings", 20, 170, this));
		add(GuiHelper.newButton("Load", 20, 70, this));
		add(GuiHelper.newButton("Editor", 310, 200, this));
		add(GuiHelper.newButton("Stats", 20, 220, this));
		
		mapList.addActionListener(this);
		mapList.setActionCommand("MapSelector");
		mapList.setBounds(230, 20, 100, 30);
		
		String[] maps = new File("./maps/").list();
		for(String map : maps)
			mapList.addItem(map.substring(0, map.length() - 4));   //Substring, .deg eltakarítás
		
		mapList.addItem("Generate");  //Legeslegrosszabb esetben is tudunk generálni mapot
		
		multiBox.setOpaque(false);
		
		add(mapSizeField);
		add(mapList);
		add(multiBox);
	}
	
	public static void showMenu() {
		GuiHelper.showNewFrame(instance = new GuiMenu(instance == null ? 0 : 100), "Tutty Frutty", JFrame.EXIT_ON_CLOSE, 480, 360);
	}
	
	public static void refreshMenu() {
		instance.loadCount += 25;
		instance.repaint();
	}
	
	@Override
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		graphics.setColor(Color.LIGHT_GRAY);
		graphics.fillRect(0, 0, 480, 360);
		
		graphics.setColor(Color.DARK_GRAY);
		graphics.setFont(GuiHelper.thiccFont);
		graphics.drawString(GuiHelper.recommendedMapSizeString, 230, 80);
		graphics.drawString("MapSize:", 340, 40);
		graphics.drawString("Loading count: " + loadCount, 340, 300);
		
		if(loading) {
			graphics.setColor(Color.DARK_GRAY);
			graphics.fillRect(145, 120, 150, 60);
			graphics.setColor(Color.BLACK);
			graphics.drawRect(144, 119, 151, 61);
			graphics.setFont(GuiHelper.thiccFont);
			graphics.drawString("Loading...", 195, 150);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		switch(event.getActionCommand()) {
		
		case "Play":
			loading = true;
			repaint();
			new Thread(() -> {   //Off main thread indítás, máskülönben a loading szöveg nem jelenik meg a thread blokkolás miatt
				String mapName = ((String)mapList.getSelectedItem()).toLowerCase();
				if(mapName.equals("generate")) {
					String[] mapSizeSplit = mapSizeField.getText().split("x");
					Map.generateMap(Integer.parseInt(mapSizeSplit[0]), Integer.parseInt(mapSizeSplit[1]), multiBox.isSelected());
				}else{
					Map.loadMap(mapName, multiBox.isSelected());
				}
				GuiIngame.showIngame();
				((JFrame)getTopLevelAncestor()).dispose();
			}).start();
			break;
			
		case "Exit": System.exit(0); break;
		case "Settings": GuiSettings.showGuiSettings(); break;
		case "MapSelector": 
			if(((String)mapList.getSelectedItem()).toLowerCase().equals("generate")) {
				mapSizeField.setEditable(true);
				mapSizeField.setText("8x8");
			}else{
				mapSizeField.setEditable(false);
				mapSizeField.setText(Main.loadMapSize((String) mapList.getSelectedItem()));
			}break;
			
		case "Editor": GuiEditor.openEditor(); ((JFrame)getTopLevelAncestor()).dispose(); break;
		case "Stats": GuiStats.openStatsGui(); break;
			
		default : //Load
			String[] allMapNames = new File("./saves/").list();
			if(allMapNames.length > 0) {
				String inputMapName = (String) JOptionPane.showInputDialog(this, "Chose map file!", "Saves", JOptionPane.QUESTION_MESSAGE, null, allMapNames, allMapNames[0]);
				if(inputMapName != null) {
					Map.loadSave(inputMapName);
					GuiIngame.showIngame();
					((JFrame)getTopLevelAncestor()).dispose();
				}
			}else {
				JOptionPane.showMessageDialog(this, "No saves found in saves directory");
			}
		}
	}
}