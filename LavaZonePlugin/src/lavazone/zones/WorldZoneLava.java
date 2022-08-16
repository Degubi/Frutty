package lavazone.zones;

import frutty.entity.living.*;
import frutty.tools.*;
import frutty.world.*;

public final class WorldZoneLava extends WorldZoneFluid {

    public WorldZoneLava() {
        super("lava:lavaZone", Material.loadTexture("world/special/lava.png"));
    }

    @Override
    public void onZoneEntered(int x, int y, Material material, EntityPlayer player) {
        player.onKilled(null);

        super.onZoneEntered(x, y, material, player);
    }
}