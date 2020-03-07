package frutty.plugin.event.stats;

import frutty.*;
import frutty.tools.*;

@FruttyEventMarker
public final class StatsSavedEvent{
	public final PropertyFile statsFile;
	
	public StatsSavedEvent(PropertyFile stats) {
		statsFile = stats;
	}
}