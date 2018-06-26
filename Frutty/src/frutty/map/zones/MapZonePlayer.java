package frutty.map.zones;

import java.awt.Graphics;

import javax.swing.ImageIcon;

import frutty.Main;
import frutty.gui.editor.GuiEditor;
import frutty.map.MapZoneBase;
import frutty.map.interfaces.IInternalZone;

public final class MapZonePlayer extends MapZoneBase implements IInternalZone{
	private final int playerID;
	
	public MapZonePlayer(int id) {
		super(false);
		playerID = id;
	}
	
	@Override
	protected ImageIcon getEditorIcon() {
		return new ImageIcon("./textures/dev/player" + playerID + ".png");
	}

	@Override
	public void draw(int x, int y, int textureIndex, Graphics graphics) {}

	@Override
	public MapZoneBase getReplacementZone() {
		return Main.emptyZone;
	}

	@Override
	public void handleEditorPlacement(GuiEditor editorInstance, int buttonX, int buttonY) {
		if(playerID == 1) {
			editorInstance.mapProperties.setPlayer1Pos(buttonX, buttonY);
		}else{
			editorInstance.mapProperties.setPlayer2Pos(buttonX, buttonY);
		}
	}
}