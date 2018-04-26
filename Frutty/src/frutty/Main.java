package frutty;

import java.io.File;
import java.util.Random;

import frutty.gui.GuiMenu;
import frutty.gui.GuiSettings;
import frutty.gui.GuiStats;

public class Main {
	public static final Random rand = new Random();
	
	public static void main(String[] args) {
		GuiMenu.showMenu();
		GuiSettings.loadSettings();
		GuiStats.loadStats();
		new File("./saves/").mkdir();
	}
}