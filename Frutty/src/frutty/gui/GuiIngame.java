package frutty.gui;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import frutty.FruttyMain;
import frutty.entity.Entity;
import frutty.entity.EntityEnemy;
import frutty.entity.EntityPlayer;
import frutty.entity.zone.EntityZone;
import frutty.gui.GuiSettings.Settings;
import frutty.tools.GuiHelper;
import frutty.tools.IOHelper;
import frutty.world.Particle;
import frutty.world.World;
import frutty.world.base.ITransparentZone;
import frutty.world.base.IZoneEntityProvider;
import frutty.world.base.MapZoneBase;
import frutty.world.zones.MapZoneWater;

public final class GuiIngame extends JPanel implements Runnable, KeyListener{
	public static GuiIngame ingameGui;
	
	protected final ScheduledExecutorService updateThread = Executors.newSingleThreadScheduledExecutor();
	protected final ScheduledExecutorService renderThread = Executors.newSingleThreadScheduledExecutor();
	protected boolean paused = false;
	private final LocalTime startTime = LocalTime.now();
	private int renderDelay;
	private long renderLastUpdate = System.currentTimeMillis();
	
	public GuiIngame() {
		setLayout(null);
		updateThread.scheduleAtFixedRate(this, 0, 20, TimeUnit.MILLISECONDS);
		renderThread.scheduleAtFixedRate(() -> ingameGui.repaint(), 0, 1000 / Settings.fps, TimeUnit.MILLISECONDS);
	}
	
	@Override
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		
		MapZoneBase[] zones = World.zones;
		
		for(int k = 0; k < zones.length; ++k) zones[k].render(World.xCoords[k], World.yCoords[k], World.materials[k], (Graphics2D) graphics);
		for(EntityPlayer players : World.players) players.handleRender(graphics);
		
		for(Entity entity : World.entities) {
			if(entity.active) {
				entity.handleRender(graphics);
			}
		}
		
		for(EntityEnemy enemies : World.enemies) {
			if(enemies.active) {
				enemies.handleRender(graphics);
			}
		}
		
		for(Particle particles : World.particles) particles.render(graphics);
		
		for(int k = 0; k < zones.length; ++k) {
			MapZoneBase zone = zones[k];
			if(zone instanceof ITransparentZone) {
				((ITransparentZone)zone).drawAfter(World.xCoords[k], World.yCoords[k], World.materials[k], graphics);
			}
		}
		
		graphics.setColor(Color.BLACK);
		graphics.setFont(GuiHelper.ingameFont);
		graphics.drawString("Score: " + World.score, World.width + 90, 20);
		graphics.drawString("Top score: " + GuiStats.topScore, World.width + 90, 80);
		
		if(Settings.enableMapDebug) {
			graphics.setColor(GuiHelper.color_128Black);
			graphics.fillRect(0, 0, 130, 130);
			
			graphics.setFont(GuiHelper.thiccFont);
			graphics.setColor(Color.WHITE);
			
			//Left
			graphics.drawString("zonecount: " + zones.length, 2, 20);
			graphics.drawString("entities: " + (World.enemies.length + World.players.length + World.entities.size() + World.particles.size()), 2, 40);
			graphics.drawString("map_width: " + (World.width + 64), 2, 60);
			graphics.drawString("map_height: " + (World.height + 64), 2, 80);
			graphics.drawString("playerpos_x: " + World.players[0].serverPosX, 2, 100);
			graphics.drawString("playerpos_y: " + World.players[0].serverPosY, 2, 120);
		}
		
		if(Settings.renderDebugLevel == 1 || Settings.renderDebugLevel == 3) {
			graphics.setColor(GuiHelper.color_128Black);
			graphics.fillRect(World.width - 85, 0, 160, 130);
			
			graphics.setFont(GuiHelper.thiccFont);
			graphics.setColor(Color.WHITE);
			
			//Right
			graphics.drawString("current map: " + World.mapName, World.width - 100, 20);
			graphics.drawString("render delay: " + renderDelay + " ms", World.width - 100, 40);
			graphics.drawString("fps: " + 1000 / (System.currentTimeMillis() - renderLastUpdate), World.width - 100, 60);
			
			renderDelay = (int) (System.currentTimeMillis() - renderLastUpdate);
			renderLastUpdate = System.currentTimeMillis();
		}
		
		graphics.setColor(Color.DARK_GRAY);
		for(int k = 0; k < 20; ++k) graphics.drawLine(World.width + 64 + k, 0, World.width + 64 + k, World.height + 83);
	}
	
	@Override
	public void run() {
		if(!paused) {
			++World.ticks;
			
			for(Entity entity : World.players) entity.update(World.ticks);
			
			for(EntityEnemy monsters : World.enemies) {
				if(monsters.active) {
					monsters.update(World.ticks);
				}
			}
			
			for(Entity entities : World.entities) {
				if(entities.active) {
					entities.update(World.ticks);
				}
			}
			
			if(World.ticks % 4 == 0) MapZoneWater.updateWaterUV();
			
			if(World.ticks % 2 == 0) {
				for(Iterator<Particle> iterator = World.particles.iterator(); iterator.hasNext();) {
					iterator.next().update(iterator);
				}
			}
			
			if(World.ticks % 20 == 0) {
				for(int k = 0; k < World.zones.length; ++k) {
					MapZoneBase zone = World.zones[k];
					if(zone.hasParticleSpawns && MapZoneBase.isEmpty(World.xCoords[k], World.yCoords[k] + 64) && FruttyMain.rand.nextInt(100) == 3) {
						Particle.spawnFallingParticles(2 + FruttyMain.rand.nextInt(5), World.xCoords[k], World.yCoords[k], World.materials[k]);
					}
					
					if(zone instanceof IZoneEntityProvider) {
						EntityZone entity = World.zoneEntities[k];
						if(entity.shouldUpdate) {
							entity.update();
						}
					}
				}
			}
		}
	}
	
	public static void showMessageAndClose(String message) {
		ingameGui.updateThread.shutdown();
		ingameGui.renderThread.shutdown();
		JOptionPane.showMessageDialog(null, message, "Frutty", JOptionPane.PLAIN_MESSAGE);
		GuiMenu.createMainFrame(false);
		((JFrame)ingameGui.getTopLevelAncestor()).dispose();
		if(GuiStats.topScore < World.score) {
			GuiStats.topScore = World.score;
		}
		GuiStats.playTime += ingameGui.startTime.until(LocalTime.now(), ChronoUnit.MINUTES);
		GuiStats.saveStats();
		World.cleanUp();
	}
	
	public static void showIngame() {
		EventQueue.invokeLater(() -> {
			JFrame ingameFrame = new JFrame("Frutty");
			ingameFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			ingameFrame.setResizable(false);
			ingameFrame.setBounds(0, 0, World.width + 288, World.height + 100);
			ingameFrame.setLocationRelativeTo(null);
			ingameFrame.setContentPane(ingameGui = new GuiIngame());
			ingameFrame.addKeyListener(ingameGui);
			ingameFrame.setFocusable(true);
			for(EntityPlayer players : World.players) {
				ingameFrame.addKeyListener(players);
			}
			ingameFrame.setVisible(true);
		});
	}
	
	@Override
	public void keyPressed(KeyEvent event) {
		int keyCode = event.getKeyCode();
		
		if(keyCode == KeyEvent.VK_ESCAPE) {
			paused = true;
			
			EventQueue.invokeLater(() -> {
				JFrame returnFrame = new JFrame("Frutty");
				PauseMenu menu = new PauseMenu();
				returnFrame.setContentPane(menu);
				returnFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				returnFrame.setResizable(false);
				returnFrame.setBounds(0, 0, 600, 540);
				returnFrame.setLocationRelativeTo(null);
				returnFrame.setFocusable(true);
				returnFrame.addKeyListener(menu);
				returnFrame.addWindowListener(menu);
				returnFrame.setVisible(true);
			});
		}else if(keyCode == KeyEvent.VK_F12) {
			try {
				IOHelper.createDirectory("screenshots");
				var window = ((JFrame)getTopLevelAncestor()).getLocationOnScreen();
				ImageIO.write(new Robot().createScreenCapture(new Rectangle(window.x + 7, window.y + 30, World.width + 64, World.height + 64)), Settings.screenshotFormat, 
																			new File("./screenshots/" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_kk_HH_ss")) +"." + Settings.screenshotFormat.toLowerCase()));
			} catch (HeadlessException | AWTException | IOException e1) {
				e1.printStackTrace();
			}
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
				ingameGui.renderThread.shutdown();
				ingameGui.updateThread.shutdown();
				if(JOptionPane.showConfirmDialog(null, "Save current status?", "Save?", JOptionPane.YES_NO_OPTION, 1) == 0) {
					World.createSave(JOptionPane.showInputDialog("Enter save name!"));
				}
				GuiStats.saveStats();
				System.exit(0);
			}else if(cmd.equals("Menu")) {
				ingameGui.renderThread.shutdown();
				ingameGui.updateThread.shutdown();
				if(JOptionPane.showConfirmDialog(null, "Save current status?", "Save?", JOptionPane.YES_NO_OPTION, 1) == 0) {
					World.createSave(JOptionPane.showInputDialog("Enter save name!"));
				}
				((JFrame)ingameGui.getTopLevelAncestor()).dispose();
				GuiMenu.createMainFrame(false);
				GuiStats.saveStats();
				World.cleanUp();
			}else{  //Save
				ingameGui.paused = true;
				World.createSave(JOptionPane.showInputDialog("Enter save name!"));
				ingameGui.paused = false;
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