package frutty.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import frutty.Main;
import frutty.entity.Entity;
import frutty.entity.EntityEnemy;
import frutty.entity.EntityPlayer;
import frutty.map.Map;
import frutty.map.MapZone;
import frutty.map.zones.MapZoneFruit;
import frutty.stuff.EnumFruit;
import frutty.stuff.ITickable;

public final class GuiIngame extends JPanel implements Runnable, ActionListener{
	private final ScheduledExecutorService thread = Executors.newSingleThreadScheduledExecutor();
	
	static GuiIngame ingameGui;
	
	private boolean paused = false;
	private boolean renderEdge = false;
	public static BufferedImage texture;
	
	public GuiIngame() {
		setLayout(null);
		
		add(GuiHelper.newButton("Exit", Map.currentMap.width + 110, Map.currentMap.height - 50, this));
		add(GuiHelper.newButton("Save", Map.currentMap.width + 110, Map.currentMap.height - 100, this));
		thread.scheduleAtFixedRate(this, 20, 20, TimeUnit.MILLISECONDS);
	}
	
	@Override
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		
		for(MapZone drawZones : Map.currentMap.zones) {
			drawZones.draw(graphics);
		}
		
		for(EntityPlayer players : Map.currentMap.players)
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
		}
		
		graphics.setColor(Color.BLACK);
		graphics.setFont(GuiHelper.ingameFont);
		graphics.drawString("Score: " + Map.currentMap.score, Map.currentMap.width + 90, 20);
		graphics.drawString("Top score: " + GuiStats.topScore, Map.currentMap.width + 90, 80);
	}
	
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
		ingameGui.thread.shutdown();
		JOptionPane.showMessageDialog(null, message, "Frutty", JOptionPane.PLAIN_MESSAGE);
		GuiStats.saveStats();
		GuiMenu.showMenu();
		((JFrame)ingameGui.getTopLevelAncestor()).dispose();
	}
	
	public static void showIngame() {
		try {
			Main.loadThread.join();
		} catch (InterruptedException e) {}
		
		EventQueue.invokeLater(() -> {
			JFrame ingameFrame = new JFrame("Tutty Frutty");
			ingameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			ingameFrame.setResizable(false);
			ingameFrame.setBounds(0, 0, Map.currentMap.width + 288, Map.currentMap.height + 100);
			ingameFrame.setLocationRelativeTo(null);
			ingameFrame.setContentPane(ingameGui = new GuiIngame());
			ingameFrame.setFocusable(true);
			for(EntityPlayer players : Map.currentMap.players) {
				ingameFrame.addKeyListener(players);
				ingameFrame.addMouseListener(players);
			}
			ingameFrame.setVisible(true);
		});
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getActionCommand().equals("Exit")) {
			ingameGui.thread.shutdown();
			if(JOptionPane.showConfirmDialog(null, "Save current status?", "Save?", JOptionPane.YES_NO_OPTION, 1) == 0) {
				Map.createSave(JOptionPane.showInputDialog("Enter save name!"));
			}
			GuiStats.saveStats();
			GuiMenu.showMenu();
			((JFrame)getTopLevelAncestor()).dispose();
			
		}else{  //Save
			paused = true;
			if(Map.createSave(JOptionPane.showInputDialog("Enter save name!"))) {
				ingameGui.thread.shutdown();
				GuiMenu.showMenu();
				((JFrame)getTopLevelAncestor()).dispose();
			}else{
				paused = false;
				((JFrame)getTopLevelAncestor()).requestFocus();
			}
		}
	}
}