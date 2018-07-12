package frutty.world.interfaces;

import frutty.gui.editor.GuiEditor;

public interface IInternalZone {
	MapZoneBase getReplacementZone();
	void handleEditorPlacement(GuiEditor editorInstance, int buttonX, int buttonY);
}