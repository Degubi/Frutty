package frutty.plugin.event.gui;

import frutty.plugin.internal.*;
import frutty.tools.*;

@FruttyEvent
public final class GuiStatSavedEvent{
	public final PropertyFile statsFile;
	
	public GuiStatSavedEvent(PropertyFile stats) {
		statsFile = stats;
	}
}