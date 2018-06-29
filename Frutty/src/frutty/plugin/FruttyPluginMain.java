package frutty.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**Mark the main method of the plugin with this annotation. (The method must be static and must not have any parameters). See example down below.<pre><code>
	//Optional eventClass argument
	{@literal @}FruttyPluginMain(eventClass = TestMain.class)
	public static void pluginMain() {
		//e.g register shit here
	}</code> </pre>*/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FruttyPluginMain {
	
	/**The class object to read events from*/
	Class<?> eventClass() default void.class;
}