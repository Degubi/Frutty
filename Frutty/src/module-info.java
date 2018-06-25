module frutty.api{
	requires transitive java.desktop;
	
	exports frutty.plugin;
	exports frutty.plugin.event;
	
	exports frutty.entity;
	
	exports frutty.map;
	exports frutty.map.zones;
	exports frutty.map.interfaces;
	
	exports frutty.tools;
}