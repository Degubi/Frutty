package frutty;

import java.io.File;
import java.util.Random;

import frutty.entity.EntityEnemy;
import frutty.entity.EntityPlayer;
import frutty.gui.GuiMenu;
import frutty.gui.GuiSettings.Settings;
import frutty.gui.GuiStats;

public final class Main {
	public static final Random rand = new Random();
	public static Thread loadThread; 
	
	public static void main(String[] args){
		GuiMenu.showMenu();
		
		loadThread = new Thread(() -> {
			Settings.loadSettings();
			GuiStats.loadStats();
			new File("./saves/").mkdir();
			
			try {
				Class.forName(EntityPlayer.class.getName());
				Class.forName(EntityEnemy.class.getName());
			} catch (ClassNotFoundException e) {}
			
		}, "Main Initializer Thread");
		loadThread.start();
	}
}