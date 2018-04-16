package frutty.gui;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;

import frutty.map.Map;

/**
 * A stat gui class fájlja
 */
public class GuiStats extends JPanel implements ActionListener{
	public static int topScore, enemyCount, zoneCount; 
	
	/**
	 * Reset gomb hozzáadása és layout nullázása
	 */
	public GuiStats() {
		setLayout(null);
		
		add(GuiHelper.newButton("Reset", 70, 100, this));
	}
	
	/**
	 * Statok betöltésére szolgáló függvény
	 */
	public static void loadStats() {
		try(Scanner input = new Scanner(new File("stats.prop"))){
			String[] split = input.nextLine().split(" ");
			topScore = Integer.parseInt(split[0]);
			enemyCount = Integer.parseInt(split[1]);
			zoneCount = Integer.parseInt(split[2]);
		} catch (FileNotFoundException e2) {
			saveStats();
		}
	}
	
	/**
	 * Pontok összehasonlítása, illetve mentés ha a jelenlegi pont nagyobb mint az eddigi rekord
	 */
	public static void compareScores() {
		if(Map.currentMap.score > topScore) {
			topScore = Map.currentMap.score;
			saveStats();
		}
	}
	
	public static void openStatsGui() {
		GuiHelper.newFrame(new GuiStats(), JFrame.DISPOSE_ON_CLOSE, 240, 180).setVisible(true);
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