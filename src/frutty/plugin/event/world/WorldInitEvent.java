package frutty.plugin.event.world;

import frutty.*;
import frutty.entity.*;
import java.util.*;

/**Event is fired when a world is initialized, can add entities*/
@FruttyEventMarker
public final class WorldInitEvent {
    public final int worldWidth, worldHeight;
    public final List<EntityBase> entities;

    public WorldInitEvent(int width, int height, List<EntityBase> entities) {
        this.worldWidth = width;
        this.worldHeight = height;
        this.entities = entities;
    }
}