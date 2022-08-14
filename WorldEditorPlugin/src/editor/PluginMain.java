package editor;

import editor.gui.*;
import frutty.plugin.*;
import frutty.plugin.event.*;
import frutty.plugin.event.gui.*;
import frutty.tools.*;

@FruttyPlugin(name = "Frutty World Editor", version = "1.0.0", description = "World Editor Plugin", pluginSiteURL = "https://github.com/Degubi/Frutty/tree/master/WorldEditorPlugin")
public final class PluginMain {

    @FruttyMain(eventClass = PluginMain.class)
    public static void pluginMain() {}

    @FruttyEvent
    public static void menuInitEvent(GuiMenuEvent event) {
        event.addNewComponent(GuiHelper.newButton("World Editor", 20, 475, e -> GuiEditor.openEmptyEditor()));
    }
}