package frutty.world.zones;

import frutty.entity.*;
import frutty.tools.*;
import frutty.world.*;
import frutty.world.base.*;
import java.awt.*;
import javax.swing.*;

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
    public void render(int x, int y, Material material, Graphics graphics) {}

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