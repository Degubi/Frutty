package frutty.tools.internal;

import java.lang.invoke.MethodHandle;

import frutty.plugin.FruttyEvent.EnumPriority;

public final class EventHandleObject {
	public final MethodHandle handle;
	public final int priority;
	
	public EventHandleObject(MethodHandle hand, int pri) {
		handle = hand;
		priority = pri;
	}
	
	public int getPriority() {
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