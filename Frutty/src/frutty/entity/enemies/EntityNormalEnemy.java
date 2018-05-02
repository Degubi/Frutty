package frutty.entity.enemies;

import java.awt.image.BufferedImage;

import frutty.entity.EntityPlayer;
import frutty.gui.GuiIngame;
import frutty.gui.GuiSettings;
import frutty.map.Map;
import frutty.map.MapZone;

public class EntityNormalEnemy extends EntityAbstractEnemy{
	private static final BufferedImage[] textures = {loadTexture("enemy/side.png"), loadTexture("enemy/front.png"), loadTexture("enemy/back.png")};

	public EntityNormalEnemy(int x, int y) {
		super(x, y);
	}

	@Override
	public void update(int ticks) {
		if(ticks % 30 == 0) {
			for(EntityPlayer player : Map.currentMap.players) {
				if((posY + motionY == player.posY && posX + motionX == player.posX) || (posX == player.posX && posY == player.posY)) {
					if(!GuiSettings.isGodEnabled()) {
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
			
		}if(ticks % 15 == 0) {
			renderPosX += motionX / 2;
			renderPosY += motionY / 2;
		}
	}

	@Override
	protected BufferedImage[] getTextures() {
		return textures;
	}
}