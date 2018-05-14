package frutty.entity.effects;

import java.awt.Graphics;
import java.util.Iterator;

import frutty.entity.EntityPlayer;

public abstract class EntityEffect {	
	public int ticks;
	
	public EntityEffect(int duration) {
		ticks = duration * 10;
	}
	
	public void update(Iterator<EntityEffect> iterator) {
		if(--ticks == 0) {
			iterator.remove();
		}
	}
	
	public abstract void handleEffect(EntityPlayer player, Graphics graphics);
}