package frutty.plugin.event.gui;

import frutty.*;
import java.awt.*;
import javax.swing.*;

/**Event is fired while the ingame renderer is running*/
@FruttyEventMarker
public final class ScreenOverlayEvent {
    
    public final Graphics2D graphics;
    public final int frameWidth;
    public final int frameHeight;
    
    public ScreenOverlayEvent(Graphics2D graphics, JFrame ingameFrame) {
        this.graphics = graphics;
        this.frameWidth = ingameFrame.getWidth();
        this.frameHeight = ingameFrame.getHeight();
    }
}