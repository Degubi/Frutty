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
	
	public static void main(String[] args){
		GuiMenu.showMenu();
		Settings.loadSettings();
		GuiStats.loadStats();
		new File("./saves/").mkdir();
	}
}