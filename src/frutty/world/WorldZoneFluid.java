package frutty.world;

import frutty.gui.*;
import frutty.gui.GuiSettings.*;
import frutty.tools.*;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

public abstract class WorldZoneFluid extends WorldZone implements TransparentZone {
    private final BufferedImage texture;

    public WorldZoneFluid(String zoneName, BufferedImage texture) {
        super(zoneName, false, false);

        this.texture = texture;
    }

    @Override
    public void render(int x, int y, Material material, Graphics graphics) {
        graphics.setColor(Color.BLACK);
        graphics.fillRect(x, y, 64, 64);
    }

    @Override
    public void drawAfter(int x, int y, Material material, Graphics graphics) {
        var textureY = GuiIngame.animatedTextureY;
        graphics.drawImage(texture, x, y, x + 64, y + 64, 0, textureY, 16, textureY + 16, null);

        if(Settings.graphicsLevel > 0) {
            graphics.setColor(GuiHelper.color_84Black);

            var till = y / 120;
            for(var k = 0; k < till && k < 4; ++k) {
                graphics.fillRect(x, y, 64, 64);
            }
        }
    }

    @Override
    public boolean canPlayerPass(int x, int y) {
        return true;
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
    protected ImageIcon getEditorIcon() {
        var returnTexture = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
        var graphics = returnTexture.createGraphics();
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0, 64, 64);
        graphics.drawImage(this.texture, 0, 0, 64, 64, 0, 0, 16, 16, null);
        graphics.dispose();
        return new ImageIcon(returnTexture);
    }
}