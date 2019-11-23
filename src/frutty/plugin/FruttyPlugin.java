package frutty.plugin;

import java.lang.annotation.*;

/**Mark the class of the plugin with this annotation. Also need to set the "Plugin-Class" attribute in the Manifest file. See an example down below. <pre><code>
	//Loads of optional parameters for the FruttyPlugin annotation
	{@literal @}FruttyPlugin(name = "test", version = "1.0.0")
	public final class TestMain{
  		public static final MapZoneBase bedrockZone = new TestPluginZone();
	
		{@literal @}FruttyPluginMain(eventClass = TestMain.class)
		public static void pluginMain() {
			MapZoneBase.registerZone("test:bedrock", bedrockZone);
		}
	
		{@literal @}FruttyEvent
		public static void mapLoadEvent(MapInitEvent event) {
			System.out.println(event.mapHeight);
		
			event.mapTextureCache[0] = "stone";
			event.mapEntities.add(new EntityApple(0, 0));
		}
	}
</code> </pre>*/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FruttyPlugin {
	
	/**The name of the plugin*/
	String name();
	/**Current version of the plugin*/
	String version();
	/**Optional description for the plugin*/
	String description() default "";
	/**Optional URL only for display in the plugins menu*/
	String pluginSiteURL() default "";
}