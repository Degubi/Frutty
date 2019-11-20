package frutty.entity;

import frutty.entity.effects.*;
import frutty.gui.*;
import frutty.gui.GuiSettings.*;
import frutty.tools.*;
import frutty.world.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;

public final class EntityPlayer extends Entity implements KeyListener{
	private static final BufferedImage[] textures = Material.loadTextures("player", "side.png", "front.png", "back.png");
	public final ArrayList<EntityEffect> entityEffects = new ArrayList<>();
	
	private int textureIndex;
	private long lastPressTime;
	public boolean hidden = false;
	
	private final int leftKey, rightKey, upKey, downKey;
	
	public EntityPlayer(int x, int y, boolean isFirst) {
		super(x, y);
		
		if(isFirst){
			leftKey = KeyEvent.VK_LEFT;
			rightKey = KeyEvent.VK_RIGHT;
			upKey = KeyEvent.VK_UP;
			downKey = KeyEvent.VK_DOWN;
		}else{
			leftKey = Settings.leftKey;
			rightKey = Settings.rightKey;
			upKey = Settings.upKey;
			downKey = Settings.downKey;
		}
	}

	private static boolean isPlayerFree(int x, int y) {
		if(!World.getZoneAtIndex(World.coordsToIndex(x, y)).canPlayerPass(x, y)) {
			return false;
		}
		
		for(Entity entities : World.entities) {
			if(entities.renderPosX == x && entities.renderPosY == y) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public void onKilled(Entity killer) {
		GuiIngame.showMessageAndClose("Game over!");
	}
	
	private void setFacing(EnumFacing facing) {
		renderPosX += facing.xOffset;
		renderPosY += facing.yOffset;
		serverPosX += facing.xOffset;
		serverPosY += facing.yOffset;
		textureIndex = facing.textureIndex;
		
		var zoneIndex = World.coordsToIndex(renderPosX, renderPosY);
		World.zones[zoneIndex].onZoneEntered(renderPosX, renderPosY, zoneIndex, World.materials[zoneIndex], this);
		lastPressTime = System.currentTimeMillis();
	}
	
	@Override
	public void keyPressed(KeyEvent event) {
		if(System.currentTimeMillis() - lastPressTime > 100L) {
			if(event.getKeyCode() == upKey && renderPosY > 0 && isPlayerFree(renderPosX, renderPosY - 64)) {
				setFacing(EnumFacing.UP);
			}else if(event.getKeyCode() == downKey && renderPosY < World.height && isPlayerFree(renderPosX, renderPosY + 64)) {
				setFacing(EnumFacing.DOWN);
			}else if(event.getKeyCode() == leftKey && renderPosX > 0 && isPlayerFree(renderPosX - 64, renderPosY)) {
				setFacing(EnumFacing.LEFT);
			}else if(event.getKeyCode() == rightKey && renderPosX < World.width && isPlayerFree(renderPosX + 64, renderPosY)) {
				setFacing(EnumFacing.RIGHT);
			}
		}
	}
	
	@Override
	public void render(Graphics graphics) {
		if(textureIndex == 3) {
			graphics.drawImage(textures[0], renderPosX + 64, renderPosY, -64, 64, null);
		}else{
			graphics.drawImage(textures[textureIndex], renderPosX, renderPosY, null);
		}
		
		for(var effects : entityEffects) {
			effects.renderEffect(this, graphics);
		}
	}
	
	public boolean isInvicible() {
		for(var effects : entityEffects) {
			if(effects instanceof EntityEffectInvisible) {
				return true;
			}
		}
		return false;
	}

	@Override public void keyTyped(KeyEvent e) {} @Override public void keyReleased(KeyEvent e) {}

	@Override
	public void updateClient() {}

	@Override
	public void updateServer() {
		for(var iterator = entityEffects.iterator(); iterator.hasNext();) {
			iterator.next().update(iterator);
		}		
	}

	@Override
	public int getClientUpdateRate() {
		return 1;
	}

	@Override
	public int getServerUpdateRate() {
		return 32;
	}
}