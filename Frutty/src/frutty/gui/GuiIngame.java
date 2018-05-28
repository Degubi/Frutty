package frutty.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
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
import frutty.map.zones.MapZoneEmpty;
import frutty.map.zones.MapZoneWater;

public final class GuiIngame extends JPanel implements Runnable, KeyListener{
	private final ScheduledExecutorService thread = Executors.newSingleThreadScheduledExecutor();
	
	static GuiIngame ingameGui;
	
	private boolean paused = false;
	public static BufferedImage skyTexture;
	public static BufferedImage[] textures;
	
	private int renderDelay;
	private long renderLastUpdate = System.currentTimeMillis();
	
	public GuiIngame() {
		setLayout(null);
		thread.scheduleAtFixedRate(this, 20, 20, TimeUnit.MILLISECONDS);
	}
	
	@Override
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		
		for(int k = 0; k < Map.zones.length; ++k) Map.zones[k].render(Map.xCoords[k], Map.yCoords[k], Map.textureData[k], graphics);
		for(EntityPlayer players : Map.players) players.render(graphics);
		
		for(Entity entity : Map.entities) {
			if(entity.active) {
				entity.render(graphics);
			}
		}
		
		for(EntityEnemy enemies : Map.enemies) {
			if(enemies.active) {
				enemies.render(graphics);
			}
		}
		
		for(int k = 0; k < Map.zones.length; ++k) {
			MapZone zone = Map.zones[k];
			if(zone instanceof ITransparentZone) {
				((ITransparentZone)zone).drawAfter(Map.xCoords[k], Map.yCoords[k], Map.textureData[k], graphics);
			}
		}
		
		for(Particle particles : Map.particles) particles.render(graphics);
		
		graphics.setColor(Color.DARK_GRAY);
		for(int k = 0; k < 20; ++k) graphics.drawLine(Map.width + 64 + k, 0, Map.width + 64 + k, Map.height + 83);
		
		graphics.setColor(Color.BLACK);
		graphics.setFont(GuiHelper.ingameFont);
		graphics.drawString("Score: " + Map.score, Map.width + 90, 20);
		graphics.drawString("Top score: " + GuiStats.topScore, Map.width + 90, 80);
		
		if(Settings.showDebug) {
			graphics.setColor(GuiHelper.color_128Black);
			graphics.fillRect(0, 0, 130, 130);
			
			graphics.setFont(GuiHelper.thiccFont);
			graphics.setColor(Color.WHITE);
			
			//Left
			graphics.drawString("zonecount: " + Map.zones.length, 2, 20);
			graphics.drawString("entities: " + (Map.enemies.length + Map.players.length + Map.entities.size() + Map.particles.size()), 2, 40);
			graphics.drawString("map_width: " + (Map.width + 64), 2, 60);
			graphics.drawString("map_height: " + (Map.height + 64), 2, 80);
			graphics.drawString("playerpos_x: " + Map.players[0].serverPosX, 2, 100);
			graphics.drawString("playerpos_y: " + Map.players[0].serverPosY, 2, 120);
			
			//Right
			graphics.drawString("render delay: " + renderDelay + " ms", Map.width - 80, 20);
			
			renderDelay = (int) (System.currentTimeMillis() - renderLastUpdate);
			renderLastUpdate = System.currentTimeMillis();
		}
	}
	
	@Override
	public void run() {
		if(!paused) {
			repaint();
			++Map.ticks;
			
			for(Entity entity : Map.players) {
				entity.update(Map.ticks);
			}
			
			for(EntityEnemy monsters : Map.enemies) {
				if(monsters.active) {
					monsters.update(Map.ticks);
				}
			}
			
			for(Entity entities : Map.entities) {
				if(entities.active) {
					entities.update(Map.ticks);
				}
			}
			
			if(Map.ticks % 4 == 0) {
				MapZoneWater.updateWaterUV();
			}
			
			if(Map.ticks % 20 == 0) {
				for(int k = 0; k < Map.zones.length; ++k) {
					MapZone zone = Map.zones[k];
					if(zone instanceof MapZoneEmpty == false && MapZone.isEmpty(Map.xCoords[k], Map.yCoords[k] + 64) && Main.rand.nextInt(100) == 3) {
						Particle.addParticles(2 + Main.rand.nextInt(5), Map.xCoords[k], Map.yCoords[k], Map.textureData[k]);
					}
					
					if(zone.hasZoneEntity()) {
						Map.getZoneEntity(k).update();
					}
				}
				
				for(Iterator<Particle> iterator = Map.particles.iterator(); iterator.hasNext();) {
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
			ingameFrame.setBounds(0, 0, Map.width + 288, Map.height + 100);
			ingameFrame.setLocationRelativeTo(null);
			ingameFrame.setContentPane(ingameGui = new GuiIngame());
			ingameFrame.addKeyListener(ingameGui);
			ingameFrame.setFocusable(true);
			for(EntityPlayer players : Map.players) {
				ingameFrame.addKeyListener(players);
				ingameFrame.addMouseListener(players);
			}
			ingameFrame.setVisible(true);
		});
	}
	
	@Override
	public void keyPressed(KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.VK_ESCAPE) {
			paused = true;
			
			EventQueue.invokeLater(() -> {
				JFrame returnFrame = new JFrame("Frutty");
				PauseMenu menu = new PauseMenu();
				returnFrame.setContentPane(menu);
				returnFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				returnFrame.setResizable(false);
				returnFrame.setBounds(0, 0, 600, 540);
				returnFrame.setLocationRelativeTo(null);
				returnFrame.setFocusable(true);
				returnFrame.addKeyListener(menu);
				returnFrame.addWindowListener(menu);
				returnFrame.setVisible(true);
			});
		}
	}
	
	private static final class PauseMenu extends JPanel implements ActionListener, WindowListener, KeyListener{
		public PauseMenu() {
			setLayout(null);
			
			add(GuiHelper.newButton("Resume", 220, 180, this));
			add(GuiHelper.newButton("Save", 220, 260, this));
			add(GuiHelper.newButton("Menu", 220, 340, this));
			add(GuiHelper.newButton("Exit", 220, 420, this));
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			String cmd = event.getActionCommand();
			if(cmd.equals("Resume")) {
				ingameGui.paused = false;
			}else if(cmd.equals("Exit")) {
				ingameGui.thread.shutdown();
				if(JOptionPane.showConfirmDialog(null, "Save current status?", "Save?", JOptionPane.YES_NO_OPTION, 1) == 0) {
					Map.createSave(JOptionPane.showInputDialog("Enter save name!"));
				}
				GuiStats.saveStats();
				Settings.saveSettings();
				System.exit(0);
			}else if(cmd.equals("Menu")) {
				ingameGui.thread.shutdown();
				if(JOptionPane.showConfirmDialog(null, "Save current status?", "Save?", JOptionPane.YES_NO_OPTION, 1) == 0) {
					Map.createSave(JOptionPane.showInputDialog("Enter save name!"));
				}
				((JFrame)ingameGui.getTopLevelAncestor()).dispose();
				GuiMenu.showMenu();
				GuiStats.saveStats();
				Settings.saveSettings();
			}else{  //Save
				ingameGui.paused = true;
				Map.createSave(JOptionPane.showInputDialog("Enter save name!"));
				ingameGui.paused = false;
				Settings.saveSettings();
				GuiStats.saveStats();
			}
			((JFrame)getTopLevelAncestor()).dispose();
		}
		
		@Override
		protected void paintComponent(Graphics graphics) {
			super.paintComponent(graphics);
			
			graphics.setColor(GuiHelper.color_84Black);
			graphics.fillRect(0, 0, 600, 540);
		}
		
		@Override
		public void keyPressed(KeyEvent event) {
			if(event.getKeyCode() == KeyEvent.VK_ESCAPE) {
				((JFrame)getTopLevelAncestor()).dispose();
				ingameGui.paused = false;
			}
		}
		
		@Override
		public void windowClosing(WindowEvent event) {
			ingameGui.paused = false;
		}

		@Override public void windowOpened(WindowEvent e) {} @Override public void windowClosed(WindowEvent e) {} @Override public void windowIconified(WindowEvent e) {} @Override public void windowDeiconified(WindowEvent e) {} @Override public void windowActivated(WindowEvent e) {} @Override public void windowDeactivated(WindowEvent e) {}
		@Override public void keyTyped(KeyEvent e) {} @Override public void keyReleased(KeyEvent e) {}
	}

	@Override public void keyTyped(KeyEvent e) {} @Override public void keyReleased(KeyEvent e) {}
}