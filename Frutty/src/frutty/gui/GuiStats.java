package frutty.gui;

import static frutty.tools.GuiHelper.newButton;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import frutty.plugin.event.gui.GuiStatInitEvent;
import frutty.plugin.event.gui.GuiStatSavedEvent;
import frutty.plugin.internal.EventHandle;
import frutty.tools.GuiHelper;
import frutty.tools.PropertyFile;

public final class GuiStats extends JPanel implements ActionListener{
	private static final PropertyFile stats = new PropertyFile("stats.prop", 4);
	public static int topScore = stats.getInt("topScore", 0);
	public static int enemyCount = stats.getInt("enemyCount", 0);
	public static int zoneCount = stats.getInt("zoneCount", 0);
	public static int playTime = stats.getInt("minutesPlayed", 0);
	
	public GuiStats() {
		setLayout(null);
		
		DefaultMutableTreeNode top = new DefaultMutableTreeNode("Frutty Stats");
		
		DefaultMutableTreeNode basic = new DefaultMutableTreeNode("Basics");
		DefaultMutableTreeNode zones = new DefaultMutableTreeNode("Zones");
		DefaultMutableTreeNode enemies = new DefaultMutableTreeNode("Enemies");
		
		basic.add(new DefaultMutableTreeNode("Minutes Played: " + playTime));
		basic.add(new DefaultMutableTreeNode("Max Score: " + topScore));
		zones.add(new DefaultMutableTreeNode("Zones Destroyed: " + zoneCount));
		enemies.add(new DefaultMutableTreeNode("Enemies Killed: " + enemyCount));
		
		top.add(basic);
		top.add(zones);
		top.add(enemies);
		
		JTree statTree = new JTree(top);
		statTree.setCellRenderer(new TreeRender());
		statTree.setFont(GuiHelper.thiccFont);
		statTree.setOpaque(false);
		statTree.setBounds(50, 50, 800, 400);
		
		if(!EventHandle.statInitEvents.isEmpty()) {
			EventHandle.handleEvent(new GuiStatInitEvent(stats, basic, zones, enemies, top), EventHandle.statInitEvents);
		}
		
		add(statTree);
		
		add(newButton("Reset", 100, 550, this));
		add(newButton("Menu", 370, 550, this));
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getActionCommand().equals("Reset")) {
			topScore = 0;
			enemyCount = 0;
			zoneCount = 0;
			repaint();
			saveStats();
		}else{
			GuiHelper.switchMenuPanel(new GuiMenu());
		}
	}
	
	public static void saveStats() {
		stats.setInt("topScore", topScore);
		stats.setInt("enemyCount", enemyCount);
		stats.setInt("zoneCount", zoneCount);
		
		if(!EventHandle.statSaveEvents.isEmpty()) EventHandle.handleEvent(new GuiStatSavedEvent(stats), EventHandle.statSaveEvents);
		stats.save();
	}
	
	protected static final class TreeRender extends DefaultTreeCellRenderer{
		
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
	}
}