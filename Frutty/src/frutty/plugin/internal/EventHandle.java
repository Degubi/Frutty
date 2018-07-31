package frutty.plugin.internal;

import java.lang.invoke.MethodHandle;
import java.util.ArrayList;
import java.util.Comparator;

import frutty.plugin.EnumPriority;

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
}