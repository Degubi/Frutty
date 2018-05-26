package frutty.entity.effects;

import java.awt.Color;
import java.awt.Graphics;

import frutty.entity.EntityPlayer;

public final class EntityEffectInvisible extends EntityEffect {
	private boolean animSwitch = false;
	
	public EntityEffectInvisible() {
		super(75);
	}
	
	@Override
	public void handleEffect(EntityPlayer player, Graphics graphics) {
		if(animSwitch) {
			graphics.setColor(Color.BLACK);
			graphics.fillRect(player.renderPosX, player.renderPosY, 64, 64);
		}
		animSwitch = !animSwitch;
	}
}