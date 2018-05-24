package frutty.entity.zone;

import java.io.Serializable;

public abstract class EntityZone implements Serializable{
	private static final long serialVersionUID = -136000208701141435L;
	
	public final int x, y, zoneIndex;
	public EntityZone(int xCoord, int yCoord, int zoIndex) {
		x = xCoord;
		y = yCoord;
		zoneIndex = zoIndex;
	}
	
	public abstract void update();
}