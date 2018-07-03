package frutty.gui.editor;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import frutty.Main;
import frutty.map.interfaces.IInternalZone;
import frutty.map.interfaces.ITexturable;
import frutty.map.interfaces.MapZoneBase;

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
		if(event.getButton() == MouseEvent.BUTTON1) {
			String activeZoneName = (String) editorInstance.zoneList.getSelectedItem();
			MapZoneBase activeZone = Main.getZoneFromName(activeZoneName);
			
			button.zoneID = activeZoneName; button.setIcon(Main.getZoneFromName(activeZoneName).editorTexture.get());
			
			if(activeZone instanceof ITexturable) {
				button.setIcon(((ITexturable)Main.getZoneFromName(button.zoneID)).getEditorTextureVars()[editorInstance.textureSelectorButton.activeTextureIndex]);
				button.zoneTexture = editorInstance.textureSelectorButton.activeTexture;
			}if(activeZone instanceof IInternalZone) {
				((IInternalZone) activeZone).handleEditorPlacement(editorInstance, button.getX(), button.getY());
			}
		}else if(event.getButton() == MouseEvent.BUTTON3) {
			if(Main.getZoneFromName(button.zoneID) instanceof ITexturable) {
				button.setIcon(((ITexturable)Main.getZoneFromName(button.zoneID)).getEditorTextureVars()[editorInstance.textureSelectorButton.activeTextureIndex]);
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