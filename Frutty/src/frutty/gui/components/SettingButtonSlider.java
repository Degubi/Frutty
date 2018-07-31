package frutty.gui.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;

import frutty.tools.GuiHelper;

public final class SettingButtonSlider extends JComponent implements ActionListener{
	private static final ImageIcon RED_ICON = getColoredImage(Color.RED);
	private static final ImageIcon TRANSPARENT_ICON = getColoredImage(GuiHelper.color_128Black);
	
	public int counter;
	private final String titleText;
	private final JButton[] butts = new JButton[10];
	
	public SettingButtonSlider(int index, String displayText, int x, int y) {
		setBounds(x, y, 700, 64);
		titleText = displayText;
		counter = index;
		
		for(int k = 1; k < 11; ++k) {
			JButton kek = new JButton(k - 1 < index ? RED_ICON : TRANSPARENT_ICON);
			kek.setBounds(385 + k * 20, 24, 16, 16);
			kek.setMnemonic(k);
			kek.addActionListener(this);
			butts[k - 1] = kek;
			add(kek);
		}
		
		add(SettingButton.newArrowButton(false, 340, 8, this));
		add(SettingButton.newArrowButton(true, 620, 8, this));
	}
	
	private static ImageIcon getColoredImage(Color color) {
		BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		
		int rgb = color.getRGB();
		for(int x = 0; x < 16; ++x) {
			for(int y = 0; y < 16; ++y) {
				img.setRGB(x, y, rgb);
			}
		}
		return new ImageIcon(img);
	}
	
	@Override
	public void paintComponent(Graphics graphics) {
		graphics.setColor(GuiHelper.color_192Black);
		graphics.fillRect(4, 4, 692, 58);
		
		graphics.setColor(Color.WHITE);
		graphics.drawRect(0, 0, 695, 62);
		graphics.drawRect(1, 1, 695, 62);
		
		graphics.setFont(GuiHelper.bigFont);
		graphics.drawString(titleText, 10, 40);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand();
		
		if(command.equals("leftButton")) {
			if(counter > 1) {
				--counter;
			}
		}else if(command.equals("rightButton")) {
			if(counter < 10) {
				++counter;
			}
		}else {
			counter = ((JButton)event.getSource()).getMnemonic();
		}
		
		for(int k = 1; k < 11; ++k) {
			butts[k - 1].setIcon(k - 1 < counter ? RED_ICON : TRANSPARENT_ICON);
		}
	}
}