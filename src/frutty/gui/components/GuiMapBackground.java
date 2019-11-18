package frutty.gui.components;

import frutty.tools.*;
import frutty.world.base.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import javax.swing.*;

public abstract class GuiMapBackground extends JPanel {
	private final MapZoneBase[] zones = new MapZoneBase[140];
	private final int[] xCoords = new int[140], yCoords = new int[140];
	private final Material[] materials = new Material[140];
	
	public GuiMapBackground(String mapName) {
		try(var input = new ObjectInputStream(Files.newInputStream(Path.of(mapName)))){
			var zoneIDCache = (String[]) input.readObject();
			var textureCache = (String[]) input.readObject();
			
			input.readUTF(); //Sky texture
			input.readShort(); input.readShort();  //Width height felesleges, 14x10 az összes
			input.readUTF(); //Next map
			
			var materialRegistry = Material.materialRegistry;
			for(int y = 0, zoneIndex = 0; y < 640; y += 64) {
				for(int x = 0; x < 896; x += 64) {
					var zone = MapZoneBase.getZoneFromName(zoneIDCache[input.readByte()]);
					
					if(zone instanceof IInternalZone) {
						zone = ((IInternalZone) zone).getReplacementZone();
					}
					
					if(zone instanceof MapZoneTexturable) {
						materials[zoneIndex] = materialRegistry.get(textureCache[input.readByte()]);
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
		
		var zonesLocal = zones;
		var xCoordsLocal = xCoords;
		var yCoordsLocal = yCoords;
		var materialsLocal = materials;

		for(int k = 0; k < zonesLocal.length; ++k) {
			var zone = zonesLocal[k];
			
			zone.render(xCoordsLocal[k], yCoordsLocal[k], materialsLocal[k], graphics);
			if(zone instanceof ITransparentZone) {
				((ITransparentZone) zone).drawAfter(xCoordsLocal[k], yCoordsLocal[k], materialsLocal[k], graphics);
			}
		}
	}
}