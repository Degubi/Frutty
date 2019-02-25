package frutty.plugin.event.gui;

import frutty.*;
import frutty.tools.*;

@FruttyEventMarker
public final class GuiStatSavedEvent{
	public final PropertyFile statsFile;
	
	public GuiStatSavedEvent(PropertyFile stats) {
		statsFile = stats;
	}
}