package frutty.entity;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import frutty.gui.GuiSettings.Settings;
import frutty.map.Map;
import frutty.map.MapZone;
import frutty.stuff.EnumFacing;

public final class EntityPlayer extends Entity implements KeyListener, MouseListener{
	private static final BufferedImage[] textures = {loadTexture("player/side.png"), loadTexture("player/front.png"), loadTexture("player/back.png")};
	
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
		if(!Map.getZoneAtPos(x, y).isPassable()) {
			return false;
		}
		
		for(Entity entities : Map.currentMap.entities) {
			if(entities instanceof EntityBall == false && entities.posX == x && entities.posY == y) {
				return false;
			}
		}
		return true;
	}
	
	private void setFacing(EnumFacing facing) {
		currentFacing = facing;
		posX += facing.xOffset;
		posY += facing.yOffset;
		textureIndex = facing.textureIndex;
		
		Map.getZoneAtPos(posX, posY).onBreak();
		lastPressTime = System.currentTimeMillis();
	}
	
	@Override
	public void keyPressed(KeyEvent event) {
		if(System.currentTimeMillis() - lastPressTime > 100L) {
			if(event.getKeyCode() == upKey && posY > 0 && isFree(posX, posY - 64)) {
				setFacing(EnumFacing.UP);
			}else if(event.getKeyCode() == downKey && posY < Map.currentMap.height && isFree(posX, posY + 64)) {
				setFacing(EnumFacing.DOWN);
			}else if(event.getKeyCode() == leftKey && posX > 0 && isFree(posX - 64, posY)) {
				setFacing(EnumFacing.LEFT);
			}else if(event.getKeyCode() == rightKey && posX < Map.currentMap.width && isFree(posX + 64, posY)) {
				setFacing(EnumFacing.RIGHT);
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		if(canShootBall && event.getX() < Map.currentMap.width + 64 && MapZone.isEmpty(posX + currentFacing.xOffset, posY + currentFacing.yOffset)) {
			Map.getBall().activate(posX, posY, currentFacing);
		}
	}
	
	@Override
	public void render(Graphics graphics) {
		if(textureIndex == 3) {
			graphics.drawImage(textures[0], posX + 64, posY, -64, 64, null);
		}else{
			graphics.drawImage(textures[textureIndex], posX, posY, null);
		}
	}
	
	@Override public void update(int ticks) {}
	@Override public void mouseClicked(MouseEvent e) {}
	@Override public void mousePressed(MouseEvent e) {}
	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}
	@Override public void keyTyped(KeyEvent e) {}
	@Override public void keyReleased(KeyEvent e) {}
}