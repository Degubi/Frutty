package frutty.gui.editor;

import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public enum EnumEditorZone{
	Normal(0, "normal.png"),
	Empty(1, "dug.png"),
	Apple(2, "apple.png"),
	Cherry(3, "cherry.png"),
	Spawner(4, "spawner.png"),
	Player1(5, "player1.png"),
	Player2(6, "player2.png"),
	Chest(7, "chest.png"),
	Water(8, "water.png"),
	Sky(9, "sky.png");
	
	private static final EnumEditorZone[] zones = values();
	
	public final int zoneIndex;
	public final ImageIcon icon;
	
	private EnumEditorZone(int index, String texture){
		zoneIndex = index;
		icon = new ImageIcon("./textures/dev/" + texture);
	}
	
	public void handlePrevious(JButton button, GuiProperties props, MouseEvent event) {
		EnumEditorZone prev = getPrevious(event, props, event.getX(), event.getY());
		button.setMnemonic(prev.zoneIndex);
		button.setIcon(prev.icon);
	}
	
	public static EnumEditorZone getFromIndex(int index) {
		return zones[index];
	}
	
	public void handleReading(GuiEditor editor, int x, int y) {
		JButton button = new JButton(icon);
		button.setBounds(x * 64, y * 64, 64, 64);
		button.setMnemonic(zoneIndex);
		button.addMouseListener(editor);
		editor.zoneButtons.add(button);
		editor.add(button);
	}
	
	public void handleNext(JButton button, GuiProperties props, MouseEvent event) {
		EnumEditorZone next = getNext(event, props, button.getX(), button.getY());
		button.setMnemonic(next.zoneIndex);
		button.setIcon(next.icon);
	}
	
	private EnumEditorZone getPrevious(MouseEvent event, GuiProperties props, int mouseX, int mouseY) {
		if(event.isShiftDown()) {
			props.setPlayer1Pos(mouseX, mouseY);
			return Player1;
		}
		if(event.isControlDown()) {
			props.setPlayer2Pos(mouseX, mouseY);
			return Player2;
		}
		if(event.isAltDown()) {
			return Spawner;
		}
		if(zoneIndex == 7){
			return Cherry;
		}
		if(zoneIndex == 4 || zoneIndex == 5 || zoneIndex == 6) {
			return Normal;
		}
		return zoneIndex == 0 ? Sky : zones[zoneIndex - 1];
	}
	
	private EnumEditorZone getNext(MouseEvent event, GuiProperties props, int mouseX, int mouseY) {
		if(event.isShiftDown()) {
			props.setPlayer1Pos(mouseX, mouseY);
			return Player1;
		}
		if(event.isControlDown()) {
			props.setPlayer2Pos(mouseX, mouseY);
			return Player2;
		}
		if(event.isAltDown()) {
			return Spawner;
		}
		
		if(zoneIndex == 3) {
			return Chest;
		}
		if((zoneIndex == 4 || zoneIndex == 5 || zoneIndex == 6)) {
			return Normal;
		}
		return zoneIndex == zones.length - 1 ? Normal : zones[zoneIndex + 1];
	}
}