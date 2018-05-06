package frutty.entity.enemies;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import frutty.entity.Entity;
import frutty.stuff.EnumFacing;

public abstract class EntityAbstractEnemy extends Entity{
	protected int textureIndex;
	protected boolean animSwitch;
	
	public EntityAbstractEnemy(int x, int y) {
		super(x, y);
	}

	@Override
	public void render(Graphics graphics) {
		if(textureIndex == 3) {
			graphics.drawImage(getTextures()[0], posX + 64, posY, -64, 64, null);
		}else{
			if(animSwitch || textureIndex == 0) {
				graphics.drawImage(getTextures()[textureIndex], posX, posY, 64, 64, null);
			}else{
				graphics.drawImage(getTextures()[textureIndex], posX + 64, posY, -64, 64, null);
			}
		}
	}
	
	protected abstract BufferedImage[] getTextures();
	
	protected void setFacing(EnumFacing facing) {
		motionX = facing.xOffset;
		motionY = facing.yOffset;
		textureIndex = facing.textureIndex;
	}
}