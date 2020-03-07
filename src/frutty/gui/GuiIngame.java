package frutty.gui;

import frutty.*;
import frutty.gui.GuiSettings.*;
import frutty.tools.*;
import frutty.world.*;
import frutty.world.base.*;
import frutty.world.zones.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.*;
import java.time.format.*;
import java.time.temporal.*;
import java.util.concurrent.*;
import javax.imageio.*;
import javax.swing.*;

public final class GuiIngame extends JPanel implements KeyListener{
    private static ScheduledExecutorService serverThread;
    private static ScheduledExecutorService renderThread;
    private static ScheduledFuture<?> serverTask;
    
    static JFrame ingameFrame;
    private static GuiIngame ingamePanel;
    private static LocalTime startTime;
    private static long renderLastUpdate;
    
    @Override
    protected void paintComponent(Graphics graphics) {
        var zones = World.zones;
        var xCoords = World.xCoords;
        var yCoords = World.yCoords;
        var materials = World.materials;

        if(Settings.renderDebugLevel < 2) {
            for(var k = 0; k < zones.length; ++k) zones[k].renderInternal(xCoords[k], yCoords[k], materials[k], graphics);
        }else{
            for(var k = 0; k < zones.length; ++k) zones[k].renderDebug(xCoords[k], yCoords[k], materials[k], graphics);
        }
        
        if(Settings.enableCollisionDebug) {
            for(var players : World.players) players.renderDebug(graphics);
            for(var entity : World.entities) entity.renderDebug(graphics);
            
            for(var enemies : World.enemies) {
                if(enemies.active) {
                    enemies.renderDebug(graphics);
                }
            }
            
            for(var particles : World.particles) particles.renderDebug(graphics);
        }else{
            for(var players : World.players) players.render(graphics);
            for(var entity : World.entities) entity.render(graphics);
            
            for(var enemies : World.enemies) {
                if(enemies.active) {
                    enemies.render(graphics);
                }
            }
            
            for(var particles : World.particles) particles.render(graphics);
        }
        
        for(var k = 0; k < zones.length; ++k) {
            var zone = zones[k];
            
            if(zone instanceof ITransparentZone) {
                ((ITransparentZone)zone).drawAfter(xCoords[k], yCoords[k], materials[k], graphics);
            }
        }
        
        var worldWidth = World.width;
        var worldHeight = World.height;
        
        graphics.setColor(Color.BLACK);
        graphics.setFont(GuiHelper.ingameFont);
        graphics.drawString("Score: " + World.score, worldWidth + 90, 20);
        graphics.drawString("Top score: " + GuiStats.topScore, worldWidth + 90, 80);
        
        if(Settings.enableMapDebug) {
            graphics.setFont(GuiHelper.thiccFont);
            graphics.setColor(Color.WHITE);
            
            //Left
            graphics.drawString("zonecount: " + zones.length, 2, 20);
            graphics.drawString("entities: " + (World.enemies.length + World.players.length + World.entities.size() + World.particles.size()), 2, 40);
            graphics.drawString("map_width: " + (worldWidth + 64), 2, 60);
            graphics.drawString("map_height: " + (worldHeight + 64), 2, 80);
            graphics.drawString("playerpos_x: " + World.players[0].serverPosX, 2, 100);
            graphics.drawString("playerpos_y: " + World.players[0].serverPosY, 2, 120);
        }
        
        if(Settings.renderDebugLevel == 1 || Settings.renderDebugLevel == 3) {
            graphics.setFont(GuiHelper.thiccFont);
            graphics.setColor(Color.WHITE);
            
            //Right
            var currentMilis = System.currentTimeMillis();
            var renderDelay = currentMilis - renderLastUpdate;
            
            graphics.drawString("current map: " + World.mapName, worldWidth - 100, 20);
            graphics.drawString("render delay: " + renderDelay + " ms", worldWidth - 100, 40);
            graphics.drawString("fps: " + (1000 / renderDelay), worldWidth - 100, 60);
            
            renderLastUpdate = System.currentTimeMillis();
        }
    }
    
    private static void updateServer() {
        ++World.ticks;
        var ticks = World.ticks;
            
        for(var entity : World.players) entity.updateInternal(ticks);
            
        for(var monsters : World.enemies) {
            if(monsters.active) {
                monsters.updateInternal(ticks);
            }
        }
            
        for(var entities : World.entities) entities.updateInternal(ticks);
            
        if(ticks % 4 == 0) MapZoneWater.updateWaterUV();
            
        if(ticks % 2 == 0) {
            for(var iterator = World.particles.iterator(); iterator.hasNext();) {
                iterator.next().update(iterator);
            }
        }
            
        if(ticks % 20 == 0) {
            var xCoords = World.xCoords;
            var yCoords = World.yCoords;
            var materials = World.materials;
            var zones = World.zones;
                
            for(var k = 0; k < zones.length; ++k) {
                var zone = zones[k];
                var xCoord = xCoords[k];
                var yCoord = yCoords[k];
                    
                if(zone.hasParticleSpawns && World.isEmptyAt(xCoord, yCoord + 64) && Main.rand.nextInt(100) == 3) {
                    World.spawnFallingParticles(3 + Main.rand.nextInt(6), xCoord, yCoord, materials[k]);
                }
                
                if(zone instanceof IZoneEntityProvider) {
                    var entity = World.zoneEntities[k];
                        
                    if(entity.needsUpdates) {
                        entity.update(k, xCoord, yCoord);
                    }
                }
            }
        }
    }
    
    public static void showMessageAndClose(String message) {
        serverThread.shutdown();
        renderThread.shutdown();
        serverThread = null;
        renderThread = null;
        
        JOptionPane.showMessageDialog(null, message, "Frutty", JOptionPane.PLAIN_MESSAGE);
        GuiMenu.createMainFrame();
        ingameFrame.dispose();
        if(GuiStats.topScore < World.score) {
            GuiStats.topScore = World.score;
        }
        
        GuiStats.playTime += startTime.until(LocalTime.now(), ChronoUnit.MINUTES);
        GuiStats.saveStats();
        World.cleanUp();
    }
    
    public static void showSaveQuestion() {
        serverThread.shutdown();
        renderThread.shutdown();
        serverThread = null;
        renderThread = null;
        serverTask = null;
        
        if(JOptionPane.showConfirmDialog(null, "Save current status?", "Save?", JOptionPane.YES_NO_OPTION, 1) == 0) {
            World.createSave(JOptionPane.showInputDialog("Enter save name!"));
        }
    }
    
    public static void pause() {
        serverTask.cancel(true);
    }
    
    public static void unpause() {
        serverTask = serverThread.scheduleAtFixedRate(GuiIngame::updateServer, 0, 20, TimeUnit.MILLISECONDS);
    }
    
    public static void shutdown() {
        serverThread.shutdown();
        renderThread.shutdown();
        
        serverTask = null;
        serverThread = null;
        renderThread = null;
    }
    
    public static void showIngame() {
        EventQueue.invokeLater(() -> {
            ingameFrame = new JFrame("Frutty");
            ingamePanel = new GuiIngame();
            
            ingameFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            ingameFrame.setResizable(false);
            ingameFrame.setBounds(0, 0, World.width + 288, World.height + 100);
            ingameFrame.setLocationRelativeTo(null);
            ingameFrame.setContentPane(ingamePanel);
            ingameFrame.addKeyListener(ingamePanel);
            ingameFrame.setIconImage(GuiHelper.frameIcon);
            ingameFrame.setFocusable(true);
            
            startTime = LocalTime.now();
            renderLastUpdate = System.currentTimeMillis();
            serverThread = Executors.newSingleThreadScheduledExecutor(task -> new Thread(task, "Server Thread"));
            renderThread = Executors.newSingleThreadScheduledExecutor(task -> new Thread(task, "Render Thread"));
            renderThread.scheduleAtFixedRate(ingamePanel::repaint, 0, 1000 / Settings.fps, TimeUnit.MILLISECONDS);

            unpause();
            
            for(var players : World.players) {
                ingameFrame.addKeyListener(players);
            }
            ingameFrame.setVisible(true);
        });
    }
    
    @Override
    public void keyPressed(KeyEvent event) {
        var keyCode = event.getKeyCode();
        
        if(keyCode == KeyEvent.VK_ESCAPE) {
            pause();
            
            EventQueue.invokeLater(() -> {
                var returnFrame = new JFrame("Frutty");
                var menu = new GuiPauseMenu();
                returnFrame.setContentPane(menu.panel);
                returnFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                returnFrame.setResizable(false);
                returnFrame.setBounds(0, 0, 600, 540);
                returnFrame.setLocationRelativeTo(null);
                returnFrame.setFocusable(true);
                returnFrame.setIconImage(GuiHelper.frameIcon);
                returnFrame.addKeyListener(menu);
                returnFrame.addWindowListener(menu);
                returnFrame.setVisible(true);
            });
        }else if(keyCode == KeyEvent.VK_F12) {
            try {
                var window = ((JFrame)getTopLevelAncestor()).getLocationOnScreen();
                ImageIO.write(new Robot().createScreenCapture(new Rectangle(window.x + 7, window.y + 30, World.width + 64, World.height + 64)), Settings.screenshotFormat, 
                                                                            new File("./screenshots/" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_kk_HH_ss")) +"." + Settings.screenshotFormat.toLowerCase()));
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
    
    @Override public void keyTyped(KeyEvent e) {} @Override public void keyReleased(KeyEvent e) {}
}