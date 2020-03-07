package frutty;

import frutty.plugin.event.*;
import java.lang.invoke.*;

public final class EventHandle {
    public final MethodHandle handle;
    public final int priority;
    
    public EventHandle(MethodHandle hand, EventPriority pri) {
        handle = hand;
        priority = pri.ordinal();
    }
}