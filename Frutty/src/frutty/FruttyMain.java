package frutty;

import frutty.gui.*;
import frutty.plugin.internal.*;
import frutty.tools.*;
import java.util.*;

public final class FruttyMain {
	public static final Random rand = new Random();
	
	private FruttyMain() {}
	
	public static void main(String[] args){
		Plugin.handlePluginInit();
		GuiMenu.createMainFrame(true);
		IOHelper.createDirectory("saves");
	}
	
	@SafeVarargs
	public static <T> List<T> toList(T... objs){
		List<T> list = new ArrayList<>(objs.length);
		
		for(T el : objs) {
			list.add(el);
		}
		return list;
	}
}