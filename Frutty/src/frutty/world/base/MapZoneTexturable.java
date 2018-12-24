package frutty.world.base;

import frutty.tools.*;
import java.awt.*;
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
		ImageIcon[] all = MapZoneBase.normalZone.textureVariants.get();
		ImageIcon[] toReturn = new ImageIcon[all.length];
		BufferedImage overlay = getOverlayTexture();
		
		for(int k = 0; k < all.length; ++k) {
			toReturn[k] = combineTextures(all[k], overlay);
		}
		return toReturn;
	}
	
	private static ImageIcon combineTextures(ImageIcon baseTexture, BufferedImage overlay) {
		BufferedImage toReturn = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
		Graphics graph = toReturn.createGraphics();
		graph.drawImage(baseTexture.getImage(), 0, 0, 64, 64, null);
		graph.drawImage(overlay, 0, 0, 64, 64, null);
		return new ImageIcon(toReturn);
	}
}