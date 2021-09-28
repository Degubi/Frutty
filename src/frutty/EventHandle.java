package frutty;

import frutty.plugin.event.*;
import java.lang.invoke.*;
import java.lang.invoke.MethodHandles.*;
import java.lang.reflect.*;

public final class EventHandle {
    public final MethodHandle handle;
    public final int priority;

    public EventHandle(Lookup lookup, Method eventMethod) {
        this.handle = unreflectEventMethod(lookup, eventMethod);
        this.priority = eventMethod.getAnnotation(FruttyEvent.class).priority().ordinal();
    }

    private static MethodHandle unreflectEventMethod(Lookup lookup, Method eventMethod) {
        try {
            return lookup.unreflect(eventMethod);
        } catch (IllegalAccessException e) {
            //TODO: Don't throw here
            throw new IllegalStateException("Unable to access event method: " + eventMethod.getName() + " in class: " + eventMethod.getDeclaringClass());
        }
    }
}