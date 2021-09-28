package frutty.gui;

import frutty.*;
import frutty.gui.GuiSettings.*;
import frutty.plugin.event.gui.*;
import frutty.tools.*;
import frutty.world.*;
import frutty.world.base.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.*;
import java.time.format.*;
import java.time.temporal.*;
import java.util.concurrent.*;
import javax.imageio.*;
import javax.swing.*;

public final class GuiIngame extends JPanel implements KeyListener {
    private static ScheduledExecutorService serverThread;
    private static ScheduledExecutorService renderThread;
    private static ScheduledFuture<?> serverTask;

    static JFrame ingameFrame;
    private static LocalTime startTime;
    private static long renderLastUpdate;

    private static boolean animatedTextureSwitch = false;
    public static int animatedTextureY = 0;

    private static final int VIEWPORT_WIDTH = 960;
    private static final int VIEWPORT_HEIGHT = 704;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        var graphics = (Graphics2D) g;
        var oldTransform = graphics.getTransform();
        var player = World.players[0];

        var camX = player.serverPosX - (VIEWPORT_WIDTH / 2);
        camX = Math.min(camX, World.width - VIEWPORT_WIDTH);
        camX = Math.max(camX, 0);

        var camY = player.serverPosY - (VIEWPORT_HEIGHT / 2);
        camY = Math.min(camY, World.height - VIEWPORT_HEIGHT);
        camY = Math.max(camY, 0);

        graphics.translate(-camX, -camY);

        var zones = World.zones;
        var xCoords = World.xCoords;
        var yCoords = World.yCoords;
        var materials = World.materials;

        if(Settings.enablePathfindingDebug) {
            var isActivePathfindingZone = World.isActivePathfindingZone;

            for(var k = 0; k < zones.length; ++k) zones[k].renderPathfinding(xCoords[k], yCoords[k], materials[k], graphics, isActivePathfindingZone[k]);
        }else if(Settings.renderDebugLevel < 2) {
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

        if(Settings.enableMapDebug) {
            graphics.setTransform(oldTransform);
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
            graphics.setTransform(oldTransform);
            graphics.setFont(GuiHelper.thiccFont);
            graphics.setColor(Color.WHITE);

            //Right
            var currentMilis = System.currentTimeMillis();
            var renderDelay = currentMilis - renderLastUpdate;
            var rightSideDebugPosition = ingameFrame.getWidth() - 390;

            graphics.drawString("current map: " + World.mapName, rightSideDebugPosition, 20);
            graphics.drawString("render delay: " + renderDelay + " ms", rightSideDebugPosition, 40);
            graphics.drawString("fps: " + (1000 / renderDelay), rightSideDebugPosition, 60);

            renderLastUpdate = System.currentTimeMillis();
        }

        if(Main.screenOverlayEvents.length > 0) Main.invokeEvent(new ScreenOverlayEvent(graphics, ingameFrame), Main.screenOverlayEvents);
    }

    private static void updateServer() {
        var ticks = World.ticks;

        for(var entity : World.players) entity.updateInternal(ticks);

        for(var monsters : World.enemies) {
            if(monsters.active) {
                monsters.updateInternal(ticks);
            }
        }

        for(var entities : World.entities) entities.updateInternal(ticks);

        if(ticks % 4 == 0) {
            if(animatedTextureSwitch) {
                animatedTextureY -= 16;
            }else{
                animatedTextureY += 16;
            }

            if(animatedTextureY == 0) {
                animatedTextureSwitch = false;
            }
            if(animatedTextureY == 448) {
                animatedTextureSwitch = true;
            }
        }

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

        ++World.ticks;
    }

    public static void showMessageAndClose(String message) {
        shutdown();

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
        shutdown();

        if(JOptionPane.showConfirmDialog(null, "Save current status?", "Save?", JOptionPane.YES_NO_OPTION, 1) == 0) {
            World.createSave(JOptionPane.showInputDialog("Enter save name!"));
        }
    }

    public static void pause() {
        System.out.println(Main.updateSystemLabel + "Pausing");
        serverTask.cancel(true);
    }

    public static void unpause() {
        System.out.println(Main.updateSystemLabel + "Unpausing");
        serverTask = serverThread.scheduleAtFixedRate(GuiIngame::updateServer, 0, 20, TimeUnit.MILLISECONDS);
    }

    public static void shutdown() {
        System.out.println(Main.updateSystemLabel + "Shutting down");
        System.out.println(Main.renderSystemLabel + "Shutting down");

        serverThread.shutdown();
        renderThread.shutdown();

        serverTask = null;
        serverThread = null;
        renderThread = null;
    }

    public static void showIngame() {
        System.out.println(Main.guiSystemLabel + "Switching to ingame frame");
        startTime = LocalTime.now();

        var ingamePanel = new GuiIngame();
        ingameFrame = new JFrame("Frutty");
        ingameFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        ingameFrame.setResizable(false);
        ingameFrame.setBounds(0, 0, Math.min(VIEWPORT_WIDTH, World.width) + 80, Math.min(VIEWPORT_HEIGHT, World.height) + 103);
        ingameFrame.setLocationRelativeTo(null);
        ingameFrame.setContentPane(ingamePanel);
        ingameFrame.addKeyListener(ingamePanel);
        ingameFrame.setIconImage(GuiHelper.frameIcon);
        ingameFrame.setFocusable(true);

        System.out.println(Main.updateSystemLabel + "Starting");
        System.out.println(Main.renderSystemLabel + "Starting");

        renderLastUpdate = System.currentTimeMillis();
        serverThread = Executors.newSingleThreadScheduledExecutor(task -> new Thread(task, "Server Thread"));
        renderThread = Executors.newSingleThreadScheduledExecutor(task -> new Thread(task, "Render Thread"));
        renderThread.scheduleAtFixedRate(ingamePanel::repaint, 0, 1000 / Settings.fps, TimeUnit.MILLISECONDS);

        unpause();

        for(var players : World.players) {
            ingameFrame.addKeyListener(players);
        }
        ingameFrame.setVisible(true);
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
                var window = ((JFrame) getTopLevelAncestor()).getLocationOnScreen();
                var outputFile = new File("./screenshots/" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_kk_HH_ss")) +"." + Settings.screenshotFormat.toLowerCase());
                var screenshotArea = new Rectangle(window.x + 7, window.y + 30, World.width + 64, World.height + 64);

                ImageIO.write(new Robot().createScreenCapture(screenshotArea), Settings.screenshotFormat, outputFile);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override public void keyTyped(KeyEvent e) {} @Override public void keyReleased(KeyEvent e) {}
}