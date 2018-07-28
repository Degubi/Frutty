package frutty.plugin.internal;

import java.lang.invoke.MethodHandle;
import java.util.Comparator;

import frutty.plugin.EnumPriority;

public final class EventHandleObject {
	public static final Comparator<EventHandleObject> byPriority = Comparator.comparingInt(event -> event.priority);

	public final MethodHandle handle;
	public final int priority;
	
	public EventHandleObject(MethodHandle hand, EnumPriority pri) {
		handle = hand;
		priority = pri.ordinal();
	}
}