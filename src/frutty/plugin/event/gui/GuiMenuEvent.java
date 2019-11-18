package frutty.plugin.event.gui;

import frutty.*;
import java.util.*;
import javax.swing.*;

/**Event is fired when the main menu is initialized, can add buttons to menu*/
@FruttyEventMarker
public final class GuiMenuEvent{
	private final List<JButton> buttons;
	
	public GuiMenuEvent(List<JButton> pass) {
		buttons = pass;
	}
	
	public void addButton(JButton button) {
		buttons.add(button);
	}
}