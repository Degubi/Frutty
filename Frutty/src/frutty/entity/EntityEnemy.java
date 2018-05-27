package frutty.entity;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import frutty.Main;
import frutty.map.Map;
import frutty.map.base.MapZone;

public final class EntityEnemy extends Entity {
	private static final BufferedImage[] fastTextures = {Main.loadTexture("enemy", "fast_side.png"), Main.loadTexture("enemy", "fast_front.png"), Main.loadTexture("enemy", "fast_back.png")};
	private static final BufferedImage[] normalTextures = {Main.loadTexture("enemy", "side.png"), Main.loadTexture("enemy", "front.png"), Main.loadTexture("enemy", "back.png")};
	
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
			graphics.drawImage(moveTick == 1 ? fastTextures[0] : normalTextures[0], renderPosX + 64, renderPosY, -64, 64, null);
		}else{
			if(animSwitch || textureIndex == 0) {
				graphics.drawImage(moveTick == 1 ? fastTextures[textureIndex] : normalTextures[textureIndex], renderPosX, renderPosY, 64, 64, null);
			}else{
				graphics.drawImage(moveTick == 1 ? fastTextures[textureIndex] : normalTextures[textureIndex], renderPosX + 64, renderPosY, -64, 64, null);
			}
		}
		
		super.render(graphics);
	}

	@Override
	public void update(int ticks) {
		if(ticks % moveTick == 0) {
			checkPlayers();
			if(ticks % updateTick == 0) {
				animSwitch = !animSwitch;
				
				if(serverPosX + motionX > Map.width || serverPosX + motionX < 0 || !MapZone.isEmptyAt(coordsToIndex(serverPosX + motionX, serverPosY + motionY))) {
					EnumFacing facing = findFreeFacing();
					motionX = facing.xOffset;
					motionY = facing.yOffset;
					textureIndex = facing.textureIndex;
				}
				
				serverPosX += motionX;
				serverPosY += motionY;
			}
		
		renderPosX += motionX / 16;
		renderPosY += motionY / 16;
		}
	}
}