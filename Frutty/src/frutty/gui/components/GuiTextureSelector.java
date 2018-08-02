package frutty.gui.components;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusEvent.Cause;
import java.awt.event.FocusListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import frutty.gui.GuiEditor;

public final class GuiTextureSelector extends JPanel implements ActionListener, FocusListener{
	private final GuiEditor editor;
	
	public static final String[] textureNames = new File("./textures/map").list((file, name) -> name.endsWith(".png"));
	public static final ImageIcon[] bigScaledTextures = getUpscaledTextures();
	
	private static ImageIcon[] getUpscaledTextures() {
		ImageIcon[] toReturn = new ImageIcon[textureNames.length];
		
		for(int k = 0; k < textureNames.length; ++k) {
			ImageIcon nrm = new ImageIcon("./textures/map/" + textureNames[k]);
			toReturn[k] = new ImageIcon((nrm).getImage().getScaledInstance(128, 128, Image.SCALE_DEFAULT));
		}
		return toReturn;
	}
	
	public static int indexOf(String name) {
		for(int k = 0; k < textureNames.length; ++k) {
			if(textureNames[k].equals(name)) {
				return k;
			}
		}
		return -1;
	}
		
	public GuiTextureSelector(GuiEditor ed) {
		editor = ed;
		setLayout(null);
			
		for(int index = 0, xPosition = 10, yPosition = 20; index < bigScaledTextures.length; ++index) {
			JButton button = new JButton(bigScaledTextures[index]);
			button.setActionCommand(textureNames[index].substring(0, textureNames[index].length() - 4));
			button.setBounds(xPosition, yPosition, 128, 128);
			button.addActionListener(this);
			xPosition += 138;
			
			if(xPosition > 600) {
				xPosition = 10;
				yPosition += 138;
			}
			add(button);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource() instanceof JButton) {
			JButton button = (JButton) event.getSource();
			editor.textureSelectorButton.activeTexture = button.getActionCommand();
			editor.textureSelectorButton.setIcon(button.getIcon());
			editor.textureSelectorButton.activeTextureIndex = indexOf(button.getActionCommand() + ".png");
			editor.repaint();
			((JFrame)getTopLevelAncestor()).dispose();
		}
	}
		
	@Override
	public void focusGained(FocusEvent event) {}
	@Override
	public void focusLost(FocusEvent event) {
		if(event.getCause() == Cause.ACTIVATION) {
			((JFrame)getTopLevelAncestor()).dispose();
		}
	}
}