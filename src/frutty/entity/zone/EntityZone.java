package frutty.entity.zone;

import java.io.*;

public abstract class EntityZone implements Serializable{
    private static final long serialVersionUID = -136000208701141435L;
    
    public boolean needsUpdates;
    
    public EntityZone(boolean startWithUpdates) {
        needsUpdates = startWithUpdates;
    }
    
    public void onNotified() {}
    
    @SuppressWarnings("unused")
    public void update(int zoneIndex, int x, int y) {}
}