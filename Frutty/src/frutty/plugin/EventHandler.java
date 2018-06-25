package frutty.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventHandler {
	EnumPriority priority() default EnumPriority.NORMAL;
	
	public static enum EnumPriority{
		LOW, NORMAL, HIGH;
		
		public static int ordinal(EnumPriority prop) {
			if(prop == EnumPriority.NORMAL) {
				return 1;
			}else if(prop == EnumPriority.HIGH) {
				return 2;
			}
			return 0;
		}
	}
}