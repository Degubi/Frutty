package frutty.entity.zone;

import frutty.entity.EntityApple;
import frutty.world.World;

public final class EntityAppleZone extends EntityZone{
	private int counter;
	
	public EntityAppleZone(int xCoord, int yCoord, int zoneIndex) {
		super(false, xCoord, yCoord, zoneIndex);
	}
	
	@Override
	public void onNotified() {
		shouldUpdate = true;
	}
	
	@Override
	public void update() {
		if(++counter == 2) {
			World.setZoneEmptyAt(zoneIndex);
			World.entities.add(new EntityApple(x, y));
		}
	}
}