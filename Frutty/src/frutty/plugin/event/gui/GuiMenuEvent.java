package frutty.plugin.event.gui;

import java.util.ArrayList;

import javax.swing.JButton;

import frutty.plugin.internal.EventBase;

/**Event is fired when the main menu is initialized, can add buttons to menu*/
public final class GuiMenuEvent extends EventBase{
	private final ArrayList<JButton> buttons;
	
	public GuiMenuEvent(ArrayList<JButton> pass) {
		buttons = pass;
	}
	
	public void addButton(JButton button) {
		buttons.add(button);
	}
}