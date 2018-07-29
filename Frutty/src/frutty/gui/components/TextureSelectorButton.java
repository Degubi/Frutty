package frutty.gui.components;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import frutty.gui.GuiEditor;

public final class TextureSelectorButton extends JButton implements ActionListener{
	public String activeTexture = "normal";
	protected int activeTextureIndex = GuiTextureSelector.indexOf("normal.png");
	private final GuiEditor editorInstance;
		
	public TextureSelectorButton(int width, GuiEditor editor) {
		super(GuiTextureSelector.bigTextures[GuiTextureSelector.indexOf("normal.png")]);
			
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
			frame.setBounds(0, 0, (GuiTextureSelector.textureNames.length + 1) * 128, (GuiTextureSelector.textureNames.length + 1) * 64);
			frame.setLocationRelativeTo(null);
			frame.setFocusable(true);
			frame.setVisible(true);
		});
	}
}