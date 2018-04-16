import java.io.File;

import frutty.gui.GuiMenu;
import frutty.gui.GuiSettings;
import frutty.gui.GuiStats;

public class Main {
	/**
	 * F� program entry pont, Men� JFrame-nek l�that�v� t�tele, ment�sek mappa l�trehoz�sa, statok bet�lt�se
	 */
	public static void main(String[] args) {
		GuiMenu.showMenu();
		GuiSettings.loadSettings();
		GuiStats.loadStats();
		new File("./saves/").mkdir();
	}
}