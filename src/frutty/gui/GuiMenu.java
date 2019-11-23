package frutty.gui;

import static frutty.tools.GuiHelper.*;

import frutty.*;
import frutty.gui.components.*;
import frutty.plugin.event.gui.*;
import frutty.tools.*;
import frutty.world.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import javax.swing.*;

public final class GuiMenu{
	private static JFrame mainFrame;
	
	public static JPanel createMenuPanel() {
		var panel = new GuiMapBackground("./maps/background" + Main.rand.nextInt(4) + ".fmap");
		panel.setLayout(null);
		
		panel.add(newButton("New Game", 700, 20, e -> GuiMapSelection.showMapSelection()));
		panel.add(newButton("Exit", 370, 550, e -> System.exit(0)));
		panel.add(newButton("Settings", 700, 250, e -> GuiSettings.showGuiSettings()));
		panel.add(newButton("Load Save", 700, 100, e -> handleSaveLoading(panel)));
		panel.add(newButton("Plugins", 20, 400, e -> GuiPlugins.showPlugins()));
		panel.add(newButton("Stats", 700, 330, e -> GuiStats.showStatsGui()));
		
		var versionLabel = new JLabel("Version: " + Main.plugins.get(0).version);
		versionLabel.setBounds(20, 600, 100, 30);
		versionLabel.setForeground(Color.WHITE);
		versionLabel.setFont(GuiHelper.thiccFont);
		panel.add(versionLabel);
		
		if(!EventHandle.menuInitEvents.isEmpty()) {
			var eventButtons = new ArrayList<JButton>(0);
			EventHandle.handleEvent(new GuiMenuEvent(eventButtons), EventHandle.menuInitEvents);
			for(var butt : eventButtons) panel.add(butt);
		}
		
		return panel;
	}
	
	public static void closeMainFrame() {
	    mainFrame.dispose();
	}
	
	public static void createMainFrame() {
		EventQueue.invokeLater(() -> {
			mainFrame = new JFrame("Frutty");
			mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			mainFrame.setResizable(false);
			mainFrame.setIconImage(frameIcon);
			mainFrame.setBounds(0, 0, 910, 675);
			mainFrame.setLocationRelativeTo(null);
			mainFrame.setContentPane(createMenuPanel());
			mainFrame.setFocusable(true);
			mainFrame.setVisible(true);
		});
	}
	
	public static void switchMenuGui(Container panel) {
        EventQueue.invokeLater(() -> {
            GuiMenu.mainFrame.setContentPane(panel);
            GuiMenu.mainFrame.revalidate();
        });
    }
	
	private static void handleSaveLoading(GuiMapBackground panel) {
	    try(var files = Files.list(Path.of("./saves"))){
	        var saveFolderList = files.map(Path::getFileName).map(Path::toString).toArray(String[]::new);
				
	        if(saveFolderList.length > 0) {
	            var saveName = (String) JOptionPane.showInputDialog(panel, "Chose map file!", "Saves", JOptionPane.QUESTION_MESSAGE, null, saveFolderList, saveFolderList[0]);
					
	            if(World.loadSave(saveName)) {
	                GuiIngame.showIngame();
	                ((JFrame)panel.getTopLevelAncestor()).dispose();
	            }
	        }else{
	            JOptionPane.showMessageDialog(panel, "No saves found in saves directory");
	        }
	    } catch (IOException e) {}
	}
}