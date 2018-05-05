package frutty.entity.enemies;

import java.awt.image.BufferedImage;

import frutty.entity.EntityPlayer;
import frutty.gui.GuiIngame;
import frutty.gui.GuiSettings.Settings;
import frutty.map.Map;
import frutty.map.MapZone;

public class EntityFastEnemy extends EntityAbstractEnemy{
	private static final BufferedImage[] textures = {loadTexture("enemy/fast_side.png"), loadTexture("enemy/fast_front.png"), loadTexture("enemy/fast_back.png")};

	public EntityFastEnemy(int x, int y) {
		super(x, y);
	}

	@Override
	public void update(int ticks) {
		if(ticks % 20 == 0) {
			for(EntityPlayer player : Map.currentMap.players) {
				if((posY + motionY == player.posY && posX + motionX == player.posX) || (posX == player.posX && posY == player.posY)) {
					if(!Settings.godEnabled) {
						GuiIngame.showMessageAndClose("Game over!");
					}else{
						active = false;
						Map.currentMap.score += 100;
					}
				}
			}
			
			if(!MapZone.isEmpty(posX + motionX, posY + motionY)) {
				setFacing(findFreeFacing());
			}
			
			posX += motionX;
			posY += motionY;
			
		}if(ticks % 10 == 0) {
			renderPosX += motionX / 2;
			renderPosY += motionY / 2;
		}
	}

	@Override
	protected BufferedImage[] getTextures() {
		return textures;
	}
}