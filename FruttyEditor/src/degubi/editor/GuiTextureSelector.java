package degubi.editor;

import frutty.tools.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.FocusEvent.*;
import javax.swing.*;

public final class GuiTextureSelector extends JPanel implements ActionListener, FocusListener{
	private final GuiEditor editor;
		
	public GuiTextureSelector(GuiEditor ed) {
		editor = ed;
		setLayout(null);
			
		String[] materialNames = Material.getMaterialNames();
		
		for(int index = 0, xPosition = 10, yPosition = 20; index < materialNames.length; ++index) {
			JButton button = new JButton(Material.materialRegistry.get(materialNames[index]).editorUpscaledTexture.get());
			button.setActionCommand(materialNames[index]);
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
		Material mat = Material.materialRegistry.get(event.getActionCommand());
		editor.textureSelectorButton.activeMaterial = mat;
		editor.textureSelectorButton.setIcon(mat.editorUpscaledTexture.get());
		editor.repaint();
		((JFrame)getTopLevelAncestor()).dispose();
	}
		
	@Override
	public void focusGained(FocusEvent event) {}
	@Override
	public void focusLost(FocusEvent event) {
		if(event.getCause() == Cause.ACTIVATION) {
			((JFrame)getTopLevelAncestor()).dispose();
		}
	}
	
	static final class TextureSelectorButton extends JButton implements ActionListener{
		public Material activeMaterial = Material.NORMAL;
		private final GuiEditor editorInstance;
			
		public TextureSelectorButton(int width, GuiEditor editor) {
			super(Material.NORMAL.editorUpscaledTexture.get());
				
			editorInstance = editor;
			setToolTipText("Texture Selector Tool");
			addActionListener(this);
			setBounds(width * 64 + 20, 200, 128, 128);
		}
			
		@Override
		public void actionPerformed(ActionEvent event) {
			EventQueue.invokeLater(() -> {
				JFrame frame = new JFrame("Texture Selector");
				GuiTextureSelector gui = new GuiTextureSelector(editorInstance);
				frame.setContentPane(gui);
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