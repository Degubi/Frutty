package frutty.entity.zone;

import java.io.Serializable;

public abstract class EntityZone implements Serializable{
	private static final long serialVersionUID = -136000208701141435L;
	
	public final int x, y, zoneIndex;
	public boolean shouldUpdate;
	
	public EntityZone(boolean startWithUpdates, int xCoord, int yCoord, int zoIndex) {
		x = xCoord;
		y = yCoord;
		zoneIndex = zoIndex;
		this.shouldUpdate = startWithUpdates;
	}
	
	public void onNotified() {}
	public void update() {}
}