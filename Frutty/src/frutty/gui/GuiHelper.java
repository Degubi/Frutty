package frutty.gui;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public final class GuiHelper {
	public static final Font ingameFont = new Font("TimesRoman", Font.PLAIN, 22);
	public static final Font thiccFont = new Font("TimeRoman", Font.BOLD, 12);
	
	public static final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
	public static final int recommendedMaxMapHeight = screen.height / 64 - 1;
	public static final int recommendedMaxMapWidth = screen.width / 64 - 1;
	public static final String recommendedMapSizeString = "Recommended max map size: " + recommendedMaxMapWidth + "x" + recommendedMaxMapHeight;
	
	private GuiHelper() {}
	
	public static void showNewFrame(JPanel panel, String name, int closeOperation, int width, int height) {
		EventQueue.invokeLater(() -> {
			JFrame returnFrame = new JFrame(name);
			returnFrame.setDefaultCloseOperation(closeOperation);
			returnFrame.setResizable(false);
			returnFrame.setBounds(0, 0, width, height);
			returnFrame.setLocationRelativeTo(null);
			returnFrame.setContentPane(panel);
			returnFrame.setFocusable(true);
			returnFrame.setVisible(true);
		});
	}
	
	public static JButton newButton(String text, int x, int y, ActionListener listener) {
		JButton butt = new JButton(text);
		butt.setBounds(x, y, 120, 30);
		butt.setMnemonic(100);
		butt.addActionListener(listener);
		return butt;
	}
	
	public static JButton newButton(String text, int x, int y, MouseListener listener) {
		JButton butt = new JButton(text);
		butt.setBounds(x, y, 120, 30);
		butt.setMnemonic(100);
		butt.addMouseListener(listener);
		return butt;
	}
}