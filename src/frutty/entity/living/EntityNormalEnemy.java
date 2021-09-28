package frutty.entity.living;

import frutty.tools.*;
import frutty.world.*;
import java.awt.image.*;
import java.util.*;

public final class EntityNormalEnemy extends EntityEnemy {
    private static final BufferedImage[] textures = Material.loadTextures("enemy/normal", "side.png", "front.png", "back.png");

    public EntityNormalEnemy(int x, int y) {
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
    public int getClientUpdateRate() { return 2; }
    @Override
    public int getServerUpdateRate() { return 32; }
}