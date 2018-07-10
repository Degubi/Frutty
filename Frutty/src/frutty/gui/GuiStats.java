package frutty.gui;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.JButton;
import javax.swing.JPanel;

import frutty.map.Map;

public final class GuiStats extends JPanel implements ActionListener{
	public static int topScore, enemyCount, zoneCount; 
	
	public GuiStats() {
		setLayout(null);
		
		JButton butt = new JButton("Reset");
		butt.setBounds(70, 100, 120, 30);
		butt.setMnemonic(100);
		butt.addActionListener(this);
		add(butt);
	}
	
	public static void loadStats() {
		try(var input = Files.newBufferedReader(Paths.get("stats.prop"))){
			String[] split = input.readLine().split(" ");
			topScore = Integer.parseInt(split[0]);
			enemyCount = Integer.parseInt(split[1]);
			zoneCount = Integer.parseInt(split[2]);
		} catch (IOException e2) {
			saveStats();
		}
	}
	
	public static void compareScores() {
		if(Map.score > topScore) {
			topScore = Map.score;
			saveStats();
		}
	}

	public static void saveStats() {
		try(var output = new PrintWriter("stats.prop")){
			output.print(topScore);
			output.print(' ');
			output.print(enemyCount);
			output.print(' ');
			output.println(zoneCount);
		} catch (FileNotFoundException e) {
			//Can't rly happen
		}
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
			topScore = 0; enemyCount = 0; zoneCount = 0;
			repaint();
			saveStats();
		}
	}
}