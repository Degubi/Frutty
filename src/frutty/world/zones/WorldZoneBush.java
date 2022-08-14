package frutty.world.zones;

import frutty.tools.*;
import frutty.world.*;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

public final class WorldZoneBush extends WorldZone implements TransparentZone {
    public static final BufferedImage texture = Material.loadTexture("world/special", "bush.png");

    public WorldZoneBush() {
        super("bushZone", true, false);
    }

    @Override
    public void drawAfter(int x, int y, Material material, Graphics graphics) {
        graphics.drawImage(texture, x, y, 64, 64, null);
    }

    @Override
    public void render(int x, int y, Material material, Graphics graphics) {
        graphics.setColor(Color.BLACK);
        graphics.fillRect(x, y, 64, 64);
    }

    @Override
    protected ImageIcon getEditorIcon() {
        var toReturn = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
        var graphics = toReturn.createGraphics();
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0, 64, 64);
        graphics.drawImage(texture, 0, 0, 64, 64, null);
        graphics.dispose();
        return new ImageIcon(toReturn);
    }

    @Override
    public boolean canNPCPass(int x, int y) {
        return true;
    }

    @Override
    public boolean isBreakable(int x, int y) {
        return false;
    }

    @Override
    public boolean doesHidePlayer(int x, int y) {
        return true;
    }
}