package frutty.gui.components;

import frutty.tools.*;
import java.awt.*;
import java.util.regex.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;

public final class SettingFieldInput extends JComponent {
    private final JTextField dataField;
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
        dataField.setBorder(new LineBorder(Color.LIGHT_GRAY));
        add(dataField);
    }

    public SettingFieldInput(String selectedData, String displayText, int x, int y, int width, String contentValidatorPattern) {
        this(selectedData, displayText, x, y, width);

        var validator = Pattern.compile(contentValidatorPattern).asMatchPredicate();

        dataField.getDocument().addDocumentListener((TextFieldChangeListener) e ->
            dataField.setBorder(new LineBorder(validator.test(dataField.getText()) ? Color.LIGHT_GRAY : Color.RED)));
    }

    // Constructor used in keybind selection setting
    public SettingFieldInput(int characterData, String displayText, int x, int y) {
        this(Character.toString((char) characterData), displayText, x, y, 100);

        ((AbstractDocument) dataField.getDocument()).setDocumentFilter(TextFilter.filter);
    }

    public String getValue() {
        return dataField.getText();
    }

    @Override
    public void paintComponent(Graphics graphics) {
        graphics.setColor(GuiHelper.color_192Black);
        graphics.fillRect(2, 2, 694, 60);

        graphics.setColor(Color.WHITE);
        graphics.drawRect(0, 0, 695, 62);
        graphics.drawRect(1, 1, 695, 62);

        graphics.setFont(GuiHelper.bigFont);
        graphics.drawString(titleText, 10, 40);
    }

    private static final class TextFilter extends DocumentFilter {
        public static final TextFilter filter = new TextFilter();

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            super.replace(fb, offset, length, text.toUpperCase(), attrs);
            if(offset > 0) {
                super.remove(fb, 0, 1);
            }
        }
    }

    @FunctionalInterface
    private interface TextFieldChangeListener extends DocumentListener {
        void update(DocumentEvent e);

        @Override
        default void insertUpdate(DocumentEvent e) { update(e); }
        @Override
        default void removeUpdate(DocumentEvent e) { update(e); }
        @Override
        default void changedUpdate(DocumentEvent e) { update(e); }
    }
}