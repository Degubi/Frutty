/**Module used for plugin development*/
module frutty.api{
    requires transitive java.desktop;
    requires static java.compiler;

    exports frutty.gui;
    exports frutty.gui.components;

    exports frutty.entity;
    exports frutty.entity.effects;
    exports frutty.entity.enemy;
    exports frutty.entity.zone;

    exports frutty.plugin;
    exports frutty.plugin.event;
    exports frutty.plugin.event.entity;
    exports frutty.plugin.event.gui;
    exports frutty.plugin.event.stats;
    exports frutty.plugin.event.world;

    exports frutty.sound;
    exports frutty.tools;

    exports frutty.world;
    exports frutty.world.zones;
    exports frutty.world.base;
}