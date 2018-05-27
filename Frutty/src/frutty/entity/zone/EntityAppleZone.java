package frutty.entity.zone;

import frutty.entity.EntityApple;
import frutty.map.Map;

public final class EntityAppleZone extends EntityZone{
	public boolean notified;
	private int counter;
	
	public EntityAppleZone(int xCoord, int yCoord, int zoneIndex) {
		super(xCoord, yCoord, zoneIndex);
	}
	
	@Override
	public void update() {
		if(notified){
			if(counter > 1) {
				Map.setZoneEmptyAt(zoneIndex);
				Map.entities.add(new EntityApple(x, y));
			}else{
				++counter;
			}
		}
	}
}