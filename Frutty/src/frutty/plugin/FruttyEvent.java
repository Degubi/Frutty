package frutty.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**Mark any methods with this annotation to register it as an event method. (Method must be static and must contain a subclass of EventBase class. See them in the plugin.event package). See an example down below. <pre><code>
	{@literal @}FruttyEvent
	public static void mapLoadEvent(MapInitEvent event) {
		//Access parameters through the event argument
	}
</code> </pre>*/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FruttyEvent {
	
	/**Optional priority for the method, affects event execution ordering, defaults to EnumPriority.NORMAL*/
	EnumPriority priority() default EnumPriority.NORMAL;
}