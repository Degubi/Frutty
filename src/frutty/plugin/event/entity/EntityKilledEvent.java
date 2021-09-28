package frutty.plugin.event.entity;

import frutty.*;
import frutty.entity.*;

/**Event is fired when an entity gets killed*/
@FruttyEventMarker
public final class EntityKilledEvent {
    public final EntityBase killedEntity;
    public final EntityBase killerEntity;

    public EntityKilledEvent(EntityBase killedEntity, EntityBase killerEntity) {
        this.killedEntity = killedEntity;
        this.killerEntity = killerEntity;
    }
}