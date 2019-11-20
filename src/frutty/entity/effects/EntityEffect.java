package frutty.entity.effects;

import frutty.entity.*;
import java.awt.*;
import java.util.*;

public abstract class EntityEffect{	
	public int ticks;
	
	public EntityEffect(int duration) {
		ticks = duration;
	}
	
	public final void update(Iterator<EntityEffect> iterator) {
		if(--ticks == 0) {
			iterator.remove();
		}
	}
	
	public abstract void renderEffect(EntityPlayer player, Graphics graphics);
}