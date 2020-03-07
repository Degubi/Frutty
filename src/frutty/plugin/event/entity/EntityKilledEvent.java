package frutty.plugin.event.entity;

import frutty.*;
import frutty.entity.*;

/**Event is fired when an entity gets killed*/
@FruttyEventMarker
public final class EntityKilledEvent {
    public final Entity killedEntity;
    public final Entity killerEntity;
    
    public EntityKilledEvent(Entity killedEntity, Entity killerEntity) {
        this.killedEntity = killedEntity;
        this.killerEntity = killerEntity;
    }
}