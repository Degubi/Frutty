package frutty.gui;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import frutty.gui.components.GuiHelper;
import frutty.tools.PropertyFile;

public final class GuiStats extends JPanel implements ActionListener{
	private static final PropertyFile stats = new PropertyFile("stats.prop", 3);
	public static int topScore = stats.getInt("topScore", 0);
	public static int enemyCount = stats.getInt("enemyCount", 0);
	public static int zoneCount = stats.getInt("zoneCount", 0);
	
	public GuiStats() {
		setLayout(null);
		JButton butt = new JButton("Reset");
		butt.setBounds(70, 100, 120, 30);
		butt.setMnemonic(100);
		butt.addActionListener(this);
		add(butt);
	}
	
	@Override
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		
		graphics.setFont(GuiHelper.thiccFont);
		graphics.drawString("Top Score: " + topScore, 20, 20);
		graphics.drawString("Enemies killed: " + enemyCount, 20, 40);
		graphics.drawString("Zones destroyed: " + zoneCount, 20, 60);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getActionCommand().equals("Reset")) {
			topScore = 0;
			enemyCount = 0;
			zoneCount = 0;
			repaint();
			saveStats();
		}
	}
	
	public static void saveStats() {
		stats.setInt("topScore", topScore);
		stats.setInt("enemyCount", enemyCount);
		stats.setInt("zoneCount", zoneCount);
		stats.save();
	}
}