package frutty.gui.editor;

import java.awt.Graphics;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JList;
import javax.swing.JPanel;

import frutty.Main;
import frutty.gui.GuiHelper;
import frutty.gui.editor.GuiEditor.ZoneButton;

public final class GuiInfo extends JPanel{
	private final GuiEditor editor;
	private final String textureCount, textureSize;
	@SuppressWarnings("rawtypes")
	private final JList textureList;
	
	public GuiInfo(GuiEditor edit) {
		setLayout(null);
		editor = edit;
		
		ArrayList<String> textures = new ArrayList<>();
		int size = 0;
		
 		for(ZoneButton writeButton : editor.zoneButtons) {
 			if(Main.hasTextureInfo(writeButton.zoneID)) {
 				String texture = "textures/map/" + writeButton.zoneTexture + ".png";
 				if(!textures.contains(texture)) {
 					size += new File("./" + texture).length();
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