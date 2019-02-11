package frutty;

import frutty.gui.*;
import frutty.plugin.internal.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class FruttyMain {
	public static final Random rand = new Random();
	
	private FruttyMain() {}
	
	public static void main(String[] args){
		FruttyMain.createDirectory("plugins");
		Plugin.loadPlugins();
		EventHandle.sortEvents();
		GuiMenu.createMainFrame(true);
		createDirectory("saves");
	}
	
	public static void createDirectory(String path) {
		var filePath = Path.of(path);
		
		if(!Files.exists(filePath)) {
			try {
				Files.createDirectory(filePath);
			} catch (IOException e) {}
		}
	}
	
	@SafeVarargs
	public static <T> List<T> toList(T... objs){
		var list = new ArrayList<T>(objs.length);
		
		for(T el : objs) {
			list.add(el);
		}
		return list;
	}
}