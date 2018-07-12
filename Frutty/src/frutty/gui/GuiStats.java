package frutty.gui;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JPanel;

import frutty.tools.PropertyFile;

public final class GuiStats extends JPanel implements ActionListener{
	public static final PropertyFile stats = new PropertyFile("stats.prop", () -> Map.of("topScore", 0, "enemyCount", 0, "zoneCount", 0));
	
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
		graphics.drawString("Top Score: " + stats.getInt("topScore"), 20, 20);
		graphics.drawString("Enemies killed: " + stats.getInt("enemyCount"), 20, 40);
		graphics.drawString("Zones destroyed: " + stats.getInt("zoneCount"), 20, 60);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getActionCommand().equals("Reset")) {
			stats.set("topScore", 0);
			stats.set("enemyCount", 0);
			stats.set("zoneCount", 0);
			repaint();
			stats.save();
		}
	}
}