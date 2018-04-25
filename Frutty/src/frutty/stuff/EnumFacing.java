package frutty.stuff;

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
}