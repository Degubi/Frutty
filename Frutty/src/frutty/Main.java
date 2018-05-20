package frutty;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

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
	
	public static BufferedImage loadTexture(String prefix, String name) {
		try{
			return ImageIO.read(new File("./textures/" + prefix + "/" + name));
		}catch(IOException e){
			System.err.println("Can't find texture: " + prefix + "/" + name + ", returning null. Have fun :)");
			return null;
		}
	}
}