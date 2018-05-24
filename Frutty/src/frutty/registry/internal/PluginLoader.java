package frutty.registry.internal;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import frutty.plugin.IFruttyPlugin;

public final class PluginLoader {
	private PluginLoader() {}
	
	public static void loadPlugins() {
		new File("./plugins/").mkdir();
		
		File[] all = new File("./plugins/").listFiles((dir, name) -> name.endsWith(".jar"));
		if(all.length > 0) {
			
			String[] mainClassNames = new String[all.length];
			URL[] classLoaderNames = new URL[all.length];
			
			for(int k = 0; k < all.length; ++k) {
				try {
					classLoaderNames[k] = all[k].toURI().toURL();
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				}
				
				try(JarFile jar = new JarFile(all[k])){
					System.out.println("KA");
					Manifest mani = jar.getManifest();
					if(mani == null) {
						System.err.println("Can't find manifest file from plugin: " + all[k]);
					}else{
						String pluginClass = mani.getMainAttributes().getValue("Plugin-Class");
						if(pluginClass == null) {
							System.err.println("Can't find \"Plugin-Class\" attribute from plugin: " + all[k]);
						}else {
							mainClassNames[k] = pluginClass;
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			try(URLClassLoader urlClass = new URLClassLoader(classLoaderNames)){
				for(int k = 0; k < mainClassNames.length; ++k) {
					if(mainClassNames[k] == null) {
						System.err.println("Can't load main class from plugin: " + all[k]);
					}else{
						Class<?> loaded = urlClass.loadClass(mainClassNames[k]);
						if(hasInterface(loaded)) {
							Method method = loaded.getMethod("register");
							method.invoke(loaded.getDeclaredConstructor().newInstance());
						}
					}
				}
			} catch (IOException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static boolean hasInterface(Class<?> theClass) {
		for(Class<?> faces : theClass.getInterfaces()) {
			if(IFruttyPlugin.class.isAssignableFrom(faces)){
				return true;
			}
		}
		return false;
	}
}