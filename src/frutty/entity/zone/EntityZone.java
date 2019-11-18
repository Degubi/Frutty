package frutty.entity.zone;

import java.io.*;

public abstract class EntityZone implements Serializable{
	private static final long serialVersionUID = -136000208701141435L;
	
	public final int x, y;
	public boolean needsUpdates;
	
	public EntityZone(boolean startWithUpdates, int xCoord, int yCoord) {
		x = xCoord;
		y = yCoord;
		needsUpdates = startWithUpdates;
	}
	
	public void onNotified() {}
	public void update() {}
}