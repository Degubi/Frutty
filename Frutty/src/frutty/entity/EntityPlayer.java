package frutty.entity;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

import frutty.Main;
import frutty.entity.effects.EntityEffect;
import frutty.entity.effects.EntityEffectInvisible;
import frutty.gui.Settings;
import frutty.map.Map;

public final class EntityPlayer extends Entity implements KeyListener{
	private static final BufferedImage[] textures = {Main.loadTexture("player", "side.png"), Main.loadTexture("player", "front.png"), Main.loadTexture("player", "back.png")};
	public final ArrayList<EntityEffect> entityEffects = new ArrayList<>();
	
	private int textureIndex;
	private long lastPressTime;
	
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

	private static boolean isFree(int x, int y) {
		if(!Map.getZoneAtIndex(coordsToIndex(x, y)).isBreakable(x, y)) {
			return false;
		}
		
		for(Entity entities : Map.entities) {
			if(entities.renderPosX == x && entities.renderPosY == y) {
				return false;
			}
		}
		return true;
	}
	
	private void setFacing(EnumFacing facing) {
		renderPosX += facing.xOffset;
		renderPosY += facing.yOffset;
		serverPosX += facing.xOffset;
		serverPosY += facing.yOffset;
		textureIndex = facing.textureIndex;
		
		int zoneIndex = coordsToIndex(renderPosX, renderPosY);
		Map.zones[zoneIndex].onBreak(renderPosX, renderPosY, zoneIndex, Map.textureData[zoneIndex], this);
		lastPressTime = System.currentTimeMillis();
	}
	
	@Override
	public void keyPressed(KeyEvent event) {
		if(System.currentTimeMillis() - lastPressTime > 100L) {
			if(event.getKeyCode() == upKey && renderPosY > 0 && isFree(renderPosX, renderPosY - 64)) {
				setFacing(EnumFacing.UP);
			}else if(event.getKeyCode() == downKey && renderPosY < Map.height && isFree(renderPosX, renderPosY + 64)) {
				setFacing(EnumFacing.DOWN);
			}else if(event.getKeyCode() == leftKey && renderPosX > 0 && isFree(renderPosX - 64, renderPosY)) {
				setFacing(EnumFacing.LEFT);
			}else if(event.getKeyCode() == rightKey && renderPosX < Map.width && isFree(renderPosX + 64, renderPosY)) {
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
		
		for(EntityEffect effects : entityEffects) {
			effects.handleEffect(this, graphics);
		}
	}
	
	public boolean isInvicible() {
		for(EntityEffect effects : entityEffects) {
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
		for(Iterator<EntityEffect> iterator = entityEffects.iterator(); iterator.hasNext();) {
			iterator.next().update(iterator);
		}		
	}

	@Override
	public int getClientUpdate() {
		return 1;
	}

	@Override
	public int getServerUpdate() {
		return 32;
	}
}