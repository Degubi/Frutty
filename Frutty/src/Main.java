import java.io.File;

import frutty.gui.GuiMenu;
import frutty.gui.GuiSettings;
import frutty.gui.GuiStats;

public class Main {
	/**
	 * Fõ program entry pont, Menü JFrame-nek láthatóvá tétele, mentések mappa létrehozása, statok betöltése
	 */
	public static void main(String[] args) {
		GuiMenu.showMenu();
		GuiSettings.loadSettings();
		GuiStats.loadStats();
		new File("./saves/").mkdir();
	}
}