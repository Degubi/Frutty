package frutty.plugin.event.gui;

import frutty.plugin.internal.EventBase;
import frutty.tools.PropertyFile;

public final class GuiStatSavedEvent extends EventBase{
	public final PropertyFile statsFile;
	
	public GuiStatSavedEvent(PropertyFile stats) {
		statsFile = stats;
	}
}