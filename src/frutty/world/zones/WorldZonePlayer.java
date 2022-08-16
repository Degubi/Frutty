package frutty.world.zones;

import frutty.entity.living.*;
import frutty.tools.*;
import frutty.world.*;
import java.awt.*;
import javax.swing.*;

public final class WorldZonePlayer extends WorldZone implements InternalZone {
    private final int playerID;

    public WorldZonePlayer(int id) {
        super(id == 1 ? "player1Zone" : "player2Zone");

        this.playerID = id;
    }

    @Override
    protected ImageIcon getEditorIcon() {
        return new ImageIcon(Material.loadTexture("dev/player" + playerID + ".png"));
    }

    @Override
    public void render(int x, int y, Material material, Graphics graphics) {}

    @Override
    public void onZoneAdded(boolean isCoop, int zoneCount, int x, int y) {
        if(playerID == 1) {
            World.players[0] = new EntityPlayer(x, y, true);
        }

        if(isCoop && playerID == 2) {
            World.players[1] = new EntityPlayer(x, y, false);
        }
    }

    @Override
    public WorldZone getReplacementZone() {
        return WorldZone.emptyZone;
    }
}