package frutty.gui.components;

import frutty.tools.*;
import frutty.world.base.*;
import java.awt.*;
import java.io.*;
import javax.swing.*;

public abstract class GuiMapBackground extends JPanel {
	private final MapZoneBase[] zones = new MapZoneBase[140];
	private final int[] xCoords = new int[140], yCoords = new int[140];
	private final Material[] materials = new Material[140];
	
	public GuiMapBackground(String mapName) {
		try(var input = IOHelper.newObjectIS(mapName)){
			String[] zoneIDCache = (String[]) input.readObject();
			String[] textureCache = (String[]) input.readObject();
			
			input.readUTF(); //Sky texture
			input.readShort(); input.readShort();  //Width height felesleges, 14x10 az összes
			input.readUTF(); //Next map
			
			for(int y = 0, zoneIndex = 0; y < 640; y += 64) {
				for(int x = 0; x < 896; x += 64) {
					MapZoneBase zone = MapZoneBase.getZoneFromName(zoneIDCache[input.readByte()]);
					
					if(zone instanceof IInternalZone) {
						zone = ((IInternalZone) zone).getReplacementZone();
					}
					
					if(zone instanceof MapZoneTexturable) {
						materials[zoneIndex] = Material.materialRegistry.get(textureCache[input.readByte()]);
					}
					xCoords[zoneIndex] = x;
					yCoords[zoneIndex] = y;
					zones[zoneIndex++] = zone;
				}
			}
		}catch(IOException | ClassNotFoundException e){}
	}
	
	@Override
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		
		for(int k = 0; k < zones.length; ++k) {
			MapZoneBase zone = zones[k];
			zone.render(xCoords[k], yCoords[k], materials[k], (Graphics2D) graphics);
			if(zone instanceof ITransparentZone) {
				((ITransparentZone) zone).drawAfter(xCoords[k], yCoords[k], materials[k], graphics);
			}
		}
	}
}