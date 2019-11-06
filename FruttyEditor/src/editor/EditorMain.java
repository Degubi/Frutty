package editor;

import editor.gui.*;
import frutty.plugin.*;
import frutty.plugin.event.gui.*;
import frutty.tools.*;

@FruttyPlugin(name = "Frutty Editor", version = "1.0.0", description = "Editor Plugin")
public final class EditorMain{
	
	@FruttyMain(eventClass = EditorMain.class)
	public static void pluginMain() {}

	@FruttyEvent
	public static void menuInitEvent(GuiMenuEvent event) {
		event.addButton(GuiHelper.newButton("Editor", 20, 475, e -> GuiEditor.openEmptyEditor()));
	}
}