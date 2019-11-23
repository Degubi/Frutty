package frutty.world.zones;

import frutty.gui.GuiSettings.*;
import frutty.tools.*;
import frutty.world.base.*;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

public final class MapZoneWater extends MapZoneBase implements ITransparentZone{
	public static final BufferedImage waterTexture = Material.loadTexture("map/special", "water.png");
	private static boolean decrease = false;
	private static int textureY = 0;
	
	public MapZoneWater() {
		super("waterZone", false, false);
	}
	
	@Override
	public void render(int x, int y, Material material, Graphics graphics) {
		graphics.setColor(Color.BLACK);
		graphics.fillRect(x, y, 64, 64);
	}
	
	@Override
	public void drawAfter(int x, int y, Material material, Graphics graphics) {
		graphics.drawImage(waterTexture, x, y, x + 64, y + 64, 0, textureY, 16, textureY + 16, null);
		
		if(Settings.graphicsLevel > 0) {
			graphics.setColor(GuiHelper.color_84Black);
			
			int till = y / 120;
			for(int k = 0; k < till && k < 4; ++k) {
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
		var graphics = returnTexture.createGraphics();
		graphics.setColor(Color.BLACK);
		graphics.fillRect(0, 0, 64, 64);
		graphics.drawImage(MapZoneWater.waterTexture, 0, 0, 64, 64, 0, 0, 16, 16, null);
		graphics.dispose();
		return new ImageIcon(returnTexture);
	}
}