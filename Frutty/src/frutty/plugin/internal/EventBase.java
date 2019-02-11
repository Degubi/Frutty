package frutty.plugin.internal;

import java.lang.invoke.*;

public interface EventBase {
	
	default void invoke(MethodHandle handle) {
		try {
			handle.bindTo(this).invokeExact();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}