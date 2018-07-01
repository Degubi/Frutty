package frutty.tools.internal;

import java.lang.invoke.MethodHandle;
import java.util.Comparator;

import frutty.plugin.FruttyEvent.EnumPriority;

public final class EventHandleObject {
	public final MethodHandle handle;
	public final int priority;
	
	public static final Comparator<EventHandleObject> PRIORITY_COMPARATOR = Comparator.comparingInt(EventHandleObject::getPriority);
	
	public EventHandleObject(MethodHandle hand, int pri) {
		handle = hand;
		priority = pri;
	}
	
	private int getPriority() {
		return priority;
	}
	
	public static int ordinal(EnumPriority prop) {
		if(prop == EnumPriority.NORMAL) {
			return 1;
		}else if(prop == EnumPriority.HIGH) {
			return 2;
		}
		return 0;
	}
}