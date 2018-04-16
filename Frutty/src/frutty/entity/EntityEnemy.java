package frutty.entity;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import frutty.gui.GuiIngame;
import frutty.map.Map;
import frutty.map.MapZone;
import frutty.stuff.EnumFacing;

/**
 * Az ellens�ges NPC-k class f�jlja, az update f�ggv�ny m�rc. 28 �ta "�rthet�bb �s olvashat�bb"
 */
public class EntityEnemy extends Entity{
	private static final BufferedImage[] textures = {loadTexture("enemy/side.png"), loadTexture("enemy/front.png"), loadTexture("enemy/back.png")};
	
	private int textureIndex, renderPosX, renderPosY;
	
	public EntityEnemy(int x, int y) {
		super(x, y);
		
		int rng = GuiIngame.rand.nextInt(4);
		if(rng == 0) {
			setFacing(EnumFacing.DOWN);
		}else if(rng == 1) {
			setFacing(EnumFacing.RIGHT);
		}else if(rng == 2) {
			setFacing(EnumFacing.LEFT);
		}else{
			setFacing(EnumFacing.UP);
		}
		
		renderPosX = x - motionX / 2;
		renderPosY = y - motionY / 2;
	}

	private void setFacing(EnumFacing facing) {
		if(facing == EnumFacing.DOWN) {
			motionX = 0;
			motionY = 64;
			textureIndex = 1;
		}else if(facing == EnumFacing.UP) {
			motionX = 0;
			motionY = -64;
			textureIndex = 2;
		}else if(facing == EnumFacing.LEFT) {
			motionX = -64;
			motionY = 0;
			textureIndex = 3;
		}else{
			motionX = 64;
			motionY = 0;
			textureIndex = 0;
		}
	}
	
	@Override
	public void render(Graphics graphics) {
		if(textureIndex == 3) {
			graphics.drawImage(textures[0], renderPosX + 64, renderPosY, -64, 64, null);
		}else{
			graphics.drawImage(textures[textureIndex], renderPosX, renderPosY, null);
		}
	}
	
	@Override
	public void update(int ticks) {
		if(ticks % 20 == 0) {
			for(EntityPlayer player : Map.getPlayers()) {
				if((posY + motionY == player.posY && posX + motionX == player.posX) || (posX == player.posX && posY == player.posY)) {
					GuiIngame.showMessageAndClose("Game over!");
				}
			}
			
			int nextPosX = posX + motionX, nextPosY = posY + motionY;
			if(!MapZone.isEmpty(nextPosX, nextPosY)) {
				for(int rotat = motionY == 64 ? 1 : 3; ; rotat = rand.nextInt(4)) {
					if(rotat == 0 && MapZone.isEmpty(posX + 64, posY)) {
						setFacing(EnumFacing.RIGHT);
						break;
					}else if(rotat == 1 && MapZone.isEmpty(posX - 64, posY)) {
						setFacing(EnumFacing.LEFT);
						break;
					}else if(rotat == 2 && MapZone.isEmpty(posX, posY + 64)) {
						setFacing(EnumFacing.DOWN);
						break;
					}else if(rotat == 3 && MapZone.isEmpty(posX, posY - 64)){
						setFacing(EnumFacing.UP);
						break;
					}
				}
			}
			posX += motionX;
			posY += motionY;
		}
		
		if(ticks % 10 == 0) {
			renderPosX += motionX / 2;
			renderPosY += motionY / 2;
		}
	}
}