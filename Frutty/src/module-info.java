module frutty.api{
	requires transitive java.desktop;
	
	exports frutty.entity;
	exports frutty.entity.zone;
	
	exports frutty.plugin;
	exports frutty.plugin.event;
	exports frutty.plugin.event.gui;
	
	exports frutty.sound;
	exports frutty.tools;
	
	exports frutty.world.zones;
	exports frutty.world.interfaces;
}