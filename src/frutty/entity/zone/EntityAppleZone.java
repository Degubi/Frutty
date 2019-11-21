package frutty.entity.zone;

import frutty.entity.*;
import frutty.world.*;

public final class EntityAppleZone extends EntityZone{
	private int delayCounter;
	
	public EntityAppleZone() {
		super(false);
	}
	
	@Override
	public void onNotified() {
		needsUpdates = true;
	}
	
	@Override
	public void update(int zoneIndex, int x, int y) {
		if(++delayCounter == 2) {
			World.setZoneEmptyAt(x, y);
			World.entities.add(new EntityApple(x, y));
		}
	}
}