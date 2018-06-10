/**
 * Frutty Module for plugin development
 */
module frutty.api{
	requires java.desktop;
	
	exports frutty.entity;
	exports frutty.entity.zone;
	
	exports frutty.map;
	exports frutty.map.interfaces;
	exports frutty.map.zones;
	
	exports frutty.plugin;
	exports frutty.stuff;
}