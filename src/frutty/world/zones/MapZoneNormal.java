package frutty.world.zones;

import frutty.tools.*;
import frutty.world.*;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

public final class MapZoneNormal extends MapZoneTexturable {

    public MapZoneNormal() {
        super("normalZone");
    }

    @Override
    public void render(int x, int y, Material material, Graphics graphics) {
        graphics.drawImage(material.texture, x, y, 64, 64, null);
    }

    @Override
    protected ImageIcon getEditorIcon() {
        return new ImageIcon("./textures/map/normal.png");
    }

    @Override
    public BufferedImage getOverlayTexture() {
        return null;
    }

    @Override
    public ImageIcon[] getEditorTextures() {
        return new ImageIcon[] {Material.NORMAL.editorTexture.get(), Material.STONE.editorTexture.get(), Material.DIRT.editorTexture.get(), Material.BRICK.editorTexture.get()};
    }
}