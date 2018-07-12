package frutty.gui.editor;

import java.awt.Graphics;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.JList;
import javax.swing.JPanel;

import frutty.Main;
import frutty.gui.GuiHelper;
import frutty.world.interfaces.ITexturable;

public final class GuiInfo extends JPanel{
	private final GuiEditor editor;
	private final String textureCount, textureSize;
	@SuppressWarnings("rawtypes")
	private final JList textureList;
	
	public GuiInfo(GuiEditor edit) {
		setLayout(null);
		editor = edit;
		
		var textures = new ArrayList<String>();
		int size = 0;
		
 		for(var writeButton : editor.zoneButtons) {
 			if(Main.getZoneFromName(writeButton.zoneID) instanceof ITexturable) {
 				String texture = "textures/map/" + writeButton.zoneTexture + ".png";
 				if(!textures.contains(texture)) {
 					try {
						size += Files.size(Paths.get("./" + texture));
					} catch (IOException e) {}
 					textures.add(texture);
 				}
 			}
 		}
 		textureSize = "Texture size: " + size + " bytes";
 		textureCount = "Texture Count: " + textures.size();
 		
 		textureList = new JList<>(textures.toArray());
		textureList.setBounds(60, 150, 200, 120);
		textureList.setBorder(GuiHelper.menuBorder);
 		
 		add(textureList);
	}
	
	@Override
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		
		graphics.setFont(GuiHelper.thiccFont);
		graphics.drawString(textureCount, 20, 20);
		graphics.drawString(textureSize, 20, 40);
		graphics.drawString("Textures Used:", 20, 145);
	}
}