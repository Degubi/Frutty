package frutty.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FruttyPlugin {
	String id();
	String version();
	String versionURL() default "";
	String description() default "";
	String updateURL() default "";
}