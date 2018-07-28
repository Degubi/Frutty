package frutty.plugin.internal;

import java.lang.invoke.MethodHandle;

public abstract class EventBase {
	public final void invoke(MethodHandle handle) {
		try {
			handle.bindTo(this).invokeExact();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}