package frutty;

import java.io.File;
import java.util.Random;

import frutty.gui.GuiMenu;
import frutty.gui.GuiSettings;
import frutty.gui.GuiStats;

public class Main {
	public static final Random rand = new Random();
	public static Thread loadThread; 
	
	public static void main(String[] args) {
		GuiMenu.showMenu();
		
		loadThread = new Thread(() -> {
			GuiSettings.loadSettings();
			GuiMenu.refreshMenu();
			
			//tryWait();
			GuiStats.loadStats();
			new File("./saves/").mkdir();
			GuiMenu.refreshMenu();
			
			//tryWait();
			loadClass("frutty.entity.EntityPlayer");
			GuiMenu.refreshMenu();
			
			//tryWait();
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
	
	private static void tryWait() {
		try {
			Thread.currentThread().sleep(3000);
		} catch (InterruptedException e) {}
	}
}