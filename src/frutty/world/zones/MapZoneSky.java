package frutty.world.zones;

import frutty.tools.*;
import frutty.world.base.*;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

public final class MapZoneSky extends MapZoneBase{
    private static BufferedImage skyTexture;

    public MapZoneSky() {
        super("skyZone", false, false);
    }

    @Override
    public void render(int x, int y, Material material, Graphics graphics) {
        graphics.drawImage(skyTexture, x, y, x + 64, y + 64, x, y, x + 64, y + 64, null);
    }
    
    @Override
    public boolean canPlayerPass(int x, int y) {
        return false;
    }
    
    public static void loadSkyTexture(String textureName) {
        skyTexture = !textureName.equals("null") ? Material.loadTexture("map/skybox", textureName + ".png") : null;
    }
    
    @Override
    protected ImageIcon getEditorIcon() {
        return new ImageIcon("./textures/dev/sky.png");
    }
}