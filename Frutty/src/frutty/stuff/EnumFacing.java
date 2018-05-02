package frutty.stuff;

import frutty.Main;

public enum EnumFacing {
	UP(0, -64, 2),
	DOWN(0, 64, 1),
	LEFT(-64, 0, 3),
	RIGHT(64, 0, 0);
	
	public final int xOffset, yOffset, textureIndex;
	private EnumFacing(int x, int y, int index) {
		xOffset = x;
		yOffset = y;
		textureIndex = index;
	}

	public static EnumFacing randomFacing() {
		switch(Main.rand.nextInt(4)) {
			case 0: return UP;
			case 1: return DOWN;
			case 2: return LEFT;
			default: return RIGHT;
		}
	}
}