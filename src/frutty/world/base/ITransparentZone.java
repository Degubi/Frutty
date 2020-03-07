package frutty.world.base;

import frutty.tools.*;
import java.awt.*;

/**Interface for transparent zones. It adds a second layer to the zone.*/
public interface ITransparentZone {
    /**
     * The method that renders the second layer of the zone. E.g: Water zone normal layer renders a fully black background, then it renders a transparent water overlay on top of it.
     * @param x X coordinate of the zone
     * @param y Y coordinate of the zone
     * @param material Texture index for zone
     * @param graphics Graphics object
     */
    void drawAfter(int x, int y, Material material, Graphics graphics);
}