package frutty.plugin.internal;

import frutty.plugin.*;
import frutty.plugin.event.gui.*;
import frutty.plugin.event.world.*;
import java.lang.invoke.*;
import java.lang.invoke.MethodHandles.*;
import java.lang.reflect.*;
import java.util.*;

public final class EventHandle {
	public static final Comparator<EventHandle> byPriority = Comparator.comparingInt(event -> event.priority);
	
	public final MethodHandle handle;
	public final int priority;
	
	public EventHandle(MethodHandle hand, EnumPriority pri) {
		handle = hand;
		priority = pri.ordinal();
	}
	
	public static void handleEvent(EventBase event, ArrayList<EventHandle> methods) {
		for(EventHandle handles : methods) event.invoke(handles.handle);
	}
	
	public static final ArrayList<EventHandle> mapLoadEvents = new ArrayList<>(0);
	public static final ArrayList<EventHandle> menuInitEvents = new ArrayList<>(0);
	public static final ArrayList<EventHandle> statInitEvents = new ArrayList<>(0);
	public static final ArrayList<EventHandle> statSaveEvents = new ArrayList<>(0);
	public static final ArrayList<EventHandle> zoneAddedEvents = new ArrayList<>(0);
	
	public static void handleEventTypes(Lookup lookup, Method eventMts, Class<?> eventTypeClass) throws IllegalAccessException {
		var handle = new EventHandle(lookup.unreflect(eventMts), eventMts.getAnnotation(FruttyEvent.class).priority());
		
		if(eventTypeClass == WorldInitEvent.class) {
			mapLoadEvents.add(handle);
		}else if(eventTypeClass == GuiMenuEvent.class) {
			menuInitEvents.add(handle);
		}else if(eventTypeClass == GuiStatInitEvent.class) {
			statInitEvents.add(handle);
		}else if(eventTypeClass == GuiStatSavedEvent.class) {
			statSaveEvents.add(handle);
		}else if(eventTypeClass == ZoneAddedEvent.class) {
			zoneAddedEvents.add(handle);
		}
	}
	
	static void sortEvents() {
		if(!mapLoadEvents.isEmpty()) {
			mapLoadEvents.trimToSize();
			mapLoadEvents.sort(byPriority);
		}
		
		if(!menuInitEvents.isEmpty()) {
			menuInitEvents.trimToSize();
			menuInitEvents.sort(byPriority);
		}
		
		if(!statInitEvents.isEmpty()) {
			statInitEvents.trimToSize();
			statInitEvents.sort(byPriority);
		}
		
		if(!statSaveEvents.isEmpty()) {
			statSaveEvents.trimToSize();
			statSaveEvents.sort(byPriority);
		}
		
		if(!zoneAddedEvents.isEmpty()) {
			zoneAddedEvents.trimToSize();
			zoneAddedEvents.sort(byPriority);
		}
	}
}