package frutty.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Static helper class, Inicializál 2 font objectet, illetve gomb és jframe kezelésre ad függvényeket
 */
public class GuiHelper {
	public static final Font ingameFont = new Font("TimesRoman", Font.PLAIN, 22);
	public static final Font thiccFont = new Font("TimeRoman", Font.BOLD, 12);
	
	public static final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
	public static final int recommendedMaxMapHeight = screen.height / 64 - 1;
	public static final int recommendedMaxMapWidth = screen.width / 64 - 1;
	public static final String recommendedMapSizeString = "Recommended max map size: " + recommendedMaxMapWidth + "x" + recommendedMaxMapHeight;

	
	/**
	 * Új JFrame object létrehozás, ablak neve mindig "Tutty Frutty". Beállítja a fókuszt, méretezést false-ra és a képernyõ közepére pozicionál
	 * @param panel JPanel instance
	 * @param closeOperation JFrame classból opció bezáráskori dolog lekezelésre
	 * @param width Szélesség 
	 * @param height Magasság
	 * @return A Preparált JFrame object
	 */
	public static JFrame newFrame(JPanel panel, int closeOperation, int width, int height) {
		return newFrame(panel, "Tutty Frutty", closeOperation, width, height);
	}
	
	/**
	 * Új JFrame object létrehozás, ablak neve függvény paraméter. Beállítja a fókuszt, méretezést false-ra és a képernyõ közepére pozicionál
	 * @param panel JPanel instance
	 * @param name Ablak neve
	 * @param closeOperation JFrame classból opció bezáráskori dolog lekezelésre
	 * @param width Szélesség 
	 * @param height Magasság
	 * @return A Preparált JFrame object
	 */
	public static JFrame newFrame(JPanel panel, String name, int closeOperation, int width, int height) {
		JFrame returnFrame = new JFrame(name);
		returnFrame.setDefaultCloseOperation(closeOperation);
		returnFrame.setResizable(false);
		returnFrame.setBounds(0, 0, width, height);
		returnFrame.setLocationRelativeTo(null);
		returnFrame.setContentPane(panel);
		returnFrame.setFocusable(true);
		return returnFrame;
	}
	
	/**
	 * Új gomb létrehozás, ez ActionListenert fogad, beállítja a koordinátákat, szélesség = 120, magasság = 30
	 * @param text Gomb szövege
	 * @param x X koordináta
	 * @param y Y koordináta
	 * @param listener ActionListener Object
	 * @return A preparált JButton object
	 */
	public static JButton newButton(String text, int x, int y, ActionListener listener) {
		JButton butt = new JButton(text);
		butt.setBounds(x, y, 120, 30);
		butt.addActionListener(listener);
		return butt;
	}
	
	/**
	 * Új gomb létrehozás, ez MouseListenert fogad, beállítja a koordinátákat, szélesség = 120, magasság = 30
	 * @param text Gomb szövege
	 * @param x X koordináta
	 * @param y Y koordináta
	 * @param listener MouseListener Object
	 * @return A preparált JButton object
	 */
	public static JButton newButton(String text, int x, int y, MouseListener listener) {
		JButton butt = new JButton(text);
		butt.setBounds(x, y, 120, 30);
		butt.addMouseListener(listener);
		return butt;
	}
}