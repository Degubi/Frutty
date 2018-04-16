package frutty.entity;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import frutty.gui.GuiIngame;
import frutty.gui.GuiStats;
import frutty.map.Map;
import frutty.map.MapZone;
import frutty.map.zones.MapZoneFruit;
import frutty.map.zones.MapZoneNormal;
import frutty.map.zones.MapZoneSpawner;
import frutty.stuff.EnumFruit;

/**
 * Játékos class, a mozgás itt kezelõdik le, illetve a labdadobás is. + render
 */
public class EntityPlayer extends Entity implements KeyListener, MouseListener{
	private static final BufferedImage[] textures = {loadTexture("player/side.png"), loadTexture("player/front.png"), loadTexture("player/back.png")};
	
	private int textureIndex, leftKey, rightKey, upKey, downKey;
	private long lastPressTime;
	
	public EntityPlayer(int x, int y, boolean isFirstPlayer) {
		super(x, y);
		
		if(isFirstPlayer) {
			leftKey = KeyEvent.VK_LEFT;
			rightKey = KeyEvent.VK_RIGHT;
			upKey = KeyEvent.VK_UP;
			downKey = KeyEvent.VK_DOWN;
		}else {
			leftKey = KeyEvent.VK_A;
			rightKey = KeyEvent.VK_D;
			upKey = KeyEvent.VK_W;
			downKey = KeyEvent.VK_S;
		}
	}

	/**
	 * Ellenõrzés a játékos által lépendõ zónára hogy arra léphet e egyáltalán.
	 * @param x Y koordináta
	 * @param y X koordináta
	 * @return True ha a zóna szabad, egyébként false
	 */
	private static boolean isFree(int x, int y) {
		for(MapZone zone : Map.currentMap.zones) {
			if(zone.posX == x && zone.posY == y && (zone instanceof MapZoneSpawner || (zone instanceof MapZoneFruit && ((MapZoneFruit)zone).fruitType == EnumFruit.APPLE))) {
				return false;
			}
		}
		
		for(Entity entities : Map.currentMap.entities) {
			if(entities instanceof EntityBall == false && entities.posX == x && entities.posY == y) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public void keyPressed(KeyEvent event) {
		if(System.currentTimeMillis() - lastPressTime > 100L) {
			if(event.getKeyCode() == upKey && posY > 0 && isFree(posX, posY - 64)) {
				textureIndex = 2;
				posY -= 64;
			}else if(event.getKeyCode() == downKey && posY < Map.currentMap.height && isFree(posX, posY + 64)) {
				textureIndex = 1;
				posY += 64;
			}else if(event.getKeyCode() == leftKey && posX > 0 && isFree(posX - 64, posY)) {
				textureIndex = 3;
				posX -= 64;
			}else if(event.getKeyCode() == rightKey && posX < Map.currentMap.width && isFree(posX + 64, posY)) {
				textureIndex = 0;
				posX += 64;
			}
			
			MapZone currentZone = Map.getZoneAtPos(posX, posY);
			if(currentZone instanceof MapZoneNormal) {
				currentZone.onBreak();
			}else if(currentZone instanceof MapZoneFruit) {
				Map.currentMap.score += 50;
				currentZone.onBreak();
						
				if(--Map.currentMap.pickCount == 0) {  //Main loopból kiszedve, nem kell framenként nézni
					GuiIngame.showMessageAndClose("You won!");
					GuiStats.compareScores();
				}
			}
			lastPressTime = System.currentTimeMillis();
		}
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		if(event.getX() < Map.currentMap.width + 64) {
			EntityBall ball = Map.getBall();
			if(!ball.active) {
				ball.activate(posX, posY, textureIndex);
			}
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

	
	public void update(int ticks) {}
	public void mouseClicked(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void keyTyped(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {}
}