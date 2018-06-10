package frutty.entity;

import frutty.map.MapZone;

public abstract class EntityFalling extends Entity{
	private int sleepCounter = 0;

	public EntityFalling(int x, int y) {
		super(x, y);
	}

	@Override
	public int getClientUpdate() { return 1; }
	@Override
	public int getServerUpdate() { return 8; }
	
	@Override
	public void updateServer() {
		if(MapZone.isEmptyAt(coordsToIndex(renderPosX, serverPosY + 64))) {
			if(sleepCounter == 0) {
				motionY = 64;
			}else{
				--sleepCounter;
			}
		}else{
			motionY = 0;
			sleepCounter = 5;
		}
	}
}