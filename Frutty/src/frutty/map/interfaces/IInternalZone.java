package frutty.map.interfaces;

import frutty.gui.editor.GuiEditor;
import frutty.map.MapZoneBase;

public interface IInternalZone {
	MapZoneBase getReplacementZone();
	void handleEditorPlacement(GuiEditor editorInstance, int buttonX, int buttonY);
}