module frutty.api{
	requires transitive java.desktop;
	
	exports frutty.entity;
	exports frutty.entity.zone;
	
	exports frutty.map.zones;
	exports frutty.map.interfaces;
	
	exports frutty.plugin;
	exports frutty.plugin.event;
	
	exports frutty.tools;
}