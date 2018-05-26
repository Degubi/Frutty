package frutty.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Iterator;
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
import frutty.gui.GuiSettings.Settings;
import frutty.map.Map;
import frutty.map.Particle;
import frutty.map.base.MapZone;
import frutty.map.interfaces.ITransparentZone;
import frutty.map.zones.MapZoneWater;

public final class GuiIngame extends JPanel implements Runnable, ActionListener{
	private final ScheduledExecutorService thread = Executors.newSingleThreadScheduledExecutor();
	
	static GuiIngame ingameGui;
	
	private boolean paused = false;
	public static BufferedImage skyTexture;
	public static BufferedImage[] textures;
	
	private int renderDelay;
	private long renderLastUpdate = System.currentTimeMillis();
	
	public GuiIngame() {
		setLayout(null);
		
		add(GuiHelper.newButton("Exit", Map.currentMap.width + 100, Map.currentMap.height - 20, this));
		add(GuiHelper.newButton("Save", Map.currentMap.width + 100, Map.currentMap.height - 100, this));
		add(GuiHelper.newButton("Pause", Map.currentMap.width + 100, Map.currentMap.height - 180, this));
		thread.scheduleAtFixedRate(this, 20, 20, TimeUnit.MILLISECONDS);
	}
	
	@Override
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		
		for(int k = 0; k < Map.currentMap.zones.length; ++k) {
			Map.currentMap.zones[k].render(Map.currentMap.xCoords[k], Map.currentMap.yCoords[k], Map.currentMap.textureData[k], graphics);
		}
		for(EntityPlayer players : Map.currentMap.players) players.render(graphics);
		
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
		
		for(int k = 0; k < Map.currentMap.zones.length; ++k) {
			MapZone zone = Map.currentMap.zones[k];
			if(zone instanceof ITransparentZone) {
				((ITransparentZone)zone).drawAfter(Map.currentMap.xCoords[k], Map.currentMap.yCoords[k], Map.currentMap.textureData[k], graphics);
			}
		}
		
		for(Particle particles : Map.currentMap.particles) particles.render(graphics);
		
		graphics.setColor(Color.DARK_GRAY);
		for(int k = 0; k < 20; ++k) {
			graphics.drawLine(Map.currentMap.width + 64 + k, 0, Map.currentMap.width + 64 + k, Map.currentMap.height + 83);
		}
		
		graphics.setColor(Color.BLACK);
		graphics.setFont(GuiHelper.ingameFont);
		graphics.drawString("Score: " + Map.currentMap.score, Map.currentMap.width + 90, 20);
		graphics.drawString("Top score: " + GuiStats.topScore, Map.currentMap.width + 90, 80);
		
		if(Settings.showDebug) {
			graphics.setColor(GuiHelper.color_128Black);
			graphics.fillRect(0, 0, 130, 130);
			
			graphics.setFont(GuiHelper.thiccFont);
			graphics.setColor(Color.WHITE);
			
			//Left
			graphics.drawString("zonecount: " + Map.currentMap.zones.length, 2, 20);
			graphics.drawString("entities: " + (Map.currentMap.enemies.length + Map.currentMap.players.length + Map.currentMap.entities.size() + Map.currentMap.particles.size()), 2, 40);
			graphics.drawString("map_width: " + (Map.currentMap.width + 64), 2, 60);
			graphics.drawString("map_height: " + (Map.currentMap.height + 64), 2, 80);
			graphics.drawString("playerpos_x: " + Map.currentMap.players[0].serverPosX, 2, 100);
			graphics.drawString("playerpos_y: " + Map.currentMap.players[0].serverPosY, 2, 120);
			
			//Right
			graphics.drawString("render delay: " + renderDelay + " ms", Map.currentMap.width - 80, 20);
			
			renderDelay = (int) (System.currentTimeMillis() - renderLastUpdate);
			renderLastUpdate = System.currentTimeMillis();
		}
	}
	
	@Override
	public void run() {
		if(!paused) {
			repaint();
			++Map.currentMap.ticks;
			
			for(Entity entity : Map.currentMap.players) {
				entity.update(Map.currentMap.ticks);
			}
			
			for(EntityEnemy monsters : Map.currentMap.enemies) {
				if(monsters.active) {
					monsters.update(Map.currentMap.ticks);
				}
			}
			
			for(Entity entities : Map.currentMap.entities) {
				if(entities.active) {
					entities.update(Map.currentMap.ticks);
				}
			}
			
			if(Map.currentMap.ticks % 4 == 0) {
				MapZoneWater.updateWaterUV();
			}
			
			if(Map.currentMap.ticks % 20 == 0) {
				for(int k = 0; k < Map.currentMap.zones.length; ++k) {
					MapZone zone = Map.currentMap.zones[k];
					if(zone != Main.emptyZone && MapZone.isEmpty(Map.currentMap.xCoords[k], Map.currentMap.yCoords[k] + 64) && Main.rand.nextInt(100) == 3) {
						Particle.addParticles(2 + Main.rand.nextInt(5), Map.currentMap.xCoords[k], Map.currentMap.yCoords[k], Map.currentMap.textureData[k]);
					}
					
					if(zone.hasZoneEntity()) {
						Map.getZoneEntity(k).update();
					}
				}
				
				for(Iterator<Particle> iterator = Map.currentMap.particles.iterator(); iterator.hasNext();) {
					iterator.next().update(iterator);
				}
			}
		}
	}
	
	public static void showMessageAndClose(String message) {
		ingameGui.thread.shutdown();
		JOptionPane.showMessageDialog(null, message, "Frutty", JOptionPane.PLAIN_MESSAGE);
		GuiMenu.showMenu();
		((JFrame)ingameGui.getTopLevelAncestor()).dispose();
		GuiStats.saveStats();
	}
	
	public static void showIngame() {
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
			Settings.saveSettings();
			((JFrame)getTopLevelAncestor()).dispose();
		}else if(event.getActionCommand().equals("Pause")) {
			paused = !paused;
			((JFrame)getTopLevelAncestor()).requestFocus();
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