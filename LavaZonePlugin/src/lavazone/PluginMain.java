package lavazone;

import frutty.plugin.*;
import frutty.world.base.*;
import lavazone.zones.*;

@FruttyPlugin(name = "Frutty Lava Zone", version = "1.0.0", description = "Lava Zone", pluginSiteURL = "https://github.com/Degubi/Frutty/tree/master/FruttyEditor")
public final class PluginMain {
    
    @FruttyMain(eventClass = PluginMain.class)
    public static void pluginMain() {
        MapZoneBase.registerZone(new MapZoneLava());
    }
}