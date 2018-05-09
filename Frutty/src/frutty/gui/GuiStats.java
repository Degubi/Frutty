package frutty.gui;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JFrame;
import javax.swing.JPanel;

import frutty.map.Map;

public final class GuiStats extends JPanel implements ActionListener{
	public static int topScore, enemyCount, zoneCount; 
	
	public GuiStats() {
		setLayout(null);
		
		add(GuiHelper.newButton("Reset", 70, 100, this));
	}
	
	public static void loadStats() {
		try(BufferedReader input = new BufferedReader(new FileReader("stats.prop"))){
			String[] split = input.readLine().split(" ");
			topScore = Integer.parseInt(split[0]);
			enemyCount = Integer.parseInt(split[1]);
			zoneCount = Integer.parseInt(split[2]);
		} catch (IOException e2) {
			saveStats();
		}
	}
	
	public static void compareScores() {
		if(Map.currentMap.score > topScore) {
			topScore = Map.currentMap.score;
			saveStats();
		}
	}
	
	public static void openStatsGui() {
		GuiHelper.showNewFrame(new GuiStats(), "Tutty Frutty", JFrame.DISPOSE_ON_CLOSE, 240, 180);
	}
	
	public static void saveStats() {
		try(PrintWriter output = new PrintWriter("stats.prop")){
			output.print(topScore);
			output.print(' ');
			output.print(enemyCount);
			output.print(' ');
			output.println(zoneCount);
		} catch (FileNotFoundException e) {}
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