package frutty.world.base;

import frutty.tools.*;
import java.awt.image.*;
import javax.swing.*;

public abstract class MapZoneTexturable extends MapZoneBase{
    public transient final Lazy<ImageIcon[]> textureVariants = new Lazy<>(this::getEditorTextures);
    
    public MapZoneTexturable(String name) {
        super(name);
    }
    
    public MapZoneTexturable(String name, boolean hasDarkening, boolean enableParticles) {
        super(name, hasDarkening, enableParticles);
    }
    
    public abstract BufferedImage getOverlayTexture();
    
    public ImageIcon[] getEditorTextures() {
        var all = MapZoneBase.normalZone.textureVariants.get();
        var toReturn = new ImageIcon[all.length];
        var overlay = getOverlayTexture();
        
        for(int k = 0; k < all.length; ++k) {
            toReturn[k] = combineTextures(all[k], overlay);
        }
        return toReturn;
    }
    
    private static ImageIcon combineTextures(ImageIcon baseTexture, BufferedImage overlay) {
        var toReturn = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
        var graph = toReturn.createGraphics();
        graph.drawImage(baseTexture.getImage(), 0, 0, 64, 64, null);
        graph.drawImage(overlay, 0, 0, 64, 64, null);
        graph.dispose();
        return new ImageIcon(toReturn);
    }
}