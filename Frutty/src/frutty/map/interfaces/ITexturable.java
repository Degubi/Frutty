package frutty.map.interfaces;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

public interface ITexturable {
	ImageIcon[] getEditorTextureVars();
	
	default ImageIcon combineTextures(ImageIcon normalTexture, BufferedImage overlay) {
		BufferedImage toReturn = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
		Graphics graph = toReturn.createGraphics();
		graph.drawImage(normalTexture.getImage(), 0, 0, 64, 64, null);
		graph.drawImage(overlay, 0, 0, 64, 64, null);
		return new ImageIcon(toReturn);
	}
}