package frutty;

import java.io.File;
import java.util.Random;

import frutty.gui.GuiMenu;
import frutty.gui.GuiSettings.Settings;
import frutty.gui.GuiStats;

public class Main {
	public static final Random rand = new Random();
	public static Thread loadThread; 
	
	public static void main(String[] args) {
		GuiMenu.showMenu();
		
		loadThread = new Thread(() -> {
			Settings.loadSettings();
			GuiMenu.refreshMenu();
			
			GuiStats.loadStats();
			new File("./saves/").mkdir();
			GuiMenu.refreshMenu();
			
			loadClass("frutty.entity.EntityPlayer");
			GuiMenu.refreshMenu();
			
			loadClass("frutty.entity.EntityEnemy");
			GuiMenu.refreshMenu();
		}, "Main Initializer Thread");
		loadThread.start();
	}
	
	private static void loadClass(String cName) {
		try { 
			Class.forName(cName);
		} catch (ClassNotFoundException e) {}
	}
}