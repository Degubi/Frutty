/**
 * Frutty Module for plugin development
 */
module frutty.api{
	requires java.desktop;
	requires java.instrument;
	
	exports frutty.entity;
	exports frutty.entity.zone;
	
	exports frutty.map.base;
	exports frutty.map.interfaces;
	exports frutty.map.zones;
	
	exports frutty.plugin;
}