package frutty.world.zones;

import java.awt.Graphics2D;

import javax.swing.ImageIcon;

import frutty.entity.EntityPlayer;
import frutty.tools.Material;
import frutty.world.World;
import frutty.world.base.IInternalZone;
import frutty.world.base.MapZoneBase;

public final class MapZonePlayer extends MapZoneBase implements IInternalZone{
	private final int playerID;
	
	public MapZonePlayer(int id) {
		super(id == 1 ? "player1Zone" : "player2Zone");
		playerID = id;
	}
	
	@Override
	protected ImageIcon getEditorIcon() {
		return new ImageIcon("./textures/dev/player" + playerID + ".png");
	}

	@Override
	public void draw(int x, int y, Material material, Graphics2D graphics) {}

	@Override
	public void onZoneAdded(boolean isCoop, int x, int y) {
		if(isCoop) {
			if(playerID == 1) {
				World.players[0] = new EntityPlayer(x, y, true);
			}else{
				World.players[1] = new EntityPlayer(x, y, false);
			}
		}else{
			if(playerID == 1) {
				World.players[0] = new EntityPlayer(x, y, true);
			}
		}
	}
	
	@Override
	public MapZoneBase getReplacementZone() {
		return MapZoneBase.emptyZone;
	}
}