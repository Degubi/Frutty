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
 * Static helper class, Inicializ�l 2 font objectet, illetve gomb �s jframe kezel�sre ad f�ggv�nyeket
 */
public class GuiHelper {
	public static final Font ingameFont = new Font("TimesRoman", Font.PLAIN, 22);
	public static final Font thiccFont = new Font("TimeRoman", Font.BOLD, 12);
	
	public static final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
	public static final int recommendedMaxMapHeight = screen.height / 64 - 1;
	public static final int recommendedMaxMapWidth = screen.width / 64 - 1;
	public static final String recommendedMapSizeString = "Recommended max map size: " + recommendedMaxMapWidth + "x" + recommendedMaxMapHeight;

	
	/**
	 * �j JFrame object l�trehoz�s, ablak neve mindig "Tutty Frutty". Be�ll�tja a f�kuszt, m�retez�st false-ra �s a k�perny� k�zep�re pozicion�l
	 * @param panel JPanel instance
	 * @param closeOperation JFrame classb�l opci� bez�r�skori dolog lekezel�sre
	 * @param width Sz�less�g 
	 * @param height Magass�g
	 * @return A Prepar�lt JFrame object
	 */
	public static JFrame newFrame(JPanel panel, int closeOperation, int width, int height) {
		return newFrame(panel, "Tutty Frutty", closeOperation, width, height);
	}
	
	/**
	 * �j JFrame object l�trehoz�s, ablak neve f�ggv�ny param�ter. Be�ll�tja a f�kuszt, m�retez�st false-ra �s a k�perny� k�zep�re pozicion�l
	 * @param panel JPanel instance
	 * @param name Ablak neve
	 * @param closeOperation JFrame classb�l opci� bez�r�skori dolog lekezel�sre
	 * @param width Sz�less�g 
	 * @param height Magass�g
	 * @return A Prepar�lt JFrame object
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
	 * �j gomb l�trehoz�s, ez ActionListenert fogad, be�ll�tja a koordin�t�kat, sz�less�g = 120, magass�g = 30
	 * @param text Gomb sz�vege
	 * @param x X koordin�ta
	 * @param y Y koordin�ta
	 * @param listener ActionListener Object
	 * @return A prepar�lt JButton object
	 */
	public static JButton newButton(String text, int x, int y, ActionListener listener) {
		JButton butt = new JButton(text);
		butt.setBounds(x, y, 120, 30);
		butt.addActionListener(listener);
		return butt;
	}
	
	/**
	 * �j gomb l�trehoz�s, ez MouseListenert fogad, be�ll�tja a koordin�t�kat, sz�less�g = 120, magass�g = 30
	 * @param text Gomb sz�vege
	 * @param x X koordin�ta
	 * @param y Y koordin�ta
	 * @param listener MouseListener Object
	 * @return A prepar�lt JButton object
	 */
	public static JButton newButton(String text, int x, int y, MouseListener listener) {
		JButton butt = new JButton(text);
		butt.setBounds(x, y, 120, 30);
		butt.addMouseListener(listener);
		return butt;
	}
}