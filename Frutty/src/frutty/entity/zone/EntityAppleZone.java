package frutty.entity.zone;

import frutty.entity.*;
import frutty.world.*;

public final class EntityAppleZone extends EntityZone{
	private int counter;
	
	public EntityAppleZone(int xCoord, int yCoord) {
		super(false, xCoord, yCoord);
	}
	
	@Override
	public void onNotified() {
		needsUpdates = true;
	}
	
	@Override
	public void update() {
		if(++counter == 2) {
			World.setZoneEmptyAt(Entity.coordsToIndex(x, y));
			World.entities.add(new EntityApple(x, y));
		}
	}
}