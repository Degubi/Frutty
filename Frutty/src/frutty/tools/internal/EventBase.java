package frutty.tools.internal;

import java.lang.invoke.MethodHandle;

public abstract class EventBase {
	public final void invoke(MethodHandle handle) {
		try {
			handle.invokeWithArguments(this);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}