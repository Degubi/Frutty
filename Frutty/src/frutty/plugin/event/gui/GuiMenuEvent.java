package frutty.plugin.event.gui;

import java.util.List;

import javax.swing.JButton;

import frutty.plugin.internal.EventBase;

/**Event is fired when the main menu is initialized, can add buttons to menu*/
public final class GuiMenuEvent extends EventBase{
	private final List<JButton> buttons;
	
	public GuiMenuEvent(List<JButton> pass) {
		buttons = pass;
	}
	
	public void addButton(JButton button) {
		buttons.add(button);
	}
}