package frutty.plugin.event;

import java.lang.annotation.*;

/**Mark any methods with this annotation to register it as an event method. (Method must be static and must contain a class from the frutty.plugin.event* package. See them in the plugin.event package). See an example down below. <pre><code>
    {@literal @}FruttyEvent
    public static void mapLoadEvent(MapInitEvent event) {
        //Access parameters through the event argument
    }
</code> </pre>*/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FruttyEvent {
    
    /**Optional priority for the method, affects event execution ordering, defaults to EnumPriority.NORMAL*/
    EventPriority priority() default EventPriority.NORMAL;
}