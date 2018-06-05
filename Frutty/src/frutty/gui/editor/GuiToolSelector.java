package frutty.gui.editor;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent.Cause;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import frutty.Main;

public final class GuiToolSelector extends JPanel implements ActionListener, FocusListener{
	private final GuiEditor editor;
	
	public static final ImageIcon player1Texture = new ImageIcon("./textures/dev/player1.png");
	public static final ImageIcon player2Texture =  new ImageIcon("./textures/dev/player2.png");
	
	public GuiToolSelector(GuiEditor editor) {
		setLayout(null);
		this.editor = editor;
		
		add(newEditorButton(0, Main.normalZone.editorTexture.get(), 0, 0, this));
		add(newEditorButton(1, Main.emptyZone.editorTexture.get(), 64, 0, this));
		add(newEditorButton(2, Main.appleZone.editorTexture.get(), 0, 64, this));
		add(newEditorButton(3, Main.cherryZone.editorTexture.get(), 64, 64, this));
		add(newEditorButton(4, Main.spawnerZone.editorTexture.get(), 0, 128, this));
		add(newEditorButton(7, Main.chestZone.editorTexture.get(), 64, 128, this));
		add(newEditorButton(5, player1Texture, 0, 192, this));
		add(newEditorButton(6, player2Texture, 64, 192, this));
		add(newEditorButton(8, Main.waterZone.editorTexture.get(), 0, 256, this));
		add(newEditorButton(9, Main.skyZone.editorTexture.get(), 64, 256, this));
		
		var entries = Main.zoneRegistry.entrySet();
		int xPos = 0, yPos = 320, counter = 0;
		for(var entry : entries) {
			@SuppressWarnings("boxing")
			int id = entry.getKey();
			if(id > 20) {
				add(newEditorButton(id, entry.getValue().editorTexture.get(), xPos, yPos, this));
			}
			++counter;
			
			if(counter % 2 == 0) {
				xPos = 0;
				yPos += 64;
			}else{
				xPos = 64;
			}
		}
	}
	
	private static JButton newEditorButton(int index, ImageIcon icon, int x, int y, ActionListener listener) {
		JButton butt = new JButton(icon);
		butt.setBounds(x, y, 64, 64);
		butt.setMnemonic(index);
		butt.addActionListener(listener);
		return butt;
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource() instanceof JButton) {
			JButton button = (JButton) event.getSource();
			editor.zoneSelectorButton.activeZone = button.getMnemonic();
			editor.zoneSelectorButton.setIcon(button.getIcon());
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
	
	public static final class GuiTextureSelector extends JPanel implements ActionListener, FocusListener{
		private final GuiEditor editor;
		
		public static final ImageIcon[] bigTextures, normalTextures, appleTextures, cherryTextures, chestTextures;
		static final String[] textureNames;
		
		static {
			String[] nop1 = new File("./textures/map").list(), nop2 = new String[nop1.length];
			
			int count = 0;
			for(int k = 0; k < nop1.length; ++k) {
				if(nop1[k].endsWith(".png")) {
					nop2[count++] = nop1[k];
				}
			}
			
			textureNames = new String[count];
			System.arraycopy(nop2, 0, textureNames, 0, count);
			
			bigTextures = new ImageIcon[count];
			normalTextures = new ImageIcon[count];
			appleTextures = new ImageIcon[count];
			cherryTextures = new ImageIcon[count];
			chestTextures = new ImageIcon[count];
			
			BufferedImage appleTexture = Main.loadTexture("fruit", "apple.png");
			BufferedImage cherryTexture = Main.loadTexture("fruit", "cherry.png");
			BufferedImage chestTexture = Main.loadTexture("map/special", "chest.png");
				
			for(int k = 0; k < textureNames.length; ++k) {
				ImageIcon nrm = new ImageIcon("./textures/map/" + textureNames[k]);
				normalTextures[k] = new ImageIcon((nrm).getImage().getScaledInstance(64, 64, Image.SCALE_DEFAULT));
				bigTextures[k] = new ImageIcon((nrm).getImage().getScaledInstance(128, 128, Image.SCALE_DEFAULT));
				appleTextures[k] = combineTextures(nrm, appleTexture);
				cherryTextures[k] = combineTextures(nrm, cherryTexture);
				chestTextures[k] = combineTextures(nrm, chestTexture);
			}
		}
		
		private static ImageIcon combineTextures(ImageIcon normalTexture, BufferedImage overlay) {
			BufferedImage toReturn = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
			Graphics graph = toReturn.createGraphics();
			graph.drawImage(normalTexture.getImage(), 0, 0, 64, 64, null);
			graph.drawImage(overlay, 0, 0, 64, 64, null);
			return new ImageIcon(toReturn);
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
			
			for(int index = 0, xPosition = 10, yPosition = 20; index < bigTextures.length; ++index) {
				JButton button = new JButton(bigTextures[index]);
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
}