package frutty.plugin.event.world;

import frutty.*;
import frutty.entity.*;
import java.util.*;

/**Event is fired when a world is initialized, can add entities, or overwrite textures*/
@FruttyEventMarker
public final class WorldInitEvent {
    public final int worldWidth, worldHeight;
    public final String[] worldTextures;
    public final List<EntityBase> worldEntities;

    public WorldInitEvent(int width, int height, String[] textures, List<EntityBase> entities) {
        this.worldWidth = width;
        this.worldHeight = height;
        this.worldTextures = textures;
        this.worldEntities = entities;
    }
}