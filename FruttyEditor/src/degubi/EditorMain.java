package degubi;

import degubi.editor.GuiEditor;
import frutty.gui.GuiMenu;
import frutty.plugin.FruttyEvent;
import frutty.plugin.FruttyPlugin;
import frutty.plugin.FruttyPluginMain;
import frutty.plugin.event.gui.GuiMenuEvent;
import frutty.tools.GuiHelper;

@FruttyPlugin(name = "Frutty Editor", version = "1.0.0", description = "Editor Plugin")
public final class EditorMain{
	
	@FruttyPluginMain(eventClass = EditorMain.class)
	public static void pluginMain() {}

	@FruttyEvent
	public static void menuInitEvent(GuiMenuEvent event) {
		event.addButton(GuiHelper.newButton("Editor", 20, 475, e -> {GuiEditor.openEmptyEditor(); GuiMenu.mainFrame.dispose();}));
	}
}