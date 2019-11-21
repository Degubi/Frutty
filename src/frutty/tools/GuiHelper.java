package frutty.tools;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public final class GuiHelper {
	public static final Font ingameFont = new Font("TimesRoman", Font.PLAIN, 22);
	public static final Font thiccFont = new Font("TimeRoman", Font.BOLD, 14);
	public static final Font bigFont = new Font("TimesRoman", Font.BOLD, 28);
	public static final Color color_128Black = new Color(0, 0, 0, 128), color_84Black = new Color(0, 0, 0, 84), color_192Black = new Color(0, 0, 0, 192);
	private static final int recommendedMaxMapHeight = Toolkit.getDefaultToolkit().getScreenSize().height / 64 - 1;
	private static final int recommendedMaxMapWidth = Toolkit.getDefaultToolkit().getScreenSize().width / 64 - 1;
	public static final LineBorder menuBorder = new LineBorder(Color.DARK_GRAY, 2);
	public static final Image frameIcon = Toolkit.getDefaultToolkit().createImage("./textures/player/side.png");

	private GuiHelper() {}
	
	public static void mapSizeCheck(int width, int height) {
		if(width > GuiHelper.recommendedMaxMapWidth || height > GuiHelper.recommendedMaxMapHeight) {
			JOptionPane.showMessageDialog(null, "Warning: map size is bigger than the recommended max map size!");
		}
	}
	
	public static JCheckBox newCheckBox(String text, int x, int y, Color foreground, boolean setSelected) {
		var box = new JCheckBox(text, setSelected);
		box.setBounds(x, y, 150, 30);
		box.setForeground(foreground);
		box.setOpaque(false);
		return box;
	}
	
	public static JButton newButton(String text, int x, int y, ActionListener listener) {
		var butt = new JButton(text);
		butt.setBounds(x, y, 160, 60);
		butt.setBorder(menuBorder);
		butt.setBackground(Color.LIGHT_GRAY);
		butt.setMnemonic(100);
		butt.addActionListener(listener);
		return butt;
	}
}