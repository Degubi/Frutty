package frutty.gui.components;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import frutty.Main;
import frutty.gui.GuiEditor;
import frutty.world.base.MapZoneTexturable;

public final class EditorZoneButton extends JButton implements MouseListener{
	public String zoneTexture, zoneID;
	private final GuiEditor editorInstance;
	
	public EditorZoneButton(ImageIcon texture, GuiEditor editor) {
		super(texture);
		editorInstance = editor;
		
		addMouseListener(this);
	}
	
	@Override
	public void mousePressed(MouseEvent event) {
		EditorZoneButton button = (EditorZoneButton)event.getComponent();
		int pressedButton = event.getButton();
		
		if(pressedButton == MouseEvent.BUTTON1) {
			String activeZoneName = (String) editorInstance.zoneList.getSelectedItem();
			button.zoneID = activeZoneName; button.setIcon(Main.getZoneFromName(activeZoneName).editorTexture.get());
			
			if(Main.getZoneFromName(activeZoneName) instanceof MapZoneTexturable) {
				button.setIcon(((MapZoneTexturable)Main.getZoneFromName(button.zoneID)).textureVariants.get()[editorInstance.textureSelectorButton.activeTextureIndex]);
				button.zoneTexture = editorInstance.textureSelectorButton.activeTexture;
			}else{
				button.zoneTexture = null;
			}
			
		}else if(pressedButton == MouseEvent.BUTTON3) {
			if(Main.getZoneFromName(button.zoneID) instanceof MapZoneTexturable) {
				button.setIcon(((MapZoneTexturable)Main.getZoneFromName(button.zoneID)).textureVariants.get()[editorInstance.textureSelectorButton.activeTextureIndex]);
				button.zoneTexture = editorInstance.textureSelectorButton.activeTexture;
			}
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
}