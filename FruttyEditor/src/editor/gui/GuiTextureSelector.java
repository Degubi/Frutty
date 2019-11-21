package editor.gui;

import frutty.tools.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.FocusEvent.*;
import javax.swing.*;

public final class GuiTextureSelector extends FocusAdapter {
    public final JPanel panel = new JPanel(null);
    
	public GuiTextureSelector(GuiEditor editor) {
		var materialNames = Material.getMaterialNames();
		
		for(int index = 0, xPosition = 10, yPosition = 20; index < materialNames.length; ++index) {
			var button = new JButton(Material.materialRegistry.get(materialNames[index]).editorUpscaledTexture.get());
			
			button.setActionCommand(materialNames[index]);
			button.setBounds(xPosition, yPosition, 128, 128);
			button.addActionListener(e -> handleTextureButtonPress(e, panel, editor));
			xPosition += 138;
			
			if(xPosition > 600) {
				xPosition = 10;
				yPosition += 138;
			}
			panel.add(button);
		}
	}
	
	private static void handleTextureButtonPress(ActionEvent event, JPanel panel, GuiEditor editor) {
		var mat = Material.materialRegistry.get(event.getActionCommand());
		editor.textureSelector.activeMaterial = mat;
		editor.textureSelector.button.setIcon(mat.editorUpscaledTexture.get());
		editor.repaint();
		((JFrame)panel.getTopLevelAncestor()).dispose();
	}
	
	@Override
	public void focusLost(FocusEvent event) {
		if(event.getCause() == Cause.ACTIVATION) {
			((JFrame)panel.getTopLevelAncestor()).dispose();
		}
	}
	
	public static final class TextureSelector implements ActionListener{
		public Material activeMaterial = Material.NORMAL;
		public final JButton button;
		private final GuiEditor editorInstance;
			
		public TextureSelector(int width, GuiEditor editor) {
			this.button = new JButton(Material.NORMAL.editorUpscaledTexture.get());
				
			this.editorInstance = editor;
			this.button.setToolTipText("Texture Selector Tool");
			this.button.addActionListener(this);
			this.button.setBounds(width * 64 + 20, 200, 128, 128);
		}
			
		@Override
		public void actionPerformed(ActionEvent event) {
			EventQueue.invokeLater(() -> {
				var frame = new JFrame("Texture Selector");
				var gui = new GuiTextureSelector(editorInstance);
				
				frame.setContentPane(gui.panel);
				frame.addFocusListener(gui);
				frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				frame.setResizable(false);
				frame.setBounds(0, 0, (Material.materialRegistry.size() + 1) * 128, (Material.materialRegistry.size() + 1) * 64);
				frame.setLocationRelativeTo(null);
				frame.setFocusable(true);
				frame.setVisible(true);
			});
		}
	}
}