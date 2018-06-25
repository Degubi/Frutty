package frutty.tools.internal;

import java.lang.invoke.MethodHandle;
import java.util.Comparator;

public final class EventHandleObject {
	public final MethodHandle handle;
	public final int priority;
	
	public static final Comparator<EventHandleObject> PRIORITY_COMPARATOR = Comparator.comparingInt(event -> event.priority);
	
	public EventHandleObject(MethodHandle hand, int pri) {
		handle = hand;
		priority = pri;
	}
}