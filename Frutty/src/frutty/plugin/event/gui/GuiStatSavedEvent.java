package frutty.plugin.event.gui;

import frutty.plugin.internal.*;
import frutty.tools.*;

public final class GuiStatSavedEvent implements EventBase{
	public final PropertyFile statsFile;
	
	public GuiStatSavedEvent(PropertyFile stats) {
		statsFile = stats;
	}
}