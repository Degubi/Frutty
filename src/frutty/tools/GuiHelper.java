package frutty.tools;

import frutty.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public final class GuiHelper {
    public static final Font ingameFont = new Font("TimesRoman", Font.PLAIN, 22);
    public static final Font thiccFont = new Font("TimeRoman", Font.BOLD, 14);
    public static final Font bigFont = new Font("TimesRoman", Font.BOLD, 28);
    public static final Color color_128Black = new Color(0, 0, 0, 128), color_84Black = new Color(0, 0, 0, 84), color_192Black = new Color(0, 0, 0, 192);
    public static final LineBorder menuBorder = new LineBorder(Color.DARK_GRAY, 2);
    public static final Image frameIcon = Toolkit.getDefaultToolkit().createImage(Main.executionDir + "textures/player/side.png");

    private GuiHelper() {}
    
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
    
    public static JLabel newLabel(String text, int x, int y) {
        var label = new JLabel(text);
        label.setBounds(x, y, text.length() * 12, 30);
        label.setFont(ingameFont);
        return label;
    }
}