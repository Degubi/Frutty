package frutty.plugin.event.gui;

import frutty.*;
import frutty.gui.*;
import java.util.*;
import javax.swing.*;

/**Event is fired when the main menu is initialized, can add buttons to menu*/
@FruttyEventMarker
public final class GuiMenuEvent {
    private final List<JComponent> newComponents;

    public GuiMenuEvent(List<JComponent> pass) {
        newComponents = pass;
    }

    public void addNewComponent(JComponent newComponent) {
        newComponents.add(newComponent);
    }

    public static void closeMainMenu() {
        GuiMainMenu.closeMainFrame();
    }

    public static void openMainMenu() {
        GuiMainMenu.createMainFrame();
    }
}