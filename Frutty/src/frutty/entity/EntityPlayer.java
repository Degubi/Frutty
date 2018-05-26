package frutty.entity;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

import frutty.Main;
import frutty.entity.effects.EntityEffect;
import frutty.entity.effects.EntityEffectInvisible;
import frutty.gui.GuiSettings.Settings;
import frutty.map.Map;
import frutty.map.base.MapZone;

public final class EntityPlayer extends Entity implements KeyListener, MouseListener{
	private static final BufferedImage[] textures = {Main.loadTexture("player", "side.png"), Main.loadTexture("player", "front.png"), Main.loadTexture("player", "back.png")};
	public final ArrayList<EntityEffect> entityEffects = new ArrayList<>();
	
	private int textureIndex;
	private long lastPressTime;
	private EnumFacing currentFacing;
	
	private final int leftKey, rightKey, upKey, downKey;
	private final boolean canShootBall;
	
	public EntityPlayer(int x, int y, boolean isFirst) {
		super(x, y);
		
		canShootBall = isFirst;
		
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
		
		currentFacing = EnumFacing.RIGHT;
	}

	private static boolean isFree(int x, int y) {
		if(!Map.getZoneAtIndex(coordsToIndex(x, y)).isBreakable(x, y)) {
			return false;
		}
		
		for(Entity entities : Map.currentMap.entities) {
			if(entities instanceof EntityBall == false && entities.renderPosX == x && entities.renderPosY == y) {
				return false;
			}
		}
		return true;
	}
	
	private void setFacing(EnumFacing facing) {
		currentFacing = facing;
		renderPosX += facing.xOffset;
		renderPosY += facing.yOffset;
		serverPosX += facing.xOffset;
		serverPosY += facing.yOffset;
		textureIndex = facing.textureIndex;
		
		int zoneIndex = currentPosToIndex();
		Map.currentMap.zones[zoneIndex].onBreak(renderPosX, renderPosY, zoneIndex, Map.currentMap.textureData[zoneIndex], this);
		lastPressTime = System.currentTimeMillis();
	}
	
	@Override
	public void keyPressed(KeyEvent event) {
		if(System.currentTimeMillis() - lastPressTime > 100L) {
			if(event.getKeyCode() == upKey && renderPosY > 0 && isFree(renderPosX, renderPosY - 64)) {
				setFacing(EnumFacing.UP);
			}else if(event.getKeyCode() == downKey && renderPosY < Map.currentMap.height && isFree(renderPosX, renderPosY + 64)) {
				setFacing(EnumFacing.DOWN);
			}else if(event.getKeyCode() == leftKey && renderPosX > 0 && isFree(renderPosX - 64, renderPosY)) {
				setFacing(EnumFacing.LEFT);
			}else if(event.getKeyCode() == rightKey && renderPosX < Map.currentMap.width && isFree(renderPosX + 64, renderPosY)) {
				setFacing(EnumFacing.RIGHT);
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		if(canShootBall && event.getX() < Map.currentMap.width + 64 && MapZone.isEmpty(renderPosX + currentFacing.xOffset, renderPosY + currentFacing.yOffset)) {
			((EntityBall)Map.currentMap.entities.get(0)).activate(renderPosX, renderPosY, currentFacing);
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
		super.render(graphics);
	}
	
	public boolean isInvicible() {
		for(EntityEffect effects : entityEffects) {
			if(effects instanceof EntityEffectInvisible) {
				return true;
			}
		}
		return false;
	}
	
	@Override 
	public void update(int ticks) {
		for(Iterator<EntityEffect> iterator = entityEffects.iterator(); iterator.hasNext();) {
			iterator.next().update(iterator);
		}
	}
	
	@Override public void mouseClicked(MouseEvent e) {} @Override public void mousePressed(MouseEvent e) {} @Override public void mouseEntered(MouseEvent e) {} @Override public void mouseExited(MouseEvent e) {} @Override public void keyTyped(KeyEvent e) {} @Override public void keyReleased(KeyEvent e) {}
}