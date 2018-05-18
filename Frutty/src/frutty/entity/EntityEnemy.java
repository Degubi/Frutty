package frutty.entity;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import frutty.Main;
import frutty.map.MapZone;

public final class EntityEnemy extends Entity {
	private static final BufferedImage[] fastTextures = {loadTexture("enemy/fast_side.png"), loadTexture("enemy/fast_front.png"), loadTexture("enemy/fast_back.png")};
	private static final BufferedImage[] normalTextures = {loadTexture("enemy/side.png"), loadTexture("enemy/front.png"), loadTexture("enemy/back.png")};
	
	private final int moveTick, updateTick;
	
	private int textureIndex;
	private boolean animSwitch;
	
	public EntityEnemy(int x, int y) {
		super(x, y);
		
		if(Main.rand.nextBoolean()) {
			moveTick = 1; updateTick = 16;
		}else{
			moveTick = 2; updateTick = 32;
		}
		
		serverPosX = x;
		serverPosY = y;
	}

	@Override
	public void render(Graphics graphics) {
		if(textureIndex == 3) {
			graphics.drawImage(moveTick == 1 ? fastTextures[0] : normalTextures[0], posX + 64, posY, -64, 64, null);
		}else{
			if(animSwitch || textureIndex == 0) {
				graphics.drawImage(moveTick == 1 ? fastTextures[textureIndex] : normalTextures[textureIndex], posX, posY, 64, 64, null);
			}else{
				graphics.drawImage(moveTick == 1 ? fastTextures[textureIndex] : normalTextures[textureIndex], posX + 64, posY, -64, 64, null);
			}
		}
		
		super.render(graphics);
	}

	@Override
	public void update(int ticks) {
		if(ticks % moveTick == 0) {
			if(ticks % updateTick == 0) {
				checkPlayer(true);
				animSwitch = !animSwitch;
				
				if(!MapZone.isEmpty(posX + motionX, posY + motionY)) {
					EnumFacing facing = findFreeFacing();
					motionX = facing.xOffset;
					motionY = facing.yOffset;
					textureIndex = facing.textureIndex;
				}
				
				serverPosX += motionX;
				serverPosY += motionY;
			}
		
		posX += motionX / 16;
		posY += motionY / 16;
		}
	}
}