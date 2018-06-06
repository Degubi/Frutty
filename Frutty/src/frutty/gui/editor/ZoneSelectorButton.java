package frutty.gui.editor;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import frutty.Main;
import frutty.gui.editor.GuiToolSelector.GuiTextureSelector;

public final class ZoneSelectorButton extends JButton implements ActionListener{
	protected String activeZone = "normalZone";
	private final GuiEditor editorInstance;
	
	public ZoneSelectorButton(int mapWidth, GuiEditor editor) {
		super(Main.emptyZone.editorTexture.get());
		
		editorInstance = editor;
		setToolTipText("Zone Selector Tool");
		addActionListener(this);
		setBounds(mapWidth * 64 + 20, 80, 64, 64);
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		EventQueue.invokeLater(() -> {
			JFrame toolSelectorFrame = new JFrame("Tool Selector");
			GuiToolSelector selector = new GuiToolSelector(editorInstance);
			toolSelectorFrame.setContentPane(selector);
			toolSelectorFrame.addFocusListener(selector);
			toolSelectorFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			toolSelectorFrame.setResizable(false);
			int height = Main.zoneRegistry.size() % 2 == 0 ? ((Main.zoneRegistry.size() + 2) / 2) * 64 : ((Main.zoneRegistry.size() + 3) / 2) * 64;
			toolSelectorFrame.setBounds(getLocationOnScreen().x, getLocationOnScreen().y, 128, height);
			toolSelectorFrame.setFocusable(true);
			toolSelectorFrame.setUndecorated(true);
			toolSelectorFrame.setVisible(true);
		});
	}

	public static final class TextureSelectorButton extends JButton implements ActionListener{
		protected String activeTexture = "normal";
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
}