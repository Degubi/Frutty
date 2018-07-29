package frutty.gui.components;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public final class SettingButtonField extends JComponent{
	public final JTextField dataField;
	private final String titleText;
	
	public SettingButtonField(String selectedData, String displayText, int x, int y) {
		setBounds(x, y, 700, 64);
		titleText = displayText;
		
		dataField = new JTextField(selectedData);
		dataField.setBounds(550, 8, 80, 50);
		dataField.setOpaque(false);
		dataField.setForeground(Color.WHITE);
		dataField.setFont(GuiHelper.bigFont);
		((AbstractDocument) dataField.getDocument()).setDocumentFilter(TextFilter.filter);
		dataField.setHorizontalAlignment(SwingConstants.CENTER);
		add(dataField);
	}
	
	public SettingButtonField(int characterData, String displayText, int x, int y) {
		this(Character.toString((char)characterData), displayText, x, y);
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
	}
	
	protected static final class TextFilter extends DocumentFilter{
		public static final TextFilter filter = new TextFilter();
			
		@Override
		public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
			super.replace(fb, offset, length, text.toUpperCase(), attrs);
			if(offset > 0) {
				super.remove(fb, 0, 1);
			}
		}
	}
}