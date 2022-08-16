package frutty.gui.components;

import frutty.tools.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;

public final class SettingFieldInput extends JComponent {
    public final JTextField dataField;
    private final String titleText;

    public SettingFieldInput(String selectedData, String displayText, int x, int y, int width) {
        setBounds(x, y, 700, 64);
        titleText = displayText;

        dataField = new JTextField(selectedData);
        dataField.setBounds(650 - width, 8, width, 50);
        dataField.setOpaque(false);
        dataField.setForeground(Color.WHITE);
        dataField.setFont(GuiHelper.bigFont);
        dataField.setHorizontalAlignment(SwingConstants.CENTER);
        dataField.setCaretColor(Color.WHITE);
        add(dataField);
    }

    // Constructor used in keybind selection setting
    public SettingFieldInput(int characterData, String displayText, int x, int y) {
        this(Character.toString((char)characterData), displayText, x, y, 100);

        ((AbstractDocument) dataField.getDocument()).setDocumentFilter(TextFilter.filter);
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

    protected static final class TextFilter extends DocumentFilter {
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