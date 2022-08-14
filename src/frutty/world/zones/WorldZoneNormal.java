package frutty.world.zones;

import frutty.tools.*;
import frutty.world.*;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

public final class WorldZoneNormal extends WorldZoneTexturable {

    public WorldZoneNormal() {
        super("normalZone");
    }

    @Override
    public void render(int x, int y, Material material, Graphics graphics) {
        graphics.drawImage(material.texture, x, y, 64, 64, null);
    }

    @Override
    protected ImageIcon getEditorIcon() {
        return new ImageIcon(GamePaths.TEXTURES_DIR + "world/normal.png");
    }

    @Override
    public BufferedImage getOverlayTexture() {
        return null;
    }

    @Override
    public ImageIcon[] getEditorTextures() {
        return new ImageIcon[] { Material.NORMAL.editorTexture.get(), Material.STONE.editorTexture.get(), Material.DIRT.editorTexture.get(), Material.BRICK.editorTexture.get() };
    }
}