package frutty;

import frutty.plugin.*;
import frutty.plugin.event.gui.*;
import frutty.plugin.event.world.*;
import java.lang.invoke.*;
import java.lang.invoke.MethodHandles.*;
import java.lang.reflect.*;
import java.util.*;

public final class EventHandle {
	public static final ArrayList<EventHandle> mapLoadEvents = new ArrayList<>(0);
	public static final ArrayList<EventHandle> menuInitEvents = new ArrayList<>(0);
	public static final ArrayList<EventHandle> statInitEvents = new ArrayList<>(0);
	public static final ArrayList<EventHandle> statSaveEvents = new ArrayList<>(0);
	public static final ArrayList<EventHandle> zoneAddedEvents = new ArrayList<>(0);
	
	public final MethodHandle handle;
	public final int priority;
	
	public EventHandle(MethodHandle hand, EnumPriority pri) {
		handle = hand;
		priority = pri.ordinal();
	}
	
	public static void handleEvent(Object event, ArrayList<EventHandle> methods) {
		for(var handles : methods) {
			try {
				handles.handle.bindTo(event).invokeExact();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void addEvent(Lookup lookup, Method eventMts, Class<?> eventTypeClass) {
		try {
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
		}catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void sortEvents() {
		var byPriority = Comparator.comparingInt((EventHandle event) -> event.priority);

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