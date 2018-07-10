package frutty.map.zones;

import java.awt.Graphics2D;

import javax.swing.ImageIcon;

import frutty.Main;
import frutty.gui.editor.GuiEditor;
import frutty.map.interfaces.IInternalZone;
import frutty.map.interfaces.MapZoneBase;

public final class MapZonePlayer extends MapZoneBase implements IInternalZone{
	private final int playerID;
	
	public MapZonePlayer(int id) {
		playerID = id;
	}
	
	@Override
	protected ImageIcon getEditorIcon() {
		return new ImageIcon("./textures/dev/player" + playerID + ".png");
	}

	@Override
	public void draw(int x, int y, int textureIndex, Graphics2D graphics) {}

	@Override
	public MapZoneBase getReplacementZone() {
		return Main.emptyZone;
	}

	@SuppressWarnings("boxing")
	@Override
	public void handleEditorPlacement(GuiEditor editorInstance, int buttonX, int buttonY) {
		if(playerID == 1) {
			editorInstance.mapProperties.table.setValueAt(buttonX, 4, 1);
			editorInstance.mapProperties.table.setValueAt(buttonY, 5, 1);
		}else{
			editorInstance.mapProperties.table.setValueAt(buttonX, 6, 1);
			editorInstance.mapProperties.table.setValueAt(buttonY, 7, 1);
		}
	}
}