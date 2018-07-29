package frutty.gui.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;

public final class SettingButton extends JComponent implements ActionListener{
	public static final String[] ON_OFF = {"Off", "On"};
	private static final ImageIcon leftIcon = new ImageIcon("./textures/gui/arrow_left.png");
	private static final ImageIcon rightIcon = new ImageIcon("./textures/gui/arrow_right.png");
	
	private final String titleText;
	private final String[] options;
	public int optionIndex;
	
	public SettingButton(int selectedIndex, String displayText, int x, int y, String... options) {
		setBounds(x, y, 700, 64);
		titleText = displayText;
		this.options = options;
		optionIndex = selectedIndex;
		add(newArrowButton(false, 340, 8, this));
		add(newArrowButton(true, 620, 8, this));
	}
	
	public SettingButton(int selectedIndex, String displayText, int x, int y, int[] values) {
		this(selectedIndex, displayText, x, y, toStringArray(values));
	}
	
	public SettingButton(boolean isOn, String displayText, int x, int y, String... options) {
		this(isOn ? 1 : 0, displayText, x, y, options);
	}
	
	@Override
	public void paintComponent(Graphics graphics) {
		graphics.setColor(Color.BLACK);
		graphics.drawRect(0, 0, 695, 62);
		graphics.drawRect(1, 1, 695, 62);
		
		graphics.setColor(GuiHelper.color_84Black);
		graphics.fillRect(4, 4, 692, 58);
		
		graphics.setColor(Color.WHITE);
		graphics.setFont(GuiHelper.bigFont);
		graphics.drawString(titleText, 10, 40);
		graphics.drawString(options[optionIndex], 500 - options[optionIndex].length() * 8, 40);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand();
		
		if(command.equals("leftButton")) {
			if(optionIndex > 0) {
				--optionIndex;
			}else{
				optionIndex = options.length - 1;
			}
		}else if(command.equals("rightButton")) {
			if(optionIndex < options.length - 1) {
				++optionIndex;
			}else{
				optionIndex = 0;
			}
		}
		repaint();
	}
	
	private static JButton newArrowButton(boolean isRight, int x, int y, ActionListener listener) {
		JButton toReturn = new JButton(isRight ? rightIcon : leftIcon);
		toReturn.addActionListener(listener);
		toReturn.setActionCommand(isRight ? "rightButton" : "leftButton");
		toReturn.setContentAreaFilled(false);
		toReturn.setBounds(x, y, 48, 48);
		return toReturn;
	}
	
	private static String[] toStringArray(int[] values) {
		String[] toStr = new String[values.length];
		for(int k = 0; k < values.length; ++k) {
			toStr[k] = Integer.toString(values[k]);
		}
		return toStr;
	}
	
	public static int indexOf(int value, int[] values) {
		for(int k = 0; k < values.length; ++k) {
			if(values[k] == value) {
				return k;
			}
		}
		throw new IllegalArgumentException("Should not get there...");
	}
}