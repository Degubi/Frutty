package frutty.entity.enemy;

import frutty.tools.*;
import frutty.world.*;
import java.awt.image.*;
import java.util.*;

public final class EntityFastEnemy extends EntityEnemy {
    private static final BufferedImage[] textures = Material.loadTextures("enemy", "fast_side.png", "fast_front.png", "fast_back.png");

    public EntityFastEnemy(int x, int y) {
        super(x, y, textures);
    }

    @Override
    public void updateServer() {
        super.updateServer();

        var serverPosX = this.serverPosX;
        var serverPosY = this.serverPosY;

        if(!World.isPositionFree(serverPosX + motionX, serverPosY + motionY)) {
            var facing = findFreeFacing();
            motionX = facing.xOffset;
            motionY = facing.yOffset;
            textureIndex = facing.textureIndex;

            Arrays.fill(World.isActivePathfindingZone, false);
            World.isActivePathfindingZone[World.worldCoordsToZoneIndex(serverPosX + facing.xOffset, serverPosY + facing.yOffset)] = true;
        }
    }


    @Override
    public int getClientUpdateRate() { return 1; }
    @Override
    public int getServerUpdateRate() { return 16; }
}