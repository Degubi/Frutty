package frutty;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import frutty.gui.GuiMenu;
import frutty.plugin.internal.Plugin;
import frutty.tools.IOHelper;

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
		ArrayList<T> list = new ArrayList<>(objs.length);
		
		for(T el : objs) {
			list.add(el);
		}
		return list;
	}
}