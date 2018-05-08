package frutty.entity;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import frutty.Main;
import frutty.map.MapZone;
import frutty.stuff.EnumFacing;

public final class EntityEnemy extends Entity {
	private static final BufferedImage[] fastTextures = {loadTexture("enemy/fast_side.png"), loadTexture("enemy/fast_front.png"), loadTexture("enemy/fast_back.png")};
	private static final BufferedImage[] normalTextures = {loadTexture("enemy/side.png"), loadTexture("enemy/front.png"), loadTexture("enemy/back.png")};
	
	private final int moveTick, updateTick;
	
	private int textureIndex;
	private boolean animSwitch;
	
	public EntityEnemy(int x, int y) {
		super(x, y);
		
		if(Main.rand.nextBoolean()) {
			moveTick = 10; updateTick = 20;
		}else{
			moveTick = 15; updateTick = 30;
		}
	}

	@Override
	public void render(Graphics graphics) {
		if(textureIndex == 3) {
			graphics.drawImage(moveTick == 10 ? fastTextures[0] : normalTextures[0], posX + 64, posY, -64, 64, null);
		}else{
			if(animSwitch || textureIndex == 0) {
				graphics.drawImage(moveTick == 10 ? fastTextures[textureIndex] : normalTextures[textureIndex], posX, posY, 64, 64, null);
			}else{
				graphics.drawImage(moveTick == 10 ? fastTextures[textureIndex] : normalTextures[textureIndex], posX + 64, posY, -64, 64, null);
			}
		}
	}

	@Override
	public void update(int ticks) {
		if(ticks % updateTick == 0) {
			checkPlayer(true);
			
			if(!MapZone.isEmpty(posX + motionX, posY + motionY)) {
				EnumFacing facing = findFreeFacing();
				motionX = facing.xOffset;
				motionY = facing.yOffset;
				textureIndex = facing.textureIndex;
			}
			
		}if(ticks % moveTick == 0) {
			posX += motionX / 2;
			posY += motionY / 2;
			animSwitch = !animSwitch;
		}
	}
}