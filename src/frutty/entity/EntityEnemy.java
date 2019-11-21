package frutty.entity;

import frutty.*;
import frutty.tools.*;
import frutty.world.*;
import java.awt.*;
import java.awt.image.*;

public final class EntityEnemy extends Entity {
	private static final BufferedImage[] fastTextures = Material.loadTextures("enemy", "fast_side.png", "fast_front.png", "fast_back.png");
	private static final BufferedImage[] normalTextures = Material.loadTextures("enemy", "side.png", "front.png", "back.png");
	
	private int textureIndex;
	private boolean animSwitch;
	private final boolean isFast = Main.rand.nextBoolean();
	public boolean active = true;

	public EntityEnemy(int x, int y) {
		super(x, y);
	}

	@Override
	public void render(Graphics graphics) {
		if(textureIndex == 3) {
			graphics.drawImage(isFast ? fastTextures[0] : normalTextures[0], renderPosX + 64, renderPosY, -64, 64, null);
		}else{
			if(animSwitch || textureIndex == 0) {
				graphics.drawImage(isFast ? fastTextures[textureIndex] : normalTextures[textureIndex], renderPosX, renderPosY, 64, 64, null);
			}else{
				graphics.drawImage(isFast ? fastTextures[textureIndex] : normalTextures[textureIndex], renderPosX + 64, renderPosY, -64, 64, null);
			}
		}
	}

	@Override
	public void onKilled(Entity killer) {
		if(killer instanceof EntityPlayer) {
			World.score += 100;
		}
		active = false;
	}
	
	@Override
	public void updateClient() {
		checkPlayers();
	}

	@Override
	public void updateServer() {
		animSwitch = !animSwitch;
		
		if(!World.isPositionFree(serverPosX + motionX, serverPosY + motionY)) {
			EnumFacing facing = findFreeFacing();
			motionX = facing.xOffset;
			motionY = facing.yOffset;
			textureIndex = facing.textureIndex;
		}
	}

	@Override
	public int getClientUpdateRate() { return isFast ? 1 : 2; }
	@Override
	public int getServerUpdateRate() { return isFast ? 16 : 32; }
}