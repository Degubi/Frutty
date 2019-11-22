package frutty;

import frutty.gui.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import javax.swing.*;

public final class Main {
	public static final Random rand = new Random();
	
	private Main() {}
	
	public static void main(String[] args) throws Exception {
		createDirectory("plugins");
		Plugin.loadPlugins();
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		GuiMenu.createMainFrame();
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
}