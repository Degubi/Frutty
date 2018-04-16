package frutty.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import frutty.entity.Entity;
import frutty.entity.EntityEnemy;
import frutty.entity.EntityPlayer;
import frutty.map.Map;
import frutty.map.MapZone;
import frutty.map.zones.MapZoneFruit;
import frutty.stuff.EnumFruit;
import frutty.stuff.ITickable;

/**
 * A fõ gui, ingame elementek és updatelések lekezelése folyik itt.
 */
public class GuiIngame extends JPanel implements Runnable, ActionListener{
	//Idõzíthetõ Thread, Timer helyett van, ugyanis az java9-ben deprecated lett, ez lényegében ugyan az, nem kell kézzel írt Threaded csinálni
	private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	public static final Random rand = new Random();  //Main-be nem lehet tenni a default package miatt
	
	static GuiIngame ingameGui;
	
	private boolean paused = false;
	
	public GuiIngame() {
		setLayout(null);
		
		add(GuiHelper.newButton("Exit", Map.currentMap.width + 110, Map.currentMap.height - 50, this));
		add(GuiHelper.newButton("Save", Map.currentMap.width + 110, Map.currentMap.height - 100, this));
		executor.scheduleAtFixedRate(this, 20, 20, TimeUnit.MILLISECONDS);
	}
	
	@Override
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		
		for(MapZone drawZones : Map.currentMap.zones) {
			drawZones.draw(graphics);
		}
		
		for(EntityPlayer players : Map.getPlayers())
			players.render(graphics);
		
		for(Entity entity : Map.currentMap.entities) {
			if(entity.active) {
				entity.render(graphics);
			}
		}
		
		for(EntityEnemy enemies : Map.currentMap.enemies) {
			if(enemies.active) {
				enemies.render(graphics);
			}
		}
		
		graphics.setColor(Color.GRAY);
		for(int k = 0; k < 20; ++k) {
			graphics.drawLine(Map.currentMap.width + 64 + k, 0, Map.currentMap.width + 64 + k, Map.currentMap.height + 83);
			graphics.drawLine(0, Map.currentMap.height + 64 + k, Map.currentMap.width + 64, Map.currentMap.height + 64 + k);
		}
		
		graphics.setColor(Color.BLACK);
		graphics.setFont(GuiHelper.ingameFont);
		graphics.drawString("Score: " + Map.currentMap.score, Map.currentMap.width + 90, 20);
		graphics.drawString("Top score: " + GuiStats.topScore, Map.currentMap.width + 90, 80);
	}
	
	/**
	 * Fõ update függvény, itt frissül az összes entity, illetve a render függvény újrahívása is itt történik.
	 */
	@Override
	public void run() {
		if(!paused) {
			repaint();
			++Map.currentMap.ticks;
			
			if(Map.currentMap.ticks % 5 == 0) {
				for(Entity entities : Map.currentMap.entities) {
					if(entities.active) {
						entities.update(Map.currentMap.ticks);
					}
				}
				for(EntityEnemy monsters : Map.currentMap.enemies) {
					if(monsters.active) {
						monsters.update(Map.currentMap.ticks);
					}
				}
				
				if(Map.currentMap.ticks % 20 == 0) {
					for(MapZone zone : Map.currentMap.zones) {
						if(zone instanceof ITickable && ((MapZoneFruit)zone).fruitType == EnumFruit.APPLE) {
							((ITickable) zone).update();
						}
					}
				}
			}
		}
	}
	
	public static void showMessageAndClose(String message) {
		ingameGui.executor.shutdown();
		JOptionPane.showMessageDialog(null, message, "Frutty", JOptionPane.PLAIN_MESSAGE);
		GuiStats.saveStats();
		GuiMenu.showMenu();
		((JFrame)ingameGui.getTopLevelAncestor()).dispose();
	}
	
	public static void showIngame() {
		JFrame ingameFrame = GuiHelper.newFrame(ingameGui = new GuiIngame(), JFrame.EXIT_ON_CLOSE, Map.currentMap.width + 288, Map.currentMap.height + 96);
		for(EntityPlayer players : Map.getPlayers()) {
			ingameFrame.addKeyListener(players);
			ingameFrame.addMouseListener(players);
		}
		ingameFrame.setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getActionCommand().equals("Exit")) {
			ingameGui.executor.shutdown();
			if(JOptionPane.showConfirmDialog(null, "Save current status?", "Save?", JOptionPane.YES_NO_OPTION, 1) == 0) {
				Map.createSave(JOptionPane.showInputDialog("Enter save name!"));
			}
			GuiStats.saveStats();
			GuiMenu.showMenu();
			((JFrame)getTopLevelAncestor()).dispose();
			
		}else{  //Save
			paused = true;
			if(Map.createSave(JOptionPane.showInputDialog("Enter save name!"))) {
				ingameGui.executor.shutdown();
				GuiMenu.showMenu();
				((JFrame)getTopLevelAncestor()).dispose();
			}else{
				paused = false;
				((JFrame)getTopLevelAncestor()).requestFocus();
			}
		}
	}
}