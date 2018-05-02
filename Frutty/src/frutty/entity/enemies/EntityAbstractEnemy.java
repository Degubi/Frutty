package frutty.entity.enemies;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import frutty.Main;
import frutty.entity.Entity;
import frutty.stuff.EnumFacing;

public abstract class EntityAbstractEnemy extends Entity{
	protected int textureIndex, renderPosX, renderPosY;
	
	public EntityAbstractEnemy(int x, int y) {
		super(x, y);
		
		switch(Main.rand.nextInt(4)) {
			case 0: setFacing(EnumFacing.DOWN); break;
			case 1: setFacing(EnumFacing.RIGHT); break;
			case 2: setFacing(EnumFacing.LEFT); break;
			default: setFacing(EnumFacing.UP);
		}
		
		renderPosX = x - motionX / 2;
		renderPosY = y - motionY / 2;
	}

	@Override
	public void render(Graphics graphics) {
		if(textureIndex == 3) {
			graphics.drawImage(getTextures()[0], renderPosX + 64, renderPosY, -64, 64, null);
		}else{
			graphics.drawImage(getTextures()[textureIndex], renderPosX, renderPosY, null);
		}
	}
	
	protected abstract BufferedImage[] getTextures();
	
	protected void setFacing(EnumFacing facing) {
		motionX = facing.xOffset;
		motionY = facing.yOffset;
		textureIndex = facing.textureIndex;
	}
}