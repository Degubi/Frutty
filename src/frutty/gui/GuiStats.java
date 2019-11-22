package frutty.gui;

import static frutty.tools.GuiHelper.*;

import frutty.*;
import frutty.gui.components.*;
import frutty.plugin.event.gui.*;
import frutty.tools.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;

public final class GuiStats extends DefaultTreeCellRenderer{
	private static final PropertyFile stats = new PropertyFile("stats.prop", 4);
	public static int topScore = stats.getInt("topScore", 0);
	public static int enemyCount = stats.getInt("enemyCount", 0);
	public static int zoneCount = stats.getInt("zoneCount", 0);
	public static int playTime = stats.getInt("minutesPlayed", 0);
	
	private GuiStats() {}
	
	public static void showStatsGui() {
		var panel = new GuiMapBackground("./maps/dev_settings.fmap");
		panel.setLayout(null);
		
		var top = new DefaultMutableTreeNode("Frutty Stats");
		var basic = new DefaultMutableTreeNode("Basics");
		var zones = new DefaultMutableTreeNode("Zones");
		var enemies = new DefaultMutableTreeNode("Enemies");
		
		basic.add(new DefaultMutableTreeNode("Minutes Played: " + playTime));
		basic.add(new DefaultMutableTreeNode("Max Score: " + topScore));
		zones.add(new DefaultMutableTreeNode("Zones Destroyed: " + zoneCount));
		enemies.add(new DefaultMutableTreeNode("Enemies Killed: " + enemyCount));
		
		top.add(basic);
		top.add(zones);
		top.add(enemies);
		
		var statTree = new JTree(top);
		statTree.setBackground(Color.BLACK);
		statTree.setCellRenderer(new GuiStats());
		statTree.setFont(GuiHelper.thiccFont);
		statTree.setOpaque(true);
		statTree.setBounds(50, 50, 200, 400);
		
		if(!EventHandle.statInitEvents.isEmpty()) {
			EventHandle.handleEvent(new GuiStatInitEvent(stats, basic, zones, enemies, top), EventHandle.statInitEvents);
		}
		
		panel.add(statTree);
		panel.add(newButton("Reset", 100, 550, e -> handleResetButtonPress(panel)));
		panel.add(newButton("Menu", 370, 550, e -> GuiMenu.switchMenuGui(GuiMenu.createMenuPanel())));
		
		GuiMenu.switchMenuGui(panel);
	}
	
	private static void handleResetButtonPress(GuiMapBackground panel) {
	    topScore = 0;
        enemyCount = 0;
        zoneCount = 0;
        panel.repaint();
        saveStats();
	}
	
	public static void saveStats() {
		stats.setInt("topScore", topScore);
		stats.setInt("enemyCount", enemyCount);
		stats.setInt("zoneCount", zoneCount);
		
		if(!EventHandle.statSaveEvents.isEmpty()) EventHandle.handleEvent(new GuiStatSavedEvent(stats), EventHandle.statSaveEvents);
		stats.save();
	}
	
	
	@Override
    public Color getBackgroundSelectionColor() {
        return null;
    }
    
    @Override
    public Color getBackgroundNonSelectionColor() {
        return null;
    }
    
    @Override
    public Color getBackground() {
        return null;
    }
    
    @Override
    public Color getTextNonSelectionColor() {
        return Color.WHITE;
    }
}