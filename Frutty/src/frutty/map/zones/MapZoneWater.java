package frutty.map.zones;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import frutty.Main;
import frutty.gui.GuiHelper;
import frutty.gui.Settings;
import frutty.map.MapZoneBase;
import frutty.map.interfaces.ITransparentZone;

public final class MapZoneWater extends MapZoneBase implements ITransparentZone{
	public static final BufferedImage waterTexture = Main.loadTexture("map/special", "water.png");
	private static boolean decrease = false;
	private static int textureY = 0;
	
	public MapZoneWater() {
		super(false);
	}
	
	@Override
	public void draw(int x, int y, int textureIndex, Graphics graphics) {
		graphics.setColor(Color.BLACK);
		graphics.fillRect(x, y, 64, 64);
	}
	
	@Override
	public void drawAfter(int x, int y, int textureIndex, Graphics graphics) {
		graphics.drawImage(waterTexture, x, y, x + 64, y + 64, 0, textureY, 16, textureY + 16, null);
		
		if(Settings.graphicsLevel > 0) {
			graphics.setColor(GuiHelper.color_84Black);
			
			int till = y / 120;
			for(int k = 0; k < till && k < 4; ++k) {
				graphics.fillRect(x, y, 64, 64);
			}
		}
	}
	
	public static void updateWaterUV() {
		if(decrease) {
			textureY -= 16;
		}else{
			textureY += 16;
		}
		
		if(textureY == 0) {
			decrease = false;
		}
		if(textureY == 448) {
			decrease = true;
		}
	}

	@Override
	protected ImageIcon getEditorIcon() {
		var returnTexture = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
		var graphics = returnTexture.getGraphics();
		graphics.setColor(Color.BLACK);
		graphics.fillRect(0, 0, 64, 64);
		graphics.drawImage(MapZoneWater.waterTexture, 0, 0, 64, 64, 0, 0, 16, 16, null);
		return new ImageIcon(returnTexture);
	}
}